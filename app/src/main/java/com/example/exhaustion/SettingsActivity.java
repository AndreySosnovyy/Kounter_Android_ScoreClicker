package com.example.exhaustion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class SettingsActivity extends AppCompatActivity {

    DataBaseHelper dataBaseHelper;
    private static final String TAG = "DEBUG LOGS";
    Toolbar toolbar;
    SharedPreferences settings;

    View vibration_view_line_on, vibration_view_line_off;
    View sound_view_line_on, sound_view_line_off;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch soundSwitch, vibrationSwitch;

    Button clearDatabaseButton, payMoneyButton, sendPromoCodeButton;
    EditText promoCodeField;
    TextView appInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        soundSwitch = findViewById(R.id.soundSwitch);
        vibrationSwitch = findViewById(R.id.vibrationSwitch);
        vibration_view_line_on = findViewById(R.id.vibration_view_line_on);
        vibration_view_line_off = findViewById(R.id.vibration_view_line_off);
        sound_view_line_on = findViewById(R.id.sound_view_line_on);
        sound_view_line_off = findViewById(R.id.sound_view_line_off);
        clearDatabaseButton = findViewById(R.id.clearDatabaseButton);
        payMoneyButton = findViewById(R.id.payMoneyButton);
        sendPromoCodeButton = findViewById(R.id.sendPromoCodeButton);
        promoCodeField = findViewById(R.id.promoCodeField);
        appInfoTextView = findViewById(R.id.appInfoTextView);

        try {
            PackageInfo packageInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
            appInfoTextView.setText("Версия приложения: " + packageInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        settings = getSharedPreferences(Helper.APP_PREFERENCES, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = settings.edit();

        dataBaseHelper = new DataBaseHelper(this);
        final SQLiteDatabase database = dataBaseHelper.getWritableDatabase();

        // замена actionBar на toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Настройки");
        setSupportActionBar(toolbar);

        // действие на кнопку "назад" в toolbar
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // начальная установка свичей
        soundSwitch.setChecked(settings.getBoolean(Helper.SETTINGS_SOUND, true));
        vibrationSwitch.setChecked(settings.getBoolean(Helper.SETTINGS_VIBRATION, true));
        if (soundSwitch.isChecked()) {
            sound_view_line_off.setVisibility(View.INVISIBLE);
            sound_view_line_on.setVisibility(View.VISIBLE);
        } else {
            sound_view_line_off.setVisibility(View.VISIBLE);
            sound_view_line_on.setVisibility(View.INVISIBLE);
        }
        if (vibrationSwitch.isChecked()) {
            vibration_view_line_off.setVisibility(View.INVISIBLE);
            vibration_view_line_on.setVisibility(View.VISIBLE);
        } else {
            vibration_view_line_off.setVisibility(View.VISIBLE);
            vibration_view_line_on.setVisibility(View.INVISIBLE);
        }

        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sound_view_line_off.setVisibility(View.INVISIBLE);
                    sound_view_line_on.setVisibility(View.VISIBLE);
                    editor.putBoolean(Helper.SETTINGS_SOUND, true);
                    editor.apply();
                } else {
                    sound_view_line_off.setVisibility(View.VISIBLE);
                    sound_view_line_on.setVisibility(View.INVISIBLE);
                    editor.putBoolean(Helper.SETTINGS_SOUND, false);
                    editor.apply();
                }
            }
        });

        vibrationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    vibration_view_line_off.setVisibility(View.INVISIBLE);
                    vibration_view_line_on.setVisibility(View.VISIBLE);
                    editor.putBoolean(Helper.SETTINGS_VIBRATION, true);
                    editor.apply();
                } else {
                    vibration_view_line_off.setVisibility(View.VISIBLE);
                    vibration_view_line_on.setVisibility(View.INVISIBLE);
                    editor.putBoolean(Helper.SETTINGS_VIBRATION, false);
                    editor.apply();
                }
            }
        });

        clearDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SQLiteDatabase database = new DataBaseHelper(getApplicationContext()).getWritableDatabase();
                try {
                    database.delete(DataBaseHelper.COUNTER_TABLE, null, null);
                    database.delete(DataBaseHelper.COUNTER_CLICK_TABLE, null, null);
                    Log.d(TAG, "THE DATABASE HAS BEEN CLEARED");
                    Toast.makeText(SettingsActivity.this, "Счетчики успешно удалены", Toast.LENGTH_SHORT).show();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        sendPromoCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // создание кода для отмены ограничений без оплаты
                String[] temp = MainActivity.GetDateAndTime();
                int day = Integer.parseInt(temp[0].substring(0, 2));
                int month = Integer.parseInt(temp[0].substring(3, 5));
                int codeDate = day + month;
                int hours = Integer.parseInt(temp[1].substring(0, 2));
                int minutes = Integer.parseInt(temp[1].substring(3, 5));
                int codeTime = hours + minutes;
                String code = "code-" + codeDate + "-" + codeTime;
                // формат кода: "code-сумма_дня_и_месяца-сумма_часов_и_минут"

                if (promoCodeField.getText().toString().equals(code)) {
                    Toast.makeText(SettingsActivity.this, "Код активирован", Toast.LENGTH_SHORT).show();
                    editor.putBoolean(Helper.SETTINGS_PAID, true);
                    editor.apply();
                    promoCodeField.setText("");
                } else {
                    Toast.makeText(SettingsActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                }
            }
        });

        promoCodeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    sendPromoCodeButton.setBackground(getResources().getDrawable(R.drawable.pick_time_button));
                } else {
                    sendPromoCodeButton.setBackground(getResources().getDrawable(R.drawable.unclickable_create_button));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
