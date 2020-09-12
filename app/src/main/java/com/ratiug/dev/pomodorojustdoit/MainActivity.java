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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public static String KEY_TEMPNAME = "KEY_TEMPNAME";
    public static String  KEY_EXTRA_MINUTES = "KEY_EXTRA_MINUTES";
    public static String KEY_BDROADCAST = "com.ratiug.dev.pomodorojustdoit_tick";
    public static String KEY_BDROADCAST_FINISH_TIMER = "com.ratiug.dev.pomodorojustdoit_finish_timer";

    private static final String TAG = "DBG | MainActivity | ";
    //
    int minutesForTimer = 1;
    String timeLeft;
    //
    ServiceConnection mServiceConn;
    TimerService myTimerService;
    Boolean bound = false;
    Intent mIntent;
    BroadcastReceiver broadcastReceiverTick;
    BroadcastReceiver finishTimer;
    //
    private TextView tvText;
    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvText = findViewById(R.id.tvTimer);
        startButton = findViewById(R.id.btnStart);

        mIntent = new Intent(this,TimerService.class).putExtra(KEY_EXTRA_MINUTES,minutesForTimer);

        broadcastReceiverTick = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timeLeft = intent.getStringExtra(KEY_TEMPNAME);
                tvText.setText(timeLeft);
            }
        };

        finishTimer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(),"Complete",Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(finishTimer,new IntentFilter(KEY_BDROADCAST_FINISH_TIMER));

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

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTimer();
            }
        });




    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
       registerReceiver(broadcastReceiverTick,new IntentFilter(KEY_BDROADCAST));
        super.onResume();
    }

    private void startTimer() {
        Log.d(TAG, "startTimer");
        bindService(mIntent,mServiceConn,0);
        startService(mIntent);
    }

}