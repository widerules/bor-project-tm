/*
 * 
 * IBMEyes.java
 * sample code for IBM Developerworks Article
 * Author: W. Frank Ableson
 * fableson@msiservices.com
 * 
 */
 
package com.msi.ibm.eyes;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.util.ByteArrayBuffer;

//import re.serialout.AudioSerialOutMono;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class IBMEyes extends Activity 
//implements SensorListener 
{
	public static String  BNNurl="http://192.168.0.101:8084";
	private LocationManager locationManager;

	final String tag = "IBMEyes";
	SensorManager sm = null;
	private long ctime;
	private long ptime;
	private long dlttime;
	private double locX;
	private double locY;
	public double posX;
	public double posY;

	// usefull permissions
	// getGps
	// sendToWeb
	// sendToDvc
	private boolean prm_sendToWeb;
	private boolean prm_getGps;
	private boolean prm_sendToDvc;

	// dvc
	private long dvc_ctime;
	private long dvc_ptime;
	private long dvc_dlttime;

	private String state = "idle";

	private float[] mValues;
	private float[] aValues;

	private Handler mCleanLedHandler = new Handler();

	TextView xViewA = null;
	TextView yViewA = null;
	TextView zViewA = null;
	TextView xViewO = null;
	TextView yViewO = null;
	TextView zViewO = null;
	TextView inetView = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// perm
		prm_sendToWeb = false;
		prm_getGps = false;
		prm_sendToDvc = false;

		ctime = new Date().getTime();
		ptime = ctime;

		AudioSerialOutMono.activate();
        Log.d(tag, "onClick: Starting service.");
        getIni();
        //startService(new Intent(this, ServiceExample.class));
        startService(new Intent(this, shakeServ.class));
        //break;
		
		dvc_ptime = ctime;
		dvc_ctime = ctime;

		sm = (SensorManager) getSystemService(SENSOR_SERVICE);
		setContentView(R.layout.main);
		xViewA = (TextView) findViewById(R.id.xbox);
		yViewA = (TextView) findViewById(R.id.ybox);
		zViewA = (TextView) findViewById(R.id.zbox);
		xViewO = (TextView) findViewById(R.id.xboxo);
		yViewO = (TextView) findViewById(R.id.yboxo);
		zViewO = (TextView) findViewById(R.id.zboxo);
		inetView = (TextView) findViewById(R.id.inetbox);
		//yViewA.setText("lgps:"+);
		
	}

	public static String getBNNurl(){
		return BNNurl;
	}
	public void getIni() {
		
		
		/*try

		{

			String filePath = "/sdcard/andipcam.log";
			BufferedReader in = new BufferedReader(new FileReader(filePath));
			String str = "";
			while ((str = in.readLine()) != null) {
				Log.d("IBME iniFile:", str);
			}

		} catch (Exception e)
		{
			e.printStackTrace();
			Log.d("IBME iniFile ERR!!!:", e.toString());
		}*/
		//-----------------
		try{
			 
			   File f = new File(Environment.getExternalStorageDirectory()+"/1.txt");
			 
			   FileInputStream fileIS = new FileInputStream(f);
			 
			   BufferedReader buf = new BufferedReader(new InputStreamReader(fileIS));
			 
			   String readString = new String();
			 
			   //just reading each line and pass it on the debugger
			 
			   while((readString = buf.readLine())!= null){
			 
			      Log.d("line: ", readString);
			 
			   }
			 
			} catch (FileNotFoundException e) {
			 
			   e.printStackTrace();
			 
			} catch (IOException e){
			 
			   e.printStackTrace();
			 
			}
			 
			 
	}

}