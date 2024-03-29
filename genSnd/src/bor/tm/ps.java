package bor.tm;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class ps extends Activity  {
    // originally from http://marblemice.blogspot.com/2010/04/generate-and-play-tone-in-android.html
    // and modified by Steve Pomeroy <steve@staticfree.info>
    private final int duration = 1; // seconds
    private final int sampleRate = 20000;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double freqOfTone = 10440; // hz

    private final byte generatedSnd[] = new byte[2 * numSamples];

    Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
 
    @Override
    protected void onResume() {
        super.onResume();

        // Use a new tread as this can take a while
        final Thread thread = new Thread(new Runnable() {
            public void run() {
                genTone();
                handler.post(new Runnable() {

                    public void run() {
                        playSound();
                    }
                });
            }
        });
        thread.start();
    }

    void genTone(){
    	int n=1;
        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            //sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate));
            if ((i>=0)&&(i<500)){n=1;}
            if ((i>=500)&&(i<1510)){
                //if (n==0) { Log.d("","ar_"+i+":Start"); }
            	if (n==1) {n=-1;}else{n=1;}
            }
           if ((i>=1510)&&(i<5000)){
               //if (n==1) { Log.d("","ar_"+i+":Stop"); }
               //if (n==-1) { Log.d("","ar_"+i+":Stop"); }
           	n=1;
           	}
            if ((i>=5000)&&(i<7500)){
                //if (n==0) { Log.d("","ar_"+i+":Start"); }
            	if (n==1) {n=-1;}else{n=1;}}
            if (i>=7500){
                //if (n==1) { Log.d("","ar_"+i+":Stop"); }
                //if (n==-1) { Log.d("","ar_"+i+":Stop"); }
            	n=1;
            }
            sample[i]=n;
            //sample[i]=1;
            //if ((i>2997)&&(i<3005)){Log.d("","ar_"+i+":"+sample[i]);};
        }
        Log.d("","ar_ver 0.0014");
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        
    }

    void playSound(){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                sampleRate, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, numSamples,
                AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, numSamples);
        audioTrack.play();
    }
}
