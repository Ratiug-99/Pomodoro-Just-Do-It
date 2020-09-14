package com.ratiug.dev.pomodorojustdoit;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    public static String KEY_TEMPNAME = "KEY_TEMPNAME";
    public static String  KEY_EXTRA_MINUTES = "KEY_EXTRA_MINUTES";
    public static String  KEY_SAVE_STATE_TIMER_TIME = "KEY_SAVE_STATE_TIMER_TIME";
    public static String KEY_BDROADCAST = "com.ratiug.dev.pomodorojustdoit_tick";
    public static String KEY_BDROADCAST_FINISH_TIMER = "com.ratiug.dev.pomodorojustdoit_finish_timer";

    private static final String TAG = "DBG | MainActivity | ";
    //
    int minutesForTimer = 25;
    long timeLeft;
    //
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.UK);
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
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        tvText = findViewById(R.id.tvTimer);
        startButton = findViewById(R.id.btnStart);

        mIntent = new Intent(this,TimerService.class).putExtra(KEY_EXTRA_MINUTES,minutesForTimer);

        broadcastReceiverTick = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                timeLeft = intent.getLongExtra(KEY_TEMPNAME,0);
                Date date = new Date(timeLeft);
                SimpleDateFormat formatter= new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formatted = formatter.format(date );
                tvText.setText(formatted);
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
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_SAVE_STATE_TIMER_TIME, tvText.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        tvText.setText(savedInstanceState.getString(KEY_SAVE_STATE_TIMER_TIME));
        super.onRestoreInstanceState(savedInstanceState);
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