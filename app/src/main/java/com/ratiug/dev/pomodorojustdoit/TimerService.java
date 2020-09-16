package com.ratiug.dev.pomodorojustdoit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {
    private static final String TAG = "DBG | TimerService | ";
    MyBinder mBinder = new MyBinder();
    int minutesForTimer;
    long mlls;
    TimerTask timerTask;
    Timer timer = new Timer();
    int timeLeft;
    long tempStr;
    Boolean runTimer = false;

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        minutesForTimer = intent.getIntExtra(MainActivity.KEY_PUT_MINUTES_TO_TIMER,0);
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

    void start() {
        Log.d(TAG, "start ");
        if (!runTimer) {
            new CountDownTimer(15000, 1000) { //inutesToMilliseconds(minutesForTimer)
                public void onTick(long millisUntilFinished) {
                    Log.d(TAG, "onTick: " + millisUntilFinished / 1000);
                    //here you can have your logic to set text to edittext
                    tempStr = (millisUntilFinished);
                    sendBroadcast(new Intent(MainActivity.KEY_BDROADCAST_TICK).putExtra(MainActivity.KEY_MILLIS_UNTIL_FINISHED, tempStr));
                    runTimer = true;
                }

                public void onFinish() {
                    Log.d(TAG, "onFinish: " + minutesForTimer);
                    Log.d(TAG, "onFinish: DONE");
                    sendBroadcast(new Intent(MainActivity.KEY_BDROADCAST_FINISH_TIMER));
                    runTimer = false;
                }
            }.start();
        }
    }

    private int minutesToMilliseconds(int minutesForTimer) {
        return minutesForTimer * 60000;
    }

    public long getTimeLeft() {
        return mlls;
    }

    class MyBinder extends Binder {
        TimerService getService() {
            Log.d(TAG, "getService");
            return TimerService.this;
        }
    }
}
