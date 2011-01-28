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
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.http.util.ByteArrayBuffer;

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
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class IBMEyes extends Activity implements SensorListener {
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

		
        Log.d(tag, "onClick: Starting service.");
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

	//ServiceExample se = new ServiceExample();
	public void onSensorChanged(int sensor, float[] values) {
		synchronized (IBMEyes.this) {
			//Log.d(tag, "onSensorChanged: " + sensor + ", x: " + values[0] 					+ ", y: " + values[1] + ", z: " + values[2]);
			//debug
			//xViewA.setText(se.getPos());
			//getPos();
			yViewA.setText("lgps:"+locX);

	        Intent intent = getIntent();
	        String path = intent.getStringExtra("com.example.android.apis.Path");

			
			if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
				aValues = values;
				// aValues[1]=values[1];
				// aValues[2]=values[2];
				xViewA.setText("Accel X: " + values[0]);
				yViewA.setText("Accel Y: " + values[1]);
				zViewA.setText("Accel Z: " + values[2]);
			} else if (sensor == SensorManager.SENSOR_ORIENTATION) {
				mValues = values;
				aValues = values;
				// mValues[1]=values[1];
				// mValues[2]=values[2];
				xViewO.setText("Orientation X: " + values[0]);
				yViewO.setText("Orientation Y: " + values[1]);
				zViewO.setText("Orientation Z: " + values[2]);
			}

			// for (int dum=1;dum<=100000;dum++){String dumstr=""+"+";}
			ctime = new Date().getTime();
			dlttime = ctime - ptime;
			dvc_dlttime = ctime - dvc_ptime;

			// sendToWeb
			if (prm_sendToWeb & (dlttime > 1000)
					& (state.equalsIgnoreCase("idle"))) {
				ptime = ctime;
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
					inetView.setText("inet answ: " + sender);
					// msg = new String(baf.toByteArray());
					// tvRes.setText("readed ");
					// for (int y=1 ;y<1000;y++){ msg+=""; }
				} catch (Exception e) {
					// myString = e.getMessage();
				}
			}// sendToWeb
			//else 
				if (prm_sendToDvc & (dvc_dlttime > 400)
					& (state.equalsIgnoreCase("idle"))) {
					dvc_ptime = ctime;
				Random random = new Random();
				if ((mValues[0] > 0)&(mValues[0] <90)) {
					RedFlashLight();
					//GreenFlashLight();
				} else if ((mValues[0] > 90)&(mValues[0] <180)) {
					GreenFlashLight();
					//RedFlashLight();
				} else if ((mValues[0] > 180)&(mValues[0] <270)) {
					BlueFlashLight();
				}

				// RedFlashLight();
				// ClearLED();
			}
			// sendToDvc

		}// synch
	}

	private void ClearLED() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(0);
	}

	private Runnable mClearLED_Task = new Runnable() {
		public void run() {
			synchronized (IBMEyes.this) {
				ClearLED();
			}
		}
	};

	private void RedFlashLight() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification();
		notif.ledARGB = 0xFFff0000;
		notif.flags = Notification.FLAG_SHOW_LIGHTS;
		notif.ledOnMS = 1000;
		//notif.ledOffMS = 100;
		nm.notify(0, notif);
		// Program the end of the light :
	}

	private void GreenFlashLight() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification();
		notif.ledARGB = 0xFFf0000;
		notif.flags = Notification.FLAG_SHOW_LIGHTS;
		notif.ledOnMS = 2000;
		//notif.ledOffMS = 100;
		nm.notify(0, notif);
		// Program the end of the light :
		mCleanLedHandler.postDelayed(mClearLED_Task, 150);
	}

	private void BlueFlashLight() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification();
		notif.ledARGB = 0xFFff0000;
		notif.flags = Notification.FLAG_SHOW_LIGHTS;
		notif.ledOnMS = 3000;
		//notif.ledOffMS = 100;
		nm.notify(0, notif);
		// Program the end of the light :
		mCleanLedHandler.postDelayed(mClearLED_Task, 250);
	}

	public void onAccuracyChanged(int sensor, int accuracy) {
		//Log.d(tag, "onAccuracyChanged: " + sensor + ", accuracy: " + accuracy);

	}

	@Override
	protected void onResume() {
		super.onResume();
		// sm.registerListener(this, SensorManager.SENSOR_ORIENTATION |
		// SensorManager.SENSOR_ACCELEROMETER,SensorManager.SENSOR_DELAY_NORMAL);
		sm.registerListener(this, SensorManager.SENSOR_ORIENTATION ,SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	protected void onStop() {
		sm.unregisterListener(this);
		super.onStop();
	}
	public void getPos() {
		

		posX = 45.046224;
		posY = 41.975043; // n

		String location_context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) this.getSystemService(location_context);
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
				locX = lat;
				locY = lng;
				sb.append(lat).append(", ").append(lng);
				// get points of services
				// tvRes.setText("trying connect to ");

			} else {
			}
		}

	}//getPosÿÿÿ
	

}