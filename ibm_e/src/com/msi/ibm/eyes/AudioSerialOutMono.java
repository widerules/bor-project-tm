package com.msi.ibm.eyes;
///!

//THIS IS CURRENTLY BEING OPTIMIZED

import java.util.Arrays;
import java.util.LinkedList;
import java.util.StringTokenizer;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.SystemClock;
import android.util.Log;
import java.util.logging.Level;

public class AudioSerialOutMono {
	// originally from
	// http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
	// and modified by Steve Pomeroy <steve@staticfree.info>
	// and further modified by mkb to make sure it can sit in its own object

	public static String outStr = "";

	private static Thread audiothread = null;
	private static AudioTrack audiotrk = null;
	private static byte generatedSnd[] = null;
	private static int newwave_l = 2000;;
	private static double[] newwave = new double[newwave_l];
	private final static byte generatedNewWave[] = new byte[2 * newwave_l];
	// set that can be edited externally
	// <<<<<<< .mine
	// public static int new_baudRate = 9600; // assumes N,8,1 right now
	// public static int new_sampleRate = 20000; // min 4000 max 48000
	// =======
	public static int new_baudRate = 1200; // assumes N,8,1 right now
	public static int new_sampleRate = 48000; // min 4000 max 48000
	// >>>>>>> .r29
	public static int new_characterdelay = 0; // in audio frames, so depends
	// on the sample rate. Useful to work with some microcontrollers.

	// set that is actually used
	private static int baudRate;
	private static int sampleRate;
	public static int characterdelay = 0;

	public static LinkedList<byte[]> playque = new LinkedList<byte[]>();
	public static boolean active = false;

	public static void UpdateParameters() {
		baudRate = new_baudRate; // we're not forcing standard baud rates here
		// specifically because we want to allow odd
		// ones
		if (new_sampleRate > 48000)
			new_sampleRate = 48000;
		if (new_sampleRate < 4000)
			new_sampleRate = 48000;
		sampleRate = new_sampleRate; // min 4000 max 48000
		if (new_characterdelay < 0)
			new_characterdelay = 0;
		characterdelay = new_characterdelay;
		minbufsize = AudioTrack.getMinBufferSize(sampleRate,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_8BIT);
	}

	public static void output(String sendthis) {
		// outStr=sendthis;
		playque.add(SerialDAC(sendthis.getBytes()));
		audiothread.interrupt();
	}

	public static void output(byte[] sendthis) {
		// outStr=sendthis.toString();
		playque.add(SerialDAC(sendthis));
		audiothread.interrupt();
	}

	public static byte[] SerialDAC(byte[] sendme) {
		int bytesinframe = 10 + characterdelay;
		int i = 0; // counter
		int j = 0; // counter
		int k = 0; // counter
		byte l = 1; // intentional jitter used to prevent the DAC from
		// flattening the waveform prematurely
		int m = 0; // counter
		final int n = sampleRate / baudRate;
		final byte logichigh = (byte) (-127 + l);
		boolean[] bits = new boolean[sendme.length * bytesinframe];
		byte[] waveform = new byte[(sendme.length * bytesinframe * sampleRate / baudRate)]; // 8
		// bit,
		// no
		// parity,
		// 1
		// stop
		byte[] waveform_temp = new byte[8];
		Arrays.fill(waveform, (byte) 0);
		Arrays.fill(waveform_temp, (byte) 0);
		Arrays.fill(bits, true); // slight opti to decide what to do with stop
		// bits

		for (i = 0; i < sendme.length; ++i) {
			m = i * bytesinframe;
			bits[m] = false;
			bits[++m] = ((sendme[i] & 1) == 0) ? false : true;
			bits[++m] = ((sendme[i] & 2) == 0) ? false : true;
			bits[++m] = ((sendme[i] & 4) == 0) ? false : true;
			bits[++m] = ((sendme[i] & 8) == 0) ? false : true;
			bits[++m] = ((sendme[i] & 16) == 0) ? false : true;
			bits[++m] = ((sendme[i] & 32) == 0) ? false : true;
			bits[++m] = ((sendme[i] & 64) == 0) ? false : true;
			bits[++m] = ((sendme[i] & 128) == 0) ? false : true;
			// cheaper to prefill to true
			// now we need a stop bit, BUT we want to be able to add more
			// (character delay) to play-nice with some microcontrollers such as
			// the Picaxe or BS1 that need it in order to do decimal conversion
			// natively.
			// for(k=0;k<bytesinframe-9;k++)
			// bits[++m]=true;
		}

		for (i = 0; i < bits.length; i++) {
			for (k = 0; k < n; k++) {
				if (bits[i]) {
					waveform[j] = (byte) (logichigh + l); // the +l / -l is to
					// fool the DAC into
					// not having a flat
					// waveform, which
					// it might reject
					l = (byte) -l;
				}
				j++;
			}
		}
		bits = null;
		// waveform
		waveform_temp[0] = logichigh;
		// waveform_temp[1]=(byte) 0;
		// waveform_temp[2]=(byte) 0;
		// waveform_temp[3]=(byte) 0;
		// waveform_temp[4]=(byte) 255;
		waveform_temp[5] = logichigh;
		// waveform_temp[6]=(byte) 0;
		waveform_temp[7] = logichigh;
		String log_sdtr = "lng:" + waveform.length + ";";
		for (i = 0; i < waveform.length; i++) {
			if (Byte.toString(waveform[i]) != "0")
			// if ((i<15)||(i>waveform.length-15))
			{
				log_sdtr += ";[" + i + "]" + Byte.toString(waveform[i]);
			}
		}
		Log.d("ar__ASOM", "[SerialDAC]waveform.length:" + waveform.length
				+ ";waveform:" + log_sdtr);

		return waveform;

	}

