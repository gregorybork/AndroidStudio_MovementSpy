package com.example.AndroidSpyer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

public class MainService extends Service {
    private Thread thread;
    private MainServiceHelper helper;
    PowerManager.WakeLock keepAwake;

    //reseting count down
    public void btnreset(){
        helper.resetCount();
        thread.interrupt();
        thread = new Thread(helper);
        thread.start();
    }



    //bind the service

    public class NewBinder extends Binder {
        MainService findService() {
            return MainService.this;
        }
    }

    //keep this app awake
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (thread.getState() == Thread.State.NEW) {
            thread.start();
        }
        PowerManager powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        keepAwake = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,":MyTag");

        return START_STICKY;
    }

    public void showResultfinal(MainServiceHelper.Output output) {
        helper.showResult(output);
    }



    // output binder
    private final IBinder Binder = new NewBinder();
    @Override
    public IBinder onBind(Intent intent) {

        return Binder;
    }


    //thread to begin mainservice
    @Override
    public void onCreate() {
        helper = new MainServiceHelper(getApplicationContext());
        thread = new Thread(helper);
        thread.start();
    }





}
