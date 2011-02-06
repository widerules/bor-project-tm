package com.msi.ibm.eyes;

//THIS IS CURRENTLY BEING OPTIMIZED

import java.util.Arrays;
import java.util.LinkedList;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.util.Log;
import java.util.logging.Level;


public class AudioSerialOutMono {
	// originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
	// and modified by Steve Pomeroy <steve@staticfree.info>
	// and further modified by mkb to make sure it can sit in its own object


	private  static Thread audiothread = null;
	private  static AudioTrack audiotrk = null;
	private  static byte generatedSnd[] = null;

	// set that can be edited externally
	public static int new_baudRate = 9600; // assumes N,8,1 right now
	public static int new_sampleRate = 4000; // min 4000 max 48000 
	public static int new_characterdelay = 0; // in audio frames, so depends 
	//on the sample rate. Useful to work with some microcontrollers.

	// set that is actually used
	private static int baudRate;
	private static int sampleRate;
	public static  int characterdelay = 0;

	public static LinkedList<byte[]> playque = new LinkedList<byte[]>();
	public static boolean active = false;

	public static void UpdateParameters(){
		baudRate = new_baudRate; // we're not forcing standard baud rates here specifically because we want to allow odd ones
		if (new_sampleRate > 48000)
			new_sampleRate = 48000;
		if (new_sampleRate < 4000)
			new_sampleRate = 4000;
		sampleRate = new_sampleRate; // min 4000 max 48000 
		if (new_characterdelay < 0)
			new_characterdelay = 0;
		characterdelay = new_characterdelay;
		minbufsize=AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_8BIT);
	}

	public static void output(String sendthis)
	{
		playque.add(SerialDAC(sendthis.getBytes()));
		audiothread.interrupt();
	}
	public static void output(byte[] sendthis)
	{
		playque.add(SerialDAC(sendthis));
		audiothread.interrupt();
	}

	public static byte[] SerialDAC(byte[] sendme)
	{
		int bytesinframe=10+characterdelay;
		int i=0; // counter 
		int j=0; // counter 
		int k=0; // counter 
		byte l=1; // intentional jitter used to prevent the DAC from flattening the waveform prematurely
		int m=0; // counter 
		final int n=sampleRate / baudRate;
		final byte logichigh = (byte) (-127+l);
		boolean[] bits = new boolean[sendme.length*bytesinframe];
		byte[] waveform = new byte[(sendme.length*bytesinframe*sampleRate / baudRate)]; // 8 bit, no parity, 1 stop
		byte[] waveform_temp = new byte[8];
		Arrays.fill(waveform, (byte) 0);
		Arrays.fill(waveform_temp, (byte) 0);
		Arrays.fill(bits, true); // slight opti to decide what to do with stop bits

		for (i=0;i<sendme.length;++i)
		{
			m=i*bytesinframe;
			bits[m]=false;
			bits[++m]=((sendme[i]&1)==0)?false:true;
			bits[++m]=((sendme[i]&2)==0)?false:true;
			bits[++m]=((sendme[i]&4)==0)?false:true;
			bits[++m]=((sendme[i]&8)==0)?false:true;
			bits[++m]=((sendme[i]&16)==0)?false:true;
			bits[++m]=((sendme[i]&32)==0)?false:true;
			bits[++m]=((sendme[i]&64)==0)?false:true;
			bits[++m]=((sendme[i]&128)==0)?false:true;
			// cheaper to prefill to true
			// now we need a stop bit, BUT we want to be able to add more (character delay) to play-nice with some microcontrollers such as the Picaxe or BS1 that need it in order to do decimal conversion natively.
//			for(k=0;k<bytesinframe-9;k++) 
//				bits[++m]=true;
		}

		for (i=0;i<bits.length;i++)
		{
			for (k=0;k<n;k++)
			{
				if (bits[i])
				{
					waveform[j]=  (byte) (logichigh+l); // the +l / -l is to fool the DAC into not having a flat waveform, which it might reject
					l=(byte) -l;
				}
				j++;
			}
		}
		bits=null;
		//waveform
		waveform_temp[0]=logichigh;
		//waveform_temp[1]=(byte) 0;
		//waveform_temp[2]=(byte) 0;
		//waveform_temp[3]=(byte) 0;
		//waveform_temp[4]=(byte) 255;
		waveform_temp[5]=logichigh;
		//waveform_temp[6]=(byte) 0;
		waveform_temp[7]=logichigh;
		String log_sdtr="lng:"+waveform.length+";";
		for (i=0;i<waveform.length;i++){
			if (Byte.toString(waveform[i])!="0")
			//if ((i<15)||(i>waveform.length-15))
			{
			log_sdtr+=";["+i+"]"+Byte.toString(waveform[i]);
			}
		}
		Log.d("AudSerOut", "ar_aso_"+log_sdtr);
		return waveform;
		
	}

	public static void activate() {
		UpdateParameters();
		// Use a new tread as this can take a while
		audiothread = new Thread(new Runnable() {
			public void run() {
				playSound();
			}
		}
		);
		audiothread.start();
		while(active == false)
		{
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static boolean isPlaying()
	{
		try{return audiotrk.getPlaybackHeadPosition() < (generatedSnd.length);}catch(Exception e){return false;}
	}

	static int minbufsize;
	static int length;

	private static void playSound(){
		active = true;
		while(active)
		{
			try {Thread.sleep(Long.MAX_VALUE);} catch (InterruptedException e) {
				while (playque.isEmpty() == false)
				{
					if (audiotrk != null)
					{
						if (generatedSnd != null)
						{
							while (audiotrk.getPlaybackHeadPosition() < (generatedSnd.length))
								SystemClock.sleep(50);  // let existing sample finish first: this can probably be set to a smarter number using the information above
						}
						audiotrk.release();
					}
					UpdateParameters(); // might as well do it at every iteration, it's cheap
					generatedSnd = playque.poll();
					length = generatedSnd.length;
					if (minbufsize<length)
						minbufsize=length;
					audiotrk = new AudioTrack(
							AudioManager.STREAM_MUSIC, 
							sampleRate, 
							AudioFormat.CHANNEL_CONFIGURATION_MONO,
							AudioFormat.ENCODING_PCM_8BIT, minbufsize,
							AudioTrack.MODE_STATIC);

					audiotrk.setStereoVolume(1,1);
					audiotrk.write(generatedSnd, 0, length); 
					audiotrk.play();
				}
			}
		}
	}
}
