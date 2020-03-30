package com.example.AndroidSpyer;

import android.os.Bundle;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.AndroidSpyer.MainService.NewBinder;


public class MainActivity extends AppCompatActivity implements MainServiceHelper.Output{
    private Handler handler;
    private boolean checker = false;
    private MainService mainservice;

    public static final int dText = 30;
    TextView text;
    String moved = "The phone was moved!";
    String notMoved = "Hasn't budged an inch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        text = findViewById(R.id.displayText);
        Button clear = findViewById(R.id.btnReset);
        Button exit = findViewById(R.id.btnExit);


        //initial set text of not moved
        text.setText(notMoved);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text.setText(notMoved);
                mainservice.btnreset();
            }
        });


        //make sure background service is not running!!!
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, MainService.class);
                stopService(intent);
                finish();
            }
        });

        handler = new Handler(getMainLooper(), new newHandler());

    }
/*
    TextView accX, accY, accZ, maxX, maxY, maxZ, displayText;
    SensorManager sensorManager;
    Sensor accelerometer;
    Vibrator vibrator;
    float accX_val, accY_val, accZ_val, maxX_val, maxY_val, maxZ_val, vibT;
    AccelerometerListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        accX = findViewById(R.id.accX);
        accY = findViewById(R.id.accY);
        accZ = findViewById(R.id.accZ);
        maxX = findViewById(R.id.maxX);
        maxY = findViewById(R.id.maxY);
        maxZ = findViewById(R.id.maxZ);
        displayText = findViewById(R.id.displayText);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)!= null){
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            vibT = accelerometer.getMaximumRange()/2;
        }
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

 */

    @Override
    protected void onResume(){
        super.onResume();
        Intent intent = new Intent(this, MainService.class);
        startService(intent);
        bind();
    }

    private void bind() {
        Intent intent = new Intent(this, MainService.class);

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder serviceBinder) {
            // We have bound to the camera service.
            NewBinder binder = (NewBinder) serviceBinder;
            mainservice = binder.findService();
            checker = true;
            // Let's connect the callbacks.
            mainservice.showResultfinal(MainActivity.this);
        }
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            checker = false;
        }
    };

/*
    @Override
    protected void onResume() {
        super.onResume();
        listener = new AccelerometerListener();
        sensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    class AccelerometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            accX_val = event.values[0];
            accY_val = event.values[1];
            accZ_val = event.values[2];

            accX.setText(Float.toString(accX_val));
            accY.setText(Float.toString(accY_val));
            accZ.setText(Float.toString(accZ_val));

            if (maxX_val < accX_val){
                maxX_val = accX_val;
                maxX.setText(Float.toString(maxX_val));
                displayText.setText("the phone moved");
            }

            if (maxY_val < accY_val){
                maxY_val = accY_val;
                maxY.setText(Float.toString(maxY_val));
                displayText.setText("the phone moved");
            }
            if (maxZ_val < accZ_val){
                maxZ_val = accZ_val;
                maxZ.setText(Float.toString(maxZ_val));
            }

            if (accX_val > vibT || accY_val > vibT || accZ_val > vibT) {
                vibrator.vibrate(50);
                Toast.makeText(MainActivity.this,"Threshold has been reached", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

 */


    @Override
    public void onResultReady(ServiceOutput result) {
        handler.obtainMessage(dText, result).sendToTarget();

    }

    //checks if it was moved
    private class newHandler implements Handler.Callback {
        @Override
        public boolean handleMessage(Message message) {

                ServiceOutput result = (ServiceOutput) message.obj;
                // show moved text
                if (result.moved == true) {
                    text.setText(moved);
                }else {
                    text.setText(notMoved);
                }

            return true;
        }
    }
}
