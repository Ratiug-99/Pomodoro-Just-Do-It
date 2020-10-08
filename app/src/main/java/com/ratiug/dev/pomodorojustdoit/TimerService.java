package com.ratiug.dev.pomodorojustdoit;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TimerService extends Service {
    private static final String TAG = "DBG | TimerService | ";
    MyBinder mBinder = new MyBinder();
    long mMlsToFinish;
    CountDownTimer cdt;
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
        mMlsToFinish = intent.getLongExtra(MainActivity.KEY_PUT_MLS_TO_TIMER, 0);
        return mBinder;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        cdt.cancel();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        startTimer();
        return super.onStartCommand(intent, flags, startId);
    }

    void startTimer() {
        Log.d(TAG, "start " + mMlsToFinish);
        if (!runTimer) {
           cdt = new CountDownTimer(mMlsToFinish, 1000) { //MinutesToMilliseconds(minutesForTimer)
                public void onTick(long millisUntilFinished) {

                        sendBroadcast(new Intent(MainActivity.KEY_BDROADCAST_TICK).putExtra(MainActivity.KEY_MILLIS_UNTIL_FINISHED, millisUntilFinished));
                        runTimer = true;

                }

                public void onFinish() {
                    sendBroadcast(new Intent(MainActivity.KEY_BDROADCAST_FINISH_TIMER));
                    runTimer = false;
                }
            }.start();
        }
    }



    class MyBinder extends Binder {
        TimerService getService() {
            Log.d(TAG, "getService");
            return TimerService.this;
        }
    }
}
