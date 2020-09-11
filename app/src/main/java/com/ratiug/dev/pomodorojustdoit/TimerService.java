package com.ratiug.dev.pomodorojustdoit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {
    MyBinder mBinder = new MyBinder();
    int minutesForTimer;
    TimerTask timerTask;
    Timer timer = new Timer();
    int timeLeft;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    private static final String TAG = "DBG | TimerService | ";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        minutesForTimer = intent.getIntExtra(MainActivity.KEY_EXTRA_MINUTES,minutesForTimer);
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        start();
        return super.onStartCommand(intent, flags, startId);
    }

    void start(){
        Log.d(TAG, "start ");

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.KEY_BDROADCAST);
                Log.d(TAG, "run: BROADCAST");
                sendBroadcast(intent);
            }
        };
        timer.schedule(timerTask,1000,200);
    }

    private int minutesToMilliseconds(int minutesForTimer){
        return minutesForTimer * 6000;
    }

    public int getTimeLeft(){
        return timeLeft;
    }

    class MyBinder extends Binder {
        TimerService getService(){
            Log.d(TAG, "getService");
            return TimerService.this;
        }
    }
}