	public static void activate() {
		UpdateParameters();
		// Use a new tread as this can take a while
		audiothread = new Thread(new Runnable() {
			public void run() {
				playSound();
			}
		});
		audiothread.start();
		while (active == false) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static boolean isPlaying() {
		try {
			return audiotrk.getPlaybackHeadPosition() < (generatedSnd.length);
		} catch (Exception e) {
			return false;
		}
	}

	static int minbufsize;
	static int length;

	private static void playSound() {
		active = true;
		while (active) {
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				while (playque.isEmpty() == false) {
					if (audiotrk != null) {
						if (generatedSnd != null) {
							while (audiotrk.getPlaybackHeadPosition() < (generatedSnd.length))
								SystemClock.sleep(50); // let existing sample
							// finish first: this
							// can probably be set
							// to a smarter number
							// using the information
							// above
						}
						audiotrk.release();
					}
					UpdateParameters(); // might as well do it at every
					// iteration, it's cheap
					generatedSnd = playque.poll();
					length = generatedSnd.length;
					if (minbufsize < length)
						minbufsize = length;
					audiotrk = new AudioTrack(AudioManager.STREAM_MUSIC,
							// sampleRate,
							10000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
							AudioFormat.ENCODING_PCM_16BIT,
							// minbufsize,
							// newwave_l,
							newwave_l, AudioTrack.MODE_STATIC);

					audiotrk.setStereoVolume(1, 1);
					// <<<<<<< .mine

					// debug
					/*
					 * byte wave=-125; for (int i=0; i<generatedSnd.length;
					 * i++){ if ((i>=0)&&(i<100)){ generatedSnd[i]=0; } if
					 * ((i>=100)&&(i<2000)){if
					 * (wave==-125){wave=-127;}else{wave=-125;};
					 * generatedSnd[i]=wave; } if ((i>=2000)&&(i<3000)){
					 * generatedSnd[i]=0; } if ((i>=3000)&&(i<3500)){
					 * generatedSnd[i]=0; if
					 * (wave==-125){wave=-127;}else{wave=-125;};
					 * generatedSnd[i]=wave; } if ((i>=3500)&&(i<4400)){
					 * generatedSnd[i]=0; } }
					 */

					// =======
					// debug
					byte neg = 1;
					byte pos = -1;
					byte zer = 0;
					int p0 = 0;// idle
					int p1 = 0;// idle
					int p2 = 0;// play
					int p3 = 0;// idle
					int p4 = 0;// play
					int p5 = 0;// idle
					int p6 = 0;// play
					int p7 = 0;// play
					
					int p = 50; // pause
					int s1 = 500;// sigs
					int s2 = 100;
					int s3 = 10;
					// int state = 0;

					p4 = newwave_l;// play

					
					// compas
					// if (outStr.equalsIgnoreCase("toF")){p2=500;}// 23 - 73 -
					// 120 170 1000~50ms
					// if (outStr.equalsIgnoreCase("toR")){p2=1000;}
					// if (outStr.equalsIgnoreCase("toB")){p2=1500;}
					// if (outStr.equalsIgnoreCase("toL")){p2=2000;}

				    StringTokenizer st = new StringTokenizer(outStr,"|");
				    int j=0;
				    String oS[]=new String[10];
				    
				     while (st.hasMoreTokens()) {
				         oS[j]=st.nextToken();
				         j++;
				     }
					
					//if (oS[0].equalsIgnoreCase("-2")) {
					//	s1 = 200;
					//}// 23 - 73 - 120 170 1000~50ms
					if (oS[0].equalsIgnoreCase("-1")) {
						s1 = 50;
					}
					if (oS[0].equalsIgnoreCase("0")) {
						s1 = 100;
					}
					if (oS[0].equalsIgnoreCase("1")) {
						s1 = 150;
					}
					if (oS[0].equalsIgnoreCase("2")) {
						s1 = 200;
					}
					//if (oS[0].equalsIgnoreCase("2")) {
					//	s1 = 1000;	
					//}
					if (oS[1].equalsIgnoreCase("1")) {
						s2 = 50;
					}
					if (oS[1].equalsIgnoreCase("2")) {
						s2 = 100;
					}
					if (oS[1].equalsIgnoreCase("3")) {
						s2 = 150;
					}

					p0 = 0;
					p1 = p;
					p2 = p + s1;
					p3 = 2 * p + s1;
					p4 = 2 * p + s1 + s2;
					p5 = 3 * p + s1 + s2;
					p6 = 3 * p + s1 + s2 + s3;
					p7 = 4 * p + s1 + s2 + s3;

					outStr +=":"+p0 + "." + 
						p1 + "." + p2 + "." + p3 + "." + p4
							+ "." + p5 + "." + p6 + "." + p7 + ".";

					int n = 0;
					for (int i = 0; i < newwave_l; ++i) {
						// sample[i] = Math.sin(2 * Math.PI * i /
						// (sampleRate/freqOfTone));
						newwave[i] = Math.sin(2 * Math.PI * i / (newwave_l));

						//if ((i >= p0) && (i < p1)) {
						//	n = 1;
						//}
						if ((i >= p1) && (i < p2)) {
							if (n == 1) {
								n = -1;
							} else {
								n = 1;
							}
						}
						if ((i >= p2) && (i < p3)) {
							n = 1;
						}
						if ((i >= p3) && (i < p4)) {
							if (n == 1) {
								n = -1;
							} else {
								n = 1;
							}
						}
						if ((i >= p4) && (i < p5)) {
							n = 1;
						}
						if ((i >= p5) && (i < p6)) {
							if (n == 1) {
								n = -1;
							} else {
								n = 1;
							}
						}
						if ((i >= p6)) {
							n = 1;
						}
						newwave[i] = n;
						// sample[i]=1;
						// if
						// ((i>2997)&&(i<3005)){Log.d("","ar_"+i+":"+sample[i]);};
					}

					Log.d("", "ar_sig:" + outStr + ";");
					// >>>>>>> .r29

					int idx = 0;
					for (final double dVal : newwave) {
						// scale to maximum amplitude
						final short val = (short) ((dVal * 32767));
						// in 16 bit wav PCM, first byte is the low order byte
						generatedNewWave[idx++] = (byte) (val & 0x00ff);
						generatedNewWave[idx++] = (byte) ((val & 0xff00) >>> 8);

					}

					// audiotrk.write(generatedSnd, 0, length);
					//audiotrk.write(generatedNewWave, 0, newwave_l);
					audiotrk.write(generatedNewWave, 0, 1000);
					// debug
					/*
					 * String log_sdtr="lng:"+generatedSnd.length+";"; for (int
					 * i=0;i<generatedSnd.length;i++){ if
					 * (Byte.toString(generatedSnd[i])!="0") //if
					 * ((i<15)||(i>waveform.length-15)) {
					 * log_sdtr+=";["+i+"]"+Byte.toString(generatedSnd[i]); } }
					 */
					String log_sdtr = "lng:" + newwave.length + ";";
					for (int i = 0; i < newwave.length; i++) {
						if (newwave[i] != zer)
						// if ((i<15)||(i>waveform.length-15))
						{
							// log_sdtr+=";["+i+"]"+Byte.toString(newwave[i]);
						}
					}

					// Log.d("ar__ASOM","[playSound]generatedSnd.length:"+generatedSnd.length+";generatedSnd:"+log_sdtr);
					Log.d("ar__ASOM", "[playSound]newwave.length:"
							+ newwave.length + ";newwave:" + log_sdtr);
					audiotrk.play();
					Log.d("", "ar_0.00035" + ":");
				}
			}
		}
	}

	private static void playSoundE() {
		int duration = 3; // seconds
		int sampleRate = 8000;
		int numSamples = duration * sampleRate;
		double sample[] = new double[numSamples];
		double freqOfTone = 440; // hz
		byte generatedSnd[] = new byte[2 * numSamples];

	}

}
