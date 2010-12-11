package com.msi.ibm.eyes;



import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;
import android.location.LocationManager;


public class ServiceExample extends Service  {
	private LocationManager locationManager;
	private static final String TAG = "UpdateService";

	/**
	 * Interval to wait between background widget updates. Every 6 hours is
	 * plenty to keep background data usage low and still provide fresh data.
	 */
	private long update_interval = 6 * DateUtils.HOUR_IN_MILLIS;

	private int update_location = 0;

	//public LocationManager lm;
	private LocationListener myLocationListener;

	
	
	private static ServiceExample ssilka = null;
	
	public ServiceExample(){
		
		//super();
	}
	
	public static ServiceExample getInstance(){
		if(ssilka == null)
			ssilka = new ServiceExample();
		return ssilka;
	}
	
    public static final int INTERVAL = 10000; // 10 sec
    public static final int FIRST_RUN = 5000; // 5 seconds
    public double locX = 0;
    public double locY = 0;
    
    int REQUEST_CODE = 11223344;

    AlarmManager alarmManager;
    @Override
    public void onCreate() {
        super.onCreate();
        ssilka=this;
		//lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//myLocationListener = new myLocationListener();
		//lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0,	myLocationListener);

        	startService();
        Log.d("!", "onCreate(..)");
    }

    @Override
    public IBinder onBind(Intent intent) {
    	Log.d("!", "onBind(..)");
        return null;
    }
    


    @Override
    public void onDestroy() {
        if (alarmManager != null) {
            Intent intent = new Intent(this, RepeatingAlarmService.class);
            alarmManager.cancel(PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0));
        }
        Toast.makeText(this, "Service Stopped!", Toast.LENGTH_LONG).show();
        Log.d("!", "Service onDestroy(). Stop AlarmManager at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }

    private void startService() {

        Intent intent = new Intent(this, RepeatingAlarmService.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, 0);

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + FIRST_RUN,INTERVAL,pendingIntent);
                //SystemClock.currentThreadTimeMillis() + FIRST_RUN,INTERVAL,pendingIntent);
       //getPosE();
        String sb = locX+""+locY;
        
		Log.d("!!!!!!!!!", "Loc:gp: "+getPos()+"| "+sb);
		

        
        
        Toast.makeText(this, "Service Started.", Toast.LENGTH_LONG).show();
        Log.d("ibm_e", "AlarmManger started at " + new java.sql.Timestamp(System.currentTimeMillis()).toString());
    }




	public String pos;
	
	
	public String getPos() {
		

		//posX = 45.046224;
	//	posY = 41.975043; // n
		
		try{
		String location_context = Context.LOCATION_SERVICE;
		pos=location_context;
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
	}//getPos
	
	public String getLocPos(){
		String res;
		getPosE();
		res = locX+""+locY;
		return res; 
	}
	
	public void getPosE() {
		

		//posX = 45.046224;
		//posY = 41.975043; // n
		//Context.
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

	}//getPos
	
	public void setest(){
		Log.d("!!!setest!!", "Loc:setest: "+getPos()+"| ");
	}

}


