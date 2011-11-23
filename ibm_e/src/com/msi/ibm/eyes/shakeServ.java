package com.msi.ibm.eyes;

/*
 * ?A) Podrobno danniye o sostoyanii Andro to server
 * 		!A.a)	otpravka pervichnih dannih na server
 * 		?A.b)	opredelit' periodichnost' svyazi s serverom - otdelit' ot posilki command na ardu
 * 
 * ?B) Upravleniye zadaniyami s servera
 * 		!B.a)	commanda na stranize otpravki dannih(/wm_s)
 * 		!B.b)	upravlenie(/cmnd) commandoy na stranize otpravki dannih
 * 		?B.c)	krasivoye 	upravlenie(/cmnd) commandoy na stranize otpravki dannih(rul')
 * 
 * ?C) Algorim upravlenia
 * 		!C.a)	ispolzovat' uglovuyu skorost'
 * 		!C.b)	izmenyat' uglovuyu skorost' ot ugla
 * 		?C.c)	izmenyat' impuls kak func(ugla) - dlitel'nost' raboti(p=Ft) 	
 *  
 *  
 * */
import com.msi.ibm.eyes.operationModule;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;

//import re.serialout.AudioSerialOutMono;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder; //import android.os.PowerManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.Toast;

public class shakeServ extends Service implements SensorEventListener {

	// logger to ext column
	public static String dirc_log; // current direction
	public static String dirT_log; // target direction
	public static String dirTc_log; // current target direction(ati)
	public static String wc_log; // current angle speed
	public static String wmax_log;
	public static String ctime_log; // current timestamp
	public static String F_log; // F -1 0 1
	public static String Fval_log; // value of F

	// control
	public static String An_dirT_cntrl;
	public static String An_wmax_cntrl;
	public static String An_daz_cntrl;
	// public static String An_Tweb_cntrl;
	public static String An_TWebLog_cntrl;
	public static String An_TWebCntrl_cntrl;
	public static String An_TSens_cntrl;
	public static String An_TArdu_cntrl;

	// accel for local tracking
	public static double aclTrk_ar[][] = new double[100000][3];// 0x 1y 2z
	public static long aclTrk_arCount = 0;
	public static double acl_x = 0;
	public static double acl_y = 0;
	public static double acl_z = 0;
	public static double acl_vx = 0;
	public static double acl_vy = 0;
	public static double acl_vz = 0;
	public static double acl_ax = 0;
	public static double acl_ay = 0;
	public static double acl_az = 0;
	public static double acl_ex = 0;
	public static double acl_ey = 0;
	public static double acl_ez = 0;
	public static long acl_pt = 0;
	public static double acl_pax = 0;
	public static double acl_pay = 0;
	public static double acl_paz = 0;
	public static double acl_dt = 0;

	private LocationManager locationManager;

	final Handler uiThreadCallback = new Handler();
	//public static final String LoginServiceUri = "http://92.63.96.27:8180/wm";
	public static final String LoginServiceUri = com.msi.ibm.eyes.IBMEyes.getBNNurl();
	

	private static final int FORCE_THRESHOLD = 2000;
	// <<<<<<< .mine
	private static final int TIME_THRESHOLD = 15000;
	// =======
	private static final int web_THRESHOLD = 5000;
	private static final int SENSOR_THRESHOLD = 1500;
	// >>>>>>> .r79
	private static final int TIME_TRESHOLD_AUDIO = 1300;
	private static final int SHAKE_TIMEOUT = 1500;
	private static final String baudRate = "1200";// speed of data transfer
	private static final int SHAKE_DURATION = 1000;
	private static final int SHAKE_COUNT = 3;
	private static final int NetworkConnectionTimeout_ms = 1500;

	// private SensorManager mSensorMgr;
	private float mLastX = -1.0f, mLastY = -1.0f, mLastZ = -1.0f;
	private long mLastTime;
	private long dLastTime;
	// private OnShakeListener mShakeListener;
	private Context mContext;
	private int mShakeCount = 0;
	private long mLastShake;
	private long mLastForce;
	private static Boolean taskStarted = false;

	private Handler mCleanLedHandler = new Handler();

	// private static PowerManager.WakeLock myWakeLock = null;

