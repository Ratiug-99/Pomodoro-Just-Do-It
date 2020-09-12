package com.ratiug.dev.pomodorojustdoit;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static String  KEY_EXTRA_MINUTES = "KEY_EXTRA_MINUTES";
    public static String KEY_BDROADCAST = "com.ratiug.dev.pomodorojustdoit";
    private static final String TAG = "DBG | MainActivity | ";
    //
    int minutesForTimer = 1;
    int timeLeft;
    //
    ServiceConnection mServiceConn;
    TimerService myTimerService;
    Boolean bound = false;
    Intent mIntent;
    BroadcastReceiver broadcastReceiver;
    //
    private TextView tvText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = findViewById(R.id.tvTimer);

        mIntent = new Intent(this,TimerService.class).putExtra(KEY_EXTRA_MINUTES,minutesForTimer);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              //  timeLeft = myTimerService.getTimeLeft();
                //Log.d(TAG, "onReceive B_R_O_A_D_");
//                tvText.setText(myTimerService.getTimeLeft());
                timeLeft = myTimerService.getTimeLeft() / 1000;
                Log.d(TAG, "onReceive: " + timeLeft);
               tvText.setText(String.valueOf(timeLeft));

            }
        };

        IntentFilter intentFilter = new IntentFilter(KEY_BDROADCAST);
        registerReceiver(broadcastReceiver,intentFilter);

        mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                myTimerService = ((TimerService.MyBinder) iBinder).getService();
                bound = true;
                Log.d(TAG, "onServiceConnected");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                bound = false;
                Log.d(TAG, "onServiceDisconnected");
            }
        };
        startTimer();

    }

    private void startTimer() {
        Log.d(TAG, "startTimer");
        bindService(mIntent,mServiceConn,0);
        startService(mIntent);
    }

}