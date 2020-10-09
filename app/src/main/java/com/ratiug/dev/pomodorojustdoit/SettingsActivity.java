package com.ratiug.dev.pomodorojustdoit;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private static final String TAG = "DBG | SA" ;

 //   private int mMinutesForConcentrate,mMinutesForRest;
    private EditText etMinutesForWork, etMinutesForRest;
    private TextView errorWorkMinutes,errorRestMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        final SharedPreferencesHelper sph = new SharedPreferencesHelper(this);

        etMinutesForWork = findViewById(R.id.et_minutes_concentrate_timer);
        etMinutesForRest = findViewById(R.id.et_minutes_rest_timer);
        etMinutesForWork.setText(sph.getMinutesConcentrate());
        etMinutesForRest.setText(sph.getMinutesRst());
        if (sph.getMinutesConcentrate().isEmpty() || sph.getMinutesRst().isEmpty()){
            Log.d(TAG, "onCreate: ");
        }
        else {
            Log.d(TAG, "onCreate: noempty");
        }
        errorRestMinutes = findViewById(R.id.tv_error_minutes_rest);
        errorWorkMinutes = findViewById(R.id.tv_error_minutes_concentrate);

        etMinutesForWork.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(isValidateValue(editable, etMinutesForWork, errorWorkMinutes))
                {
                    sph.setMinutesConcentrate(editable.toString());
                }
            }
        });
        etMinutesForRest.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(isValidateValue(editable,etMinutesForRest,errorRestMinutes));
                    sph.setMinutesRest(editable.toString());
            }
        });
    }

    private Boolean isValidateValue(Editable editable, EditText editText, TextView tvError) {
        String regexStr = "^[0-9]*$";
        Boolean bool = false;
        if (editable.toString().isEmpty() ||
                editable.length() > 2 ||
                !editText.getText().toString().trim().matches(regexStr) ||
                Integer.parseInt(editable.toString()) < 5 ||
                Integer.parseInt(editable.toString()) > 60) {
            tvError.setText("Enter Minutes from 5 to 60"); //todo write correct text error
            tvError.setVisibility(View.VISIBLE);
            editText.getBackground().setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
            bool  = false;
            Log.d(TAG, "isValidateValue: false");
        } else {
            editText.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            tvError.setVisibility(View.GONE);
            bool = true;
            Log.d(TAG, "isValidateValue: true");
        }
        return bool;
    }
}