	Handler serviceHandler;
	Task myTask = new Task();

	// ----------------------------------------bruno's tutorial stuff
	SensorManager mSensorEventManager;

	Sensor mSensor;

	// BroadcastReceiver for handling ACTION_SCREEN_OFF.
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Check action just to be on the safe side.
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				// Unregisters the listener and registers it again.
				mSensorEventManager.unregisterListener(shakeServ.this);
				mSensorEventManager.registerListener(shakeServ.this, mSensor,
				// SensorManager.SENSOR_DELAY_NORMAL);
						SensorManager.SENSOR_DELAY_GAME);
			}
		}
	};

	public static String locX;

	public static String locY;

	public static double locXe;
	public static double locYe;

	public static double dir0;
	public static double dir1;
	public static double dir2;

	public static String webDataAr[] = new String[100]; // array of data from
	// web
	public static long webDataArTm[] = new long[100]; // array time of request
	// data from web
	public static int webDataCount = 0; // curren counter value for webData
	// array

	public float[] aValues;
	public float[] mValues;

	@Override
	public void onCreate() {
		AudioSerialOutMono.activate();
		for (int i = 0; i < 100; i++) {
			webDataAr[i] = "0|0";
			webDataArTm[i] = System.currentTimeMillis();
		}

		float[] mValues = null;
		float[] aValues = null;
		super.onCreate();
		Log.d("shake service startup", "registering for shake");
		Log.d("!", "onCreate(..)");
		mContext = getApplicationContext();
		// Obtain a reference to system-wide sensor event manager.
		mSensorEventManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);

		// Get the default sensor for accel
		mSensor = mSensorEventManager
				//.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		 .getDefaultSensor(Sensor.TYPE_ORIENTATION);

		// Register for events.
		mSensorEventManager.registerListener(this, mSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
		// TODO I'll only register at screen off. I don't have a use for shake
		// while not in sleep (yet)

		// Register our receiver for the ACTION_SCREEN_OFF action. This will
		// make our receiver
		// code be called whenever the phone enters standby mode.
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);

		serviceHandler = new Handler();
	}

	@Override
	public void onDestroy() {
		// Unregister our receiver.
		unregisterReceiver(mReceiver);

		serviceHandler.removeCallbacks(myTask);
		serviceHandler = null;

		// Unregister from SensorManager.
		mSensorEventManager.unregisterListener(this);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't need a IBinder interface.
		return null;
	}

	// -------------end of the tutorial besides the accuracy and sensor change
	// stubs

	public void onShake() {
		// Poke a user activity to cause wake?
		Log.d("onShake", "doing wakeup");
		// PowerManager myPM = (PowerManager)
		// getApplicationContext().getSystemService(Context.POWER_SERVICE);
		// myPM.userActivity(SystemClock.uptimeMillis(), false);

		int flags;

		// flags = PowerManager.SCREEN_DIM_WAKE_LOCK;
		// flags |= PowerManager.ON_AFTER_RELEASE;
		// flags |= PowerManager.ACQUIRE_CAUSES_WAKEUP;

		// myWakeLock = myPM.newWakeLock(flags, "acquire");

		// myWakeLock.setReferenceCounted(false);
		// myWakeLock.acquire();

		serviceHandler.postDelayed(myTask, 4000);

	}

	class Task implements Runnable {
		public void run() {
			// myWakeLock.release();
			// myWakeLock = null;
		}
	}

	// begin code from putnaar --- we don't need any of the interface or the
	// registration
	// before it was all activity based, now the service can take all the
	// actions. Just adapted the listener to the onShake method

	/*
	 * public interface OnShakeListener { public void onShake(); } public
	 * shakeServ(Context context) { mContext = context; resume(); } public void
	 * setOnShakeListener(OnShakeListener listener) { mShakeListener = listener;
	 * }
	 * 
	 * public void resume() { mSensorMgr =
	 * (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE); if
	 * (mSensorMgr == null) { throw new
	 * UnsupportedOperationException("Sensors not supported"); } boolean
	 * supported = mSensorMgr.registerListener((SensorEventListener) this,
	 * mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
	 * SensorManager.SENSOR_DELAY_GAME);
	 * 
	 * 
	 * 
	 * if (!supported) { mSensorMgr.unregisterListener(this); throw new
	 * UnsupportedOperationException("Accelerometer not supported"); } }
	 * 
	 * public void pause() { if (mSensorMgr != null) {
	 * mSensorMgr.unregisterListener(this); mSensorMgr = null; } }
	 */

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// not used right now
	}

	// Used to decide if it is a shake
	public void onSensorChanged(SensorEvent event) {

		// if(event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			long now = System.currentTimeMillis();
			netxAccl(event.values[SensorManager.DATA_X],
					event.values[SensorManager.DATA_Y],
					event.values[SensorManager.DATA_Z], now);
			// if ((now - mLastForce) > SHAKE_TIMEOUT) {
			// mShakeCount = 0;
			// }

			if ((now - mLastTime) > SENSOR_THRESHOLD) {
				Log.d("onShake", "ar_doing accele:" + "x "
						+ event.values[SensorManager.DATA_X] + ";y "
						+ event.values[SensorManager.DATA_Y] + ";z "
						+ event.values[SensorManager.DATA_Z]);
				//Log.d("accl_offset", "accl_offset 1 x:" + acl_x + ";vx:" + acl_vx+ ";ax:" + acl_ax + ";ex:" + acl_ex + ";dt:" + acl_dt + ";");
				//Log.d("accl_offset", "accl_offset 2 y:" + acl_y + ";vy:" + acl_vy+ ";ay:" + acl_ay + ";ey:" + acl_ey + ";dt:" + acl_dt + ";");
				//Log.d("accl_offset", "accl_offset 3 z:" + acl_z + ";vz:" + acl_vz+ ";az:" + acl_az + ";ez:" + acl_ez + ";dt:" + acl_dt + ";");
				//Log.d("accl_offset", "accl_offset 4 x:" + acl_x + ";y:" + acl_y
				//		+ ";z:" + acl_z + ";cnt:" + aclTrk_arCount + ";pt:"
				//		+ acl_pt);

				/*
				 * long diff = now - mLastTime; float speed =
				 * Math.abs(event.values[SensorManager.DATA_X] +
				 * event.values[SensorManager.DATA_Y] +
				 * event.values[SensorManager.DATA_Z] - mLastX - mLastY -
				 * mLastZ) / diff * 10000; if (speed > FORCE_THRESHOLD) { if
				 * ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake >
				 * SHAKE_DURATION)) { mLastShake = now; mShakeCount = 0; // if
				 * (mShakeListener != null) { // mShakeListener.onShake(); // }
				 * //call the reaction you want to have happen onShake(); }
				 * mLastForce = now; } mLastTime = now; mLastX =
				 * event.values[SensorManager.DATA_X]; mLastY =
				 * event.values[SensorManager.DATA_Y]; mLastZ =
				 * event.values[SensorManager.DATA_Z];
				 */
			}
		}
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			long now = System.currentTimeMillis();

			// if ((now - mLastForce) > SHAKE_TIMEOUT) {
			// mShakeCount = 0;
			// }
			if ((now - dLastTime) > SENSOR_THRESHOLD) {
				dLastTime = now;
				mValues = event.values;
				dir0 = mValues[0];
				dir1 = mValues[1];
				dir2 = mValues[2];
				mValues = event.values;
				aValues = event.values;
				dir0 = mValues[0];
				dir1 = mValues[1];
				dir2 = mValues[2];
				// Log.d("onShake", "ar_doing orient:" + "x "
				// + event.values[SensorManager.DATA_X] + ";y "
				// + event.values[SensorManager.DATA_Y] + ";z "
				// + event.values[SensorManager.DATA_Z]);
				Log.d("onShake", "ar_doing orient:" + "x " + dir0 + ";y "
						+ dir1 + ";z " + dir2);

				if (!getTaskStarted()) {
					double tmpX = 0;
					double tmpY = 0;
					double tmpZ = 0;
					int tmpDir = 0;
					int tmpTE = 5000;
					Boolean tmpTC = false;
					// <<<<<<< .mine
					try {
						// tmpDir = Integer.parseInt(webDataAr[0]);
						// tmpTE=Integer.parseInt(webDataAr[1]);

						StringTokenizer parser = new StringTokenizer(
								webDataAr[0], "|");
						String tmpStr = "";
						int i = 0;
						while (parser.hasMoreTokens()) {
							i++;
							tmpStr = parser.nextToken();
							if (i == 1) {
								tmpDir = Integer.parseInt(tmpStr);
							}
							if (i == 2) {
								tmpTE = Integer.parseInt(tmpStr);
								;
							}
						}

						// tmpDir = Integer.parseInt(webDataAr[0]);
						// tmpTE = Integer.parseInt(webDataAr[1]);
						// debug
						// tmpDir++;
						// if (tmpDir>360){tmpDir=0;}
						// webDataAr[0]=tmpDir+"";
					} catch (Exception e) {
					}

					Log.d("", "shS_oSCH_:addTask() x:" + tmpX + ";y:" + tmpY
							+ ";z:" + tmpZ + ";dir:" + tmpDir + ";te:" + tmpTE
							+ ";tc:" + tmpTC);
					operationModule.addTask(tmpX, tmpY, tmpZ, tmpDir, tmpTE,
							tmpTC);
					Log.d("", "shS_oSCH_:addTask() ended;");
					setTaskStarted(true);
					Log.d("", "shS_oSCH_:nextTask();taskStarted:"
							+ getTaskStarted());
					operationModule.nextTask();
					Log.d("", "shS_oSCH_:nextTask() ended;");
				} else {
					Log.d("", "shS_oSCH_: start go ;");
					operationModule.go();
					Log.d("", "shS_oSCH_: back from go ;");
				}
				String posi = "";
				posi = getPos();

				Log.d("onShake", "doing positi:" + posi + ";");

				StringTokenizer parser = new StringTokenizer(posi, " ");
				String tmpStr = "";
				int i = 0;
				while (parser.hasMoreTokens()) {
					i++;
					tmpStr = parser.nextToken();
					if (i == 1) {
						locX = tmpStr;
					}
					if (i == 2) {
						locY = tmpStr;
					}
				}

				// debug
				// if ((mValues[0] < 90)&&(mValues[0] > 0)) { toRight("toR"); }
				// if ((mValues[0] < 180)&&(mValues[0] > 90)) { toRight("toF");
				// }
				// if ((mValues[0] < 270)&&(mValues[0] > 180)) { toRight("toL");
				// }
				// if ((mValues[0] < 360)&&(mValues[0] > 270)) { toRight("toB");
				// }
				// debug
				// if (webDataAr[0].equalsIgnoreCase("120|5000")){webDataAr[0] =
				// "120|0";}
			}

			if ((now - mLastTime) > web_THRESHOLD) {

				mLastTime = now;

				new Thread() {
					@Override
					public void run() {
						webSender();
						// _doInBackgroundPost();
						// uiThreadCallback.post(runInUIThread);
					}
				}.start();

				/*
				 * ) long diff = now - mLastTime; float speed =
				 * Math.abs(event.values[SensorManager.DATA_X] +
				 * event.values[SensorManager.DATA_Y] +
				 * event.values[SensorManager.DATA_Z] - mLastX - mLastY -
				 * mLastZ) / diff * 10000; if (speed > FORCE_THRESHOLD) { if
				 * ((++mShakeCount >= SHAKE_COUNT) && (now - mLastShake >
				 * SHAKE_DURATION)) { mLastShake = now; mShakeCount = 0; // if
				 * (mShakeListener != null) { // mShakeListener.onShake(); // }
				 * //call the reaction you want to have happen onShake(); }
				 * mLastForce = now; } mLastTime = now; mLastX =
				 * event.values[SensorManager.DATA_X]; mLastY =
				 * event.values[SensorManager.DATA_Y]; mLastZ =
				 * event.values[SensorManager.DATA_Z];
				 */
			}
		}

	}

	public String getPos() {
		String pos = "";

		// posX = 45.046224;
		// posY = 41.975043; // n

		try {

			String location_context = Context.LOCATION_SERVICE;
			pos = location_context;
			ServiceExample se = ServiceExample.getInstance();

			locationManager = (LocationManager) getSystemService(location_context);
			String myString = null;
			// TextView tvPos = (TextView) findViewById(R.id.posConsole);
			// TextView tvRes = (TextView) findViewById(R.id.resConsole);
			StringBuilder sb = new StringBuilder("Enabled Providers:");
			List<String> providers = locationManager.getProviders(true);
			for (String provider : providers) {
				locationManager.requestLocationUpdates(provider, 500, 0,
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
				Location location = locationManager
						.getLastKnownLocation(provider);
				if (location != null) {
					double lat = location.getLatitude();
					double lng = location.getLongitude();
					locXe = lat;
					locYe = lng;
					pos = lat + " " + lng;
					// locY = lng;
					sb.append(lat).append(", ").append(lng);
					// get points of services
					// tvRes.setText("trying connect to ");

				} else {
				}
			}

		} catch (Exception e) {

			pos += e.toString();
			e.printStackTrace();
		}
		return pos;
	}

	private int battLevel = -1;
	private Date battDate = new Date();

	private void batLevel(Context context, Intent intent) {

		int scale = intent.getIntExtra("level", -1);
		battLevel = scale;

	}

	private void webSender() {
		Log.d("wSndr", "background task - start");

		try {
			// debug
			// webDataAr[0] = "120|5000";

			Date cDate = new Date();
			// int tmpInt = Integer.parseInt(webDataAr[0])+1;
			// if (tmpInt>360){tmpInt=3;}
			// webDataAr[0] = tmpInt+"";

			// locX=777;
			// locY=777;
			// String sender
			// ="v="+cDate.toString()+"&lX="+aValues[0]+"&lY="+aValues[1]+
			// "&v0="+mValues[0]+"&v1="+mValues[1]+"&v2="+mValues[2];
			String sender = "lX=" + locX + "&lY=" + locY + "&v0=" + mValues[0]
					+ "&v1=" + mValues[1] + "&v2=" + mValues[2]
					//+"&a0="	+ aValues[0] + "&a1=" + aValues[1] + "&a2=" + aValues[2]
					+ "&ext=" + "!" + battLevel;

			//String urlStr = "http://92.63.96.27:8180/wm/wm_s?" + sender;
			String urlStr = com.msi.ibm.eyes.IBMEyes.getBNNurl()+"/wm_s?" + sender;
			
			Log.d("ar_wSndr:", urlStr);

			URL myURL = new URL(urlStr);
			URLConnection ucon = myURL.openConnection();

			InputStream is = ucon.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(50);
			String webData = "";
			try {
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
				webData = new String(baf.toByteArray());
			} finally {
				// baf.clear();
				bis.close();
			}
			if (!webData.equalsIgnoreCase("-")) {
				for (int i = 0; i < 99; i++) {
					webDataAr[i + 1] = webDataAr[i];
					webDataArTm[i + 1] = webDataArTm[i];
				}
				// <<<<<<< .mine

				// StringTokenizer st = new StringTokenizer(webData,"|");
				// int i=0;
				// / while (st.hasMoreTokens()) {
				// webDataAr[i] = st.nextToken().trim();
				// i++;
				// }

				// =======

				webDataAr[0] = webData.trim();
				// webDataAr[0] = "120|5000";
				// >>>>>>> .r93
				Log.d("ar_wSndr:", "webData [" + webData + "]");
				// webDataAr[0] = "180";
				webDataArTm[0] = System.currentTimeMillis();
			}

			// inetView.setText("inet answ: " + new
			// String(baf.toByteArray()));
			// inetView.setText("inet answ: " + sender);
			// Toast.makeText(this,
			// "Sended to web!:"+sender,Toast.LENGTH_LONG).show();
			Log.d("ar_wSndr:", "ar_" + urlStr + " >>> " + baf.toByteArray());

		} catch (Exception e) {
			// ex = e;
			// Log.e(getClass().getSimpleName(), "problem encountered", e);
			e.printStackTrace();
			Log.d("wSndrErr", e.toString());
		}

		// Log.i(getClass().getSimpleName(), "background task - end");
	}

	private void RedFlashLight() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification();
		notif.ledARGB = 0xFFff0000;
		notif.flags = Notification.FLAG_SHOW_LIGHTS;
		notif.ledOnMS = 500;
		nm.notify(0, notif);
		// nm.cancelAll();
		// Program the end of the light :
		mCleanLedHandler.postDelayed(mClearLED_Task, 200);
	}

	private void GreenFlashLight() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification();
		notif.ledARGB = 0xFFFF0000;
		notif.flags = Notification.FLAG_SHOW_LIGHTS;
		notif.ledOnMS = 500;
		// notif.ledOnMS = 100;
		nm.notify(0, notif);
		// Program the end of the light :
		// nm.cancelAll();
		mCleanLedHandler.postDelayed(mClearLED_Task, 400);
	}

	private void ClearLED() {
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(0);
	}

	private Runnable mClearLED_Task = new Runnable() {
		public void run() {
			// synchronized (IBMEyes.this) {
			ClearLED();
			// }
		}
	};

	public static void toRight(String extData) {
		Log.d("ar_toRight", extData);

		try {
			AudioSerialOutMono.new_baudRate = Integer.parseInt(baudRate);
		} catch (Exception e) {
			AudioSerialOutMono.new_baudRate = 9600;
			// baudbox.setText("9600");
			e.printStackTrace();
		}
		try {
			AudioSerialOutMono.new_characterdelay = Integer.parseInt("1");
		} catch (Exception e) {
			AudioSerialOutMono.new_characterdelay = 0;
			e.printStackTrace();// charbox.setText("0");

		}
		char sign = '0';
		if (extData.equals("toR")) {
			sign = 'a';
		}
		;
		if (extData.equals("toL")) {
			sign = 'k';
		}
		;
		if (extData.equals("toF")) {
			sign = 'p';
		}
		;
		if (extData.equals("toB")) {
			sign = 'z';
		}
		;
		AudioSerialOutMono.outStr = extData;

		try {
			AudioSerialOutMono.UpdateParameters();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			AudioSerialOutMono.output("a");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.d("ar_toRight", " stopt");
	}

	public static void setTaskStarted(Boolean taskStarted) {
		shakeServ.taskStarted = taskStarted;
	}

	public static Boolean getTaskStarted() {
		return taskStarted;
	}

	public static void setTaskStarted(boolean taskStarted2) {
		// TODO Auto-generated method stub
		taskStarted = taskStarted2;
	}

	public static void netxAccl(float values1, float values2, float values3,
			long ct) {
		aclTrk_arCount++;
		if (aclTrk_arCount == 98000) {
			aclTrk_arCount = 0;
		}

		if (acl_pt != 0) {
			double dt = (ct - acl_pt);
			acl_dt=dt/1000;
			
			aclTrk_ar[(int) aclTrk_arCount][0] = values1-acl_pax;
			aclTrk_ar[(int) aclTrk_arCount][1] = values2-acl_pay;
			aclTrk_ar[(int) aclTrk_arCount][2] = values3-acl_paz;
			acl_pax=values1;
			acl_pay=values2;
			acl_paz=values3;
			acl_ex = (values1 - aclTrk_ar[(int) aclTrk_arCount - 1][0]) / acl_dt;
			acl_ey = (values2 - aclTrk_ar[(int) aclTrk_arCount - 1][1]) / acl_dt;
			acl_ez = (values3 - aclTrk_ar[(int) aclTrk_arCount - 1][2]) / acl_dt;
			acl_ax = acl_ax + acl_ex * acl_dt;
			acl_ay = acl_ay + acl_ey * acl_dt;
			acl_az = acl_az + acl_ez * acl_dt;

			float tempF=(float) acl_vx;

			acl_vx = tempF + acl_ax * acl_dt;
			acl_vy = acl_vy + acl_ay * acl_dt;
			acl_vz = acl_vz + acl_az * acl_dt;
			acl_x = acl_x + acl_vx * acl_dt + acl_ax * acl_dt * acl_dt / 2 + acl_ex * acl_dt
					* acl_dt * acl_dt / 6;
			acl_y = acl_y + acl_vy * acl_dt + acl_ay * acl_dt * acl_dt / 2 + acl_ey * acl_dt
					* acl_dt * acl_dt / 6;
			acl_z = acl_z + acl_vz * acl_dt + acl_az * acl_dt * acl_dt / 2 + acl_ez * acl_dt
					* acl_dt * acl_dt / 6;
		}
		acl_pt = ct;

	}

}
