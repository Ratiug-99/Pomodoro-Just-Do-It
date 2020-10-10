package com.ratiug.dev.pomodorojustdoit;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "DBG | MainActivity | ";
    //KEY`s and ID`s
    public static String KEY_MILLIS_UNTIL_FINISHED = "KEY_MILLIS_UNTIL_FINISHED"; //Variable key to transmit milliseconds to finish every tick
    public static String KEY_PUT_MLS_TO_TIMER = "KEY_PUT_MINUTES_TO_TIMER"; //Minutes to start timer
    public static String KEY_SAVE_STATE_TIMER_TIME = "KEY_SAVE_STATE_TIMER_TIME";
    public static String KEY_BDROADCAST_TICK = "com.ratiug.dev.pomodorojustdoit_tick";
    public static String KEY_BDROADCAST_FINISH_TIMER = "com.ratiug.dev.pomodorojustdoit_finish_timer";
    //

    //
    DateFormat mDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.UK);
    ServiceConnection mServiceConn;
    TimerService myTimerService;
    Boolean bound = false;
    Intent mIntent;
    BroadcastReceiver mBroadcastReceiverTick;
    BroadcastReceiver mFinishTimer;
    MediaPlayer mPlayer ;
    //View
    private TextView mTextViewTime;
    private Button mStartTimerBtn;
    private Button mStopTimerButton;
    private Button mSettingsButton;
    //temp var//todo optimizecode
    int minutesForTimerDefault ; //todo need update when call;
    boolean isRunTimer = false;
    long timeLeft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        mTextViewTime = findViewById(R.id.tvTimer);
        mStartTimerBtn = findViewById(R.id.btnStart);
        mStopTimerButton = findViewById(R.id.btnStop);
        mSettingsButton = findViewById(R.id.btnSettings);
        updateDefaultTimeConcentrate();
        mBroadcastReceiverTick = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
               // Log.d(TAG, "onReceive: TICK +");
                updateTimeToFinish(intent);
            }
        };

        mFinishTimer = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "onReceive: FINISH + ");
                isRunTimer = false;
                finishTimer();
            }
        };

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

        mStartTimerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMyServiceRunning(TimerService.class)){
                    Log.d(TAG, "onClickStartTimer: start");
                    pauseTimer();
                    // todo pause timer
                } else {
                    Log.d(TAG, "onClickStartTimer: stop");
                    startTimer();
                }
            }
        });

        mStopTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer();
            }
        });

        mSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });
    }




    private void RegisterReceiver() {
        registerReceiver(mFinishTimer, new IntentFilter(KEY_BDROADCAST_FINISH_TIMER));
        registerReceiver(mBroadcastReceiverTick, new IntentFilter(KEY_BDROADCAST_TICK));
    }

    private void UnRegisterReceiver() {
            unregisterReceiver(mFinishTimer);
            unregisterReceiver(mBroadcastReceiverTick);

    }

    private void finishTimer() { //todo optimizecode
        mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.finish_work_timer);
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.stop();
            }
        });
        mPlayer.start();
        mStartTimerBtn.setText(R.string.start);
        stopService(mIntent);
        timeLeft = minutesForTimerDefault * 60000;

    }

    private void updateTimeToFinish(Intent intentFromBroadcast) {
        timeLeft = intentFromBroadcast.getLongExtra(KEY_MILLIS_UNTIL_FINISHED, 0);
       setTimeText(timeLeft);
    }

    private void setTimeText(long mls){
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss", Locale.UK);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = new Date(mls);
        String timeToFinish = formatter.format(date);
        mTextViewTime.setText(timeToFinish);
        CreateNotificationConcentration(timeToFinish);
    }

    private void CreateNotificationConcentration(String time) { //todo add onClickListener
        String KEY_CHANNEL_TIMER = "KEY_CHANNEL_TIMER";
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), KEY_CHANNEL_TIMER)
                .setSmallIcon(R.drawable.ic_notifi_timer)
                .setContentTitle("Время концентрации")
                .setContentText(time)
                .setVibrate(null)
//                .setColor(Color.parseColor("#009add"))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(
                NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(KEY_CHANNEL_TIMER, KEY_CHANNEL_TIMER, NotificationManager.IMPORTANCE_LOW);

            notificationManager.createNotificationChannel(mChannel);
        }

        int ID_NOTIFY_TIMER = 0;
        notificationManager.notify(ID_NOTIFY_TIMER, mBuilder.build());
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(KEY_SAVE_STATE_TIMER_TIME, mTextViewTime.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        mTextViewTime.setText(savedInstanceState.getString(KEY_SAVE_STATE_TIMER_TIME));
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void stopTimer() { //todo optimizecode
        Log.d(TAG, "stopTimer");
       // mTextViewTime.setText(R.string._25_00);
        setTimeText(minutesForTimerDefault * 60000);
        timeLeft = minutesForTimerDefault * 60000;
        mStartTimerBtn.setText(R.string.start);
        stopService(mIntent);
    }

    private void startTimer() { //todo optimizecode
        Log.d(TAG, "startTimer " + timeLeft);
        RegisterReceiver();
        mIntent = new Intent(this, TimerService.class).putExtra(KEY_PUT_MLS_TO_TIMER, timeLeft);
        bindService(mIntent, mServiceConn, 0);
        startService(mIntent);
        isRunTimer = true;
        mStartTimerBtn.setText("Pause");
    }

    private void pauseTimer() { //todo optimizecode
        stopService(mIntent);
        mStartTimerBtn.setText("Start!");
        Log.d(TAG, "pauseTimer: " );
    }

   private void updateDefaultTimeConcentrate(){
       final SharedPreferencesHelper sharedPreferencesHelper = new SharedPreferencesHelper(this);
        minutesForTimerDefault = Integer.parseInt(sharedPreferencesHelper.getMinutesConcentrate());
       Log.d(TAG, "updateDefaultTimeConcentrate: " + isRunTimer);
        if(!isRunTimer){
            timeLeft = minutesForTimerDefault * 60000;
            setTimeText(timeLeft);
        }
    }


    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        updateDefaultTimeConcentrate();
        super.onResume();
    }
}