package com.example.AndroidSpyer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.HandlerThread;
import android.util.Log;

import static android.content.Context.SENSOR_SERVICE;

public class MainServiceHelper implements Runnable ,SensorEventListener{

    private Context c;
    Sensor xysenseor;
    private boolean ifrun;
    float thresholdacc;
    SensorManager sensorManager;
    Output output;


    //initialize time
    int startTime = (int)((System.currentTimeMillis()/1000)%3600);
    HandlerThread handler;

    //runnable
    @Override
    public void run(){
        handler = new HandlerThread("newThread");
        handler.start();
        ifrun = true;
        sensorManager = (SensorManager) c.getSystemService(SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
            xysenseor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            thresholdacc = xysenseor.getMaximumRange()/2;
            sensorManager.registerListener(this, xysenseor, SensorManager.SENSOR_DELAY_NORMAL);


        }
    }

    public void resetCount(){
        startTime = (int)((System.currentTimeMillis()/1000)%3600);
        Log.d("reset", "In resetcount()");
    }

    public MainServiceHelper(Context context) {
        this.c = context;

    }


    //checks threshold acceleration
    @Override
    public void onSensorChanged(SensorEvent event) {
        if(ifrun) {
            //let device know seconds
            int curTime = (int) ((System.currentTimeMillis()/1000)%3600);
            //checks to make sure 15 seconds have passed
            if (curTime - startTime > 15) {
                float xdirection, ydirection;

                //setting values to each direction
                xdirection = event.values[0];
                ydirection = event.values[1];

                ydirection = ydirection - (float) 9.73282;

                if (xdirection < 2)
                    xdirection= 0;
                if (ydirection < 2)
                    ydirection = 0;

                if (xdirection > thresholdacc) {
                    ifMoved(true);
                    Log.d("moved", "In sensor change");
                }
                if (ydirection > thresholdacc) {
                    ifMoved(true);
                    Log.d("moved", "In sensor change");


                }
            }
        }
    }


    private void ifMoved(Boolean ifmoved) {
        ServiceOutput result = new ServiceOutput();
        result.moved = ifmoved;
        output.onResultReady(result);

    }

    public void showResult(Output result) {
        output = result;
    }
    public interface Output {
        void onResultReady(ServiceOutput result);
    }








    //required function
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }




}
