package com.msi.ibm.eyes;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;

import com.msi.ibm.eyes.IBMEyes;

public class RepeatingAlarmService extends BroadcastReceiver  {
	private LocationManager locationManager;
	SensorManager sm = null;
	
	
	private double locX;
	private double locY;
	private String err;
	LocationManager lm;
	private float[] mValues;
	private float[] aValues;
	public void onCreate(Context context){
		//sm = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
		//sm.registerListener(this, SensorManager.SENSOR_ORIENTATION ,SensorManager.SENSOR_DELAY_NORMAL);

	}
	protected void onStop() {
		//sm.unregisterListener(this);
		//super.onStop();
	}
	
	public void onReceive(Context context, Intent intent) {
		try{
			//onSensorChanged(SensorManager.SENSOR_ORIENTATION, mValues);
		locX = 0;
		locY = 1;
		StringBuilder sb = new StringBuilder("run Enabled Providers:");
		Log.d("ras", "Loc: "+sb);
		
		String out;
		try{
		//out=se.getLocPos();
		}catch(Exception e){ out="e"+e; }
		Toast.makeText(context, "It's Service Time!"
				+getPos(context)+"|o:"
				//+mValues[0]+"|o:"
				//+getAcl(context)
				, Toast.LENGTH_LONG).show();
		Log.d("!!!", "Loc: "+sb);
		Log.d("!", "Timed alarm onReceive() started at time: "
						//+ IBMEyes.posX
						+ "|setPos|"
						+ getPos(context)
						+ "|setPos|"
						//+ getAcl(context)
						//+ "|"
						+ new java.sql.Timestamp(System.currentTimeMillis())
								.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
		}
	

public String getPos(Context context) {
	String pos="";

		//posX = 45.046224;
	//	posY = 41.975043; // n
		
		try{
			
		String location_context = Context.LOCATION_SERVICE;
		pos=location_context;
		ServiceExample se = ServiceExample.getInstance();

		locationManager = (LocationManager) context.getSystemService(location_context);
		String myString = null;
		// TextView tvPos = (TextView) findViewById(R.id.posConsole);
		// TextView tvRes = (TextView) findViewById(R.id.resConsole);
		StringBuilder sb = new StringBuilder("Enabled Providers:");
		List<String> providers = locationManager.getProviders(true);
		for (String provider : providers) {
			locationManager.requestLocationUpdates(provider, 1000, 0,
					new LocationListener() {
						public void onLocationChanged(Location location) {
							
						}

						public void onProviderDisabled(String provider) {
						}

						public void onProviderEnabled(String provider) {
						}

						public void onStatusChanged(String provider,
								int status, Bundle extras) {
						}
					});
			sb.append("\n").append(provider).append(": ");
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				double lat = location.getLatitude();
				double lng = location.getLongitude();
				pos = lat +" "+ lng;
				//locY = lng;
				sb.append(lat).append(", ").append(lng);
				// get points of services
				// tvRes.setText("trying connect to ");

			} else {
			}
		}
		
	}catch(Exception e){
		
		pos+=e.toString();
		e.printStackTrace();
	}
		return pos;
	}

public String getAcl(Context context){
	float[] a =null;
	try{
		 //context.(new Intent(context, ServiceExample.class));
	sm = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
	//float[] outR = new float[3];
	//float[] f = null;
	//a =sm.getOrientation(null, mValues);
	}catch(Exception e){
		e.printStackTrace();
		
	}
	return "o:"+mValues[0]+","+mValues[1]+","+mValues[2]+""+a[0]+".";
}


public void onSensorChanged(int sensor, float[] values) {
	try{
		Log.d("!!!", "onSensorChanged");
	//synchronized (IBMEyes.this) {
		//Log.d(tag, "onSensorChanged: " + sensor + ", x: " + values[0] 					+ ", y: " + values[1] + ", z: " + values[2]);
		//debug
		//xViewA.setText(se.getPos());
		//getPos();
		//yViewA.setText("lgps:"+locX);
		mValues[0]=-1;
		mValues[1]=-1;
		mValues[2]=-1;
		if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
			aValues = values;
			// aValues[1]=values[1];
			// aValues[2]=values[2];
			//xViewA.setText("Accel X: " + values[0]);
			//yViewA.setText("Accel Y: " + values[1]);
			//zViewA.setText("Accel Z: " + values[2]);
		} else if (sensor == SensorManager.SENSOR_ORIENTATION) {
			mValues = values;
			aValues = values;
			// mValues[1]=values[1];
			// mValues[2]=values[2];
			//xViewO.setText("Orientation X: " + values[0]);
			//yViewO.setText("Orientation Y: " + values[1]);
			//zViewO.setText("Orientation Z: " + values[2]);
		}

		// for (int dum=1;dum<=100000;dum++){String dumstr=""+"+";}
		//ctime = new Date().getTime();
		//dlttime = ctime - ptime;
		//dvc_dlttime = ctime - dvc_ptime;

		// sendToWeb
		if (false) 
		//(prm_sendToWeb & (dlttime > 1000)& (state.equalsIgnoreCase("idle"))) {
			//ptime = ctime;
			try {
				//
				//getPos();
				Date cDate = new Date();
				// locX=777;
				// locY=777;
				// String sender
				// ="v="+cDate.toString()+"&lX="+aValues[0]+"&lY="+aValues[1]+
				// "&v0="+mValues[0]+"&v1="+mValues[1]+"&v2="+mValues[2];
				String sender = "lX=" + locX + "&lY=" + locY + "&v0="
						+ mValues[0] + "&v1=" + mValues[1] + "&v2="
						+ mValues[2] + "&a0=" + aValues[0] + "&a1="
						+ aValues[1] + "&a2=" + aValues[2];

				String urlStr = "http://92.63.96.27:8180/wm/wm_s?" + sender;

				URL myURL = new URL(urlStr);
				URLConnection ucon = myURL.openConnection();

				InputStream is = ucon.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);

				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}

				// inetView.setText("inet answ: " + new
				// String(baf.toByteArray()));
				//inetView.setText("inet answ: " + sender);
				// msg = new String(baf.toByteArray());
				// tvRes.setText("readed ");
				// for (int y=1 ;y<1000;y++){ msg+=""; }
			} catch (Exception e) {
				// myString = e.getMessage();
			}
		//}// sendToWeb
		//else 
			//if (prm_sendToDvc & (dvc_dlttime > 400)	& (state.equalsIgnoreCase("idle"))) {
				//dvc_ptime = ctime;
			//Random random = new Random();
			//if ((mValues[0] > 0)&(mValues[0] <90)) {	RedFlashLight();//GreenFlashLight();
			//} else if ((mValues[0] > 90)&(mValues[0] <180)) {GreenFlashLight();	//RedFlashLight();
			//} else if ((mValues[0] > 180)&(mValues[0] <270)) {	BlueFlashLight();	}

			// RedFlashLight();
			// ClearLED();
		//}
		// sendToDvc

	//}// synch
	}catch(Exception e){
		e.printStackTrace();
	}
}


public void onAccuracyChanged(int sensor, int accuracy) {
	// TODO Auto-generated method stub
	
}
}

