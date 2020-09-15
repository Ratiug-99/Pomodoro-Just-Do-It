package com.ratiug.dev.pomodorojustdoit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DBG | MainActivity | ";
    public static String KEY_TEMPNAME = "KEY_TEMPNAME";
    public static String KEY_EXTRA_MINUTES = "KEY_EXTRA_MINUTES";
    public static String KEY_SAVE_STATE_TIMER_TIME = "KEY_SAVE_STATE_TIMER_TIME";
    public static String KEY_BDROADCAST = "com.ratiug.dev.pomodorojustdoit_tick";
    public static String KEY_BDROADCAST_FINISH_TIMER = "com.ratiug.dev.pomodorojustdoit_finish_timer";
    private static String KEY_CHANNEL_TIMER = "KEY_CHANNEL_TIMER";
    private static int ID_NOTIFY_TIMER = 0;

    //////todo optimize code
    int minutesForTimer = 25;
    long timeLeft;
    MediaPlayer mPlayer;
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
    //temp var
    Button button;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        tvText = findViewById(R.id.tvTimer);
        button = findViewById(R.id.tempbutton);
        startButton = findViewById(R.id.btnStart);

        mIntent = new Intent(this, TimerService.class).putExtra(KEY_EXTRA_MINUTES, minutesForTimer);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                temp();
            }
        });

        broadcastReceiverTick = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { //todo create method
                timeLeft = intent.getLongExtra(KEY_TEMPNAME, 0);
                Date date = new Date(timeLeft);
                SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                String formatted = formatter.format(date);
                tvText.setText(formatted);
            }
        };

        finishTimer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) { //todo create method
                mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.music);  //todo: change sound
                mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mPlayer.stop();
                    }
                });
                mPlayer.start();
                Toast.makeText(getApplicationContext(), "Complete", Toast.LENGTH_SHORT).show();
            }
        };

        registerReceiver(finishTimer, new IntentFilter(KEY_BDROADCAST_FINISH_TIMER));

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

    private void temp() { // todo create normal function and updateinfo at tick
        Log.d(TAG, "temp: ");
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), KEY_CHANNEL_TIMER)
                .setSmallIcon(R.drawable.ic_notifi_timer)
                .setContentTitle("title")
                .setContentText("content")
                .setColor(Color.parseColor("#009add"))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(
                NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(KEY_CHANNEL_TIMER, KEY_CHANNEL_TIMER, NotificationManager.IMPORTANCE_HIGH);

            notificationManager.createNotificationChannel(mChannel);
        }

        notificationManager.notify(0, mBuilder.build());
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
        registerReceiver(broadcastReceiverTick, new IntentFilter(KEY_BDROADCAST));
        super.onResume();
    }

    private void startTimer() {

        Log.d(TAG, "startTimer");
        bindService(mIntent, mServiceConn, 0);
        startService(mIntent);

        
    }

}