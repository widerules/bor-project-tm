package com.msi.ibm.eyes;


import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

class MyService extends Service implements SensorEventListener {
    SensorManager mSensorEventManager;
    Sensor mSensor;
	SensorManager sm = null;


    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                mSensorEventManager.unregisterListener(MyService.this);
                mSensorEventManager.registerListener(MyService.this, mSensor,SensorManager.SENSOR_DELAY_NORMAL);
            }
}

    };

    public void onCreate(Context context) {
        super.onCreate();

        // Obtain a reference to system-wide sensor event manager.
        //mSensorEventManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        sm = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
		sm.registerListener((SensorListener) this, SensorManager.SENSOR_ORIENTATION ,SensorManager.SENSOR_DELAY_NORMAL);
        // Get the default sensor of type TYPE_ORIENTATION (orientation sensor).
        //mSensor = SensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        // Register for events.
        //SensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        // Unregister our receiver.
        unregisterReceiver(mReceiver);

        // Unregister from SensorManager.
        sm.unregisterListener(this);
    }





	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		Log.d("!!!", "Loc: "+event.values[0]);
		
	}
}