package com.ratiug.dev.pomodorojustdoit;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    public static final String APP_PREFERENCES = "settings";
    public static final String APP_PREFERENCES_MINUTES_FOR_CONCENTRATE_TIMER = "minutes_for_work_timer";
    public static final String APP_PREFERENCES_MINUTES_FOR_REST_TIMER = "minutes_for_rest_timer";

    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    public SharedPreferencesHelper (Context context){
        mSharedPreferences = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.apply();
    }

    public void setMinutesConcentrate (String minutes){
        mEditor.putString(APP_PREFERENCES_MINUTES_FOR_CONCENTRATE_TIMER,minutes);
        mEditor.apply();
    }

    public void setMinutesRest (String minutes){
        mEditor.putString(APP_PREFERENCES_MINUTES_FOR_REST_TIMER,minutes);
        mEditor.apply();
    }

    public String  getMinutesConcentrate(){
        return mSharedPreferences.getString(APP_PREFERENCES_MINUTES_FOR_CONCENTRATE_TIMER,"25");
    }

    public String  getMinutesRst(){
        return mSharedPreferences.getString(APP_PREFERENCES_MINUTES_FOR_REST_TIMER,"5");
    }


}
