package com.example.exhaustion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;


public class MenuActivity extends AppCompatActivity implements CustomDialogFragment.CustomFragmentDialogListener {

    RadioButton radioButton1, radioButton2, radioButton3, radioButton4;
    Switch timerSwitch, stopwatchSwitch;
    EditText finishValueField, stepValueField, nameField, startValueField;
    TextView radioButton1textView, radioButton2textView, radioButton3textView, radioButton4textView;
    Button pickTimeButton, createCounterButton, clearDatabaseButton;
    Toolbar toolbar;
    View timerLineOff, timerLineOn, stopwatchLineOff, stopwatchLineOn;
    CompoundButton previousRB;
    Animation scaleAnimation, reverseScaleAnimation, pickTimeButtonAnimation, pickTimeButtonAnimationReverse;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_for_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            // новый лэйаут для настроек
        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        radioButton1 = findViewById(R.id.radioButton1);
        radioButton2 = findViewById(R.id.radioButton2);
        radioButton3 = findViewById(R.id.radioButton3);
        radioButton4 = findViewById(R.id.radioButton4);
        timerSwitch = findViewById(R.id.timerSwitch);
        stopwatchSwitch = findViewById(R.id.stopwatchSwitch);
        finishValueField = findViewById(R.id.finishValueField);
        startValueField = findViewById(R.id.startValueField);
        stepValueField = findViewById(R.id.stepValueField);
        nameField = findViewById(R.id.nameField);
        pickTimeButton = findViewById(R.id.pickTimeButton);
        createCounterButton = findViewById(R.id.createCounterButton);
        radioButton1textView = findViewById(R.id.radioButton1textView);
        radioButton2textView = findViewById(R.id.radioButton2textView);
        radioButton3textView = findViewById(R.id.radioButton3textView);
        radioButton4textView = findViewById(R.id.radioButton4textView);
        toolbar = findViewById(R.id.toolbar);
        timerLineOff = findViewById(R.id.timer_view_line_off);
        timerLineOn = findViewById(R.id.timer_view_line_on);
        stopwatchLineOff = findViewById(R.id.stopwatch_view_line_off);
        stopwatchLineOn = findViewById(R.id.stopwatch_view_line_on);
        clearDatabaseButton = findViewById(R.id.clearDB);

        startValueField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        finishValueField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        stepValueField.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        previousRB = radioButton1;
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        reverseScaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_reverse);
        pickTimeButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.pick_time_button_anim);
        pickTimeButtonAnimationReverse = AnimationUtils.loadAnimation(this, R.anim.pick_time_button_anim_reverse);

        toolbar.setTitle("      Kounter");
        setSupportActionBar(toolbar);

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    createCounterButton.setBackground(getResources().getDrawable(R.drawable.pick_time_button));
                } else {
                    createCounterButton.setBackground(getResources().getDrawable(R.drawable.unclickable_create_button));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clearDatabaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = new DataBaseHelper(getApplicationContext()).getWritableDatabase();
                try {
                    database.delete(DataBaseHelper.COUNTER_TABLE, null, null);
                    database.delete(DataBaseHelper.COUNTER_CLICK_TABLE, null, null);
//                    database.execSQL("drop table if exists " + DataBaseHelper.COUNTER_CLICK_TABLE);
//                    database.execSQL("drop table if exists " + DataBaseHelper.COUNTER_TABLE);
                    Log.d("myLog", "The database has been cleared");
                } catch (Exception e) {
                    Log.d("myLog", e.toString());
                }

            }
        });

        createCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO - сделать проверку на повторение имен в базе данных

                if (nameField.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Введите название счётчика", Toast.LENGTH_SHORT).show();
                } else if (timerSwitch.isChecked() && timerHours == 0 && timerMinutes == 0 && timerSeconds == 0) {
                    Toast.makeText(getApplicationContext(), "Выберите время для таймера", Toast.LENGTH_SHORT).show();
                } else if (stepValueField.getText().toString().equals("0")) {
                    Toast.makeText(MenuActivity.this, "Шаг не может быть равен 0", Toast.LENGTH_SHORT).show();
                } else if (finishValueField.getText().toString().equals("0")) {
                    Toast.makeText(MenuActivity.this, "Финиш не может быть равен 0", Toast.LENGTH_SHORT).show();
                } else if (!startValueField.getText().toString().equals("") && !finishValueField.getText().toString().equals("") &&
                        Integer.parseInt(startValueField.getText().toString()) >= Integer.parseInt(finishValueField.getText().toString())) {
                    Toast.makeText(MenuActivity.this, "Старт должен быть меньше финиша", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();

                    if (radioButton1.isChecked()) {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                    } else if (radioButton2.isChecked()) {
                        intent = new Intent(getApplicationContext(), Main2Activity.class);
                    } else if (radioButton3.isChecked()) {
                        intent = new Intent(getApplicationContext(), Main3Activity.class);
                    } else if (radioButton4.isChecked()) {
                        intent = new Intent(getApplicationContext(), Main4Activity.class);
                    }

                    String startValue = "0", finishValue = String.valueOf(Integer.MAX_VALUE), stepValue = "1";
                    if (!startValueField.getText().toString().equals("")) {
                        startValue = startValueField.getText().toString();
                    }
                    if (!finishValueField.getText().toString().equals("")) {
                        finishValue = finishValueField.getText().toString();
                    }
                    if (!stepValueField.getText().toString().equals("")) {
                        stepValue = stepValueField.getText().toString();
                    }

                    intent.putExtra("NAME", "" + nameField.getText().toString());
                    intent.putExtra("START_VALUE", "" + startValue);
                    intent.putExtra("FINISH_VALUE", "" + finishValue);
                    intent.putExtra("STEP_VALUE", "" + stepValue);
                    intent.putExtra("TIMER", "" + timerSwitch.isChecked());
                    intent.putExtra("STOPWATCH", "" + stopwatchSwitch.isChecked());
                    intent.putExtra("TIMER_VALUE_HOURS", "" + timerHours);
                    intent.putExtra("TIMER_VALUE_MINUTES", "" + timerMinutes);
                    intent.putExtra("TIMER_VALUE_SECONDS", "" + timerSeconds);
                    startActivity(intent);
                }
            }
        });

        radioButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView) {
                    //previousRB.startAnimation(reverseScaleAnimation);
                    radioButton1textView.setTextColor(getResources().getColor(R.color.white));
                    radioButton2textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton3textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton4textView.setTextColor(getResources().getColor(R.color.blue));
                    previousRB.setChecked(false);
                    previousRB.clearAnimation();
                    previousRB = buttonView;
                    radioButton1.startAnimation(scaleAnimation);
                }
            }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView) {
                    //previousRB.startAnimation(reverseScaleAnimation);
                    radioButton2textView.setTextColor(getResources().getColor(R.color.white));
                    radioButton1textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton3textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton4textView.setTextColor(getResources().getColor(R.color.blue));
                    previousRB.setChecked(false);
                    previousRB.clearAnimation();
                    previousRB = buttonView;
                    radioButton2.startAnimation(scaleAnimation);
                }
            }
        });
        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView) {
                    //previousRB.startAnimation(reverseScaleAnimation);
                    radioButton3textView.setTextColor(getResources().getColor(R.color.white));
                    radioButton1textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton2textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton4textView.setTextColor(getResources().getColor(R.color.blue));
                    previousRB.setChecked(false);
                    previousRB.clearAnimation();
                    previousRB = buttonView;
                    radioButton3.startAnimation(scaleAnimation);
                }
            }
        });
        radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView) {
                    //previousRB.startAnimation(reverseScaleAnimation);
                    radioButton4textView.setTextColor(getResources().getColor(R.color.white));
                    radioButton1textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton2textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton3textView.setTextColor(getResources().getColor(R.color.blue));
                    previousRB.setChecked(false);
                    previousRB.clearAnimation();
                    previousRB = buttonView;
                    radioButton4.startAnimation(scaleAnimation);
                }
            }
        });

        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    stopwatchSwitch.setChecked(false);
                    pickTimeButton.setVisibility(View.VISIBLE);
                    pickTimeButton.setClickable(true);
                    pickTimeButton.startAnimation(pickTimeButtonAnimation);
                    timerLineOff.setVisibility(View.INVISIBLE);
                    timerLineOn.setVisibility(View.VISIBLE);
                    if (timerHours == 0 && timerMinutes == 0 && timerSeconds == 0) {
                        createCounterButton.setBackground(getResources().getDrawable(R.drawable.unclickable_create_button));
                    }
                } else {
                    timerLineOff.setVisibility(View.VISIBLE);
                    timerLineOn.setVisibility(View.INVISIBLE);
                    pickTimeButton.startAnimation(pickTimeButtonAnimationReverse);
                    pickTimeButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pickTimeButton.setVisibility(View.INVISIBLE);
                            pickTimeButton.setClickable(false);
                        }
                    }, 90);
                    if (!nameField.getText().toString().equals("")) {
                        createCounterButton.setBackground(getResources().getDrawable(R.drawable.pick_time_button));
                    }
                }
            }
        });

        stopwatchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && timerSwitch.isChecked()) {
                    stopwatchLineOff.setVisibility(View.INVISIBLE);
                    stopwatchLineOn.setVisibility(View.VISIBLE);
                    timerSwitch.setChecked(false);
                    pickTimeButton.startAnimation(pickTimeButtonAnimationReverse);
                    pickTimeButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pickTimeButton.setVisibility(View.INVISIBLE);
                            pickTimeButton.setClickable(false);
                        }
                    }, 90);
                } else if (isChecked) {
                    stopwatchLineOff.setVisibility(View.INVISIBLE);
                    stopwatchLineOn.setVisibility(View.VISIBLE);
                    if (!nameField.getText().toString().equals("")) {
                        createCounterButton.setBackground(getResources().getDrawable(R.drawable.pick_time_button));
                    }
                } else {
                    stopwatchLineOff.setVisibility(View.VISIBLE);
                    stopwatchLineOn.setVisibility(View.INVISIBLE);
                }
            }
        });

        pickTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogFragment dialog = new CustomDialogFragment();
                dialog.show(getSupportFragmentManager(), "custom");
            }
        });
    }

    int timerHours = 0, timerMinutes = 0, timerSeconds = 0;

    @SuppressLint("SetTextI18n")
    @Override
    public void getTime(int hours, int minutes, int seconds) {

        timerHours = hours;
        timerMinutes = minutes;
        timerSeconds = seconds;

        if (!(hours == 0 && minutes == 0 && seconds == 0)) {
            pickTimeButton.setText(hours + "ч " + minutes + "м " + seconds + "с");
            pickTimeButton.setBackground(getResources().getDrawable(R.drawable.piked_time_button));
            pickTimeButton.setTextColor(getResources().getColor(R.color.blue));
            createCounterButton.setBackground(getResources().getDrawable(R.drawable.pick_time_button));
        } else {
            pickTimeButton.setText("Выбрать время");
            pickTimeButton.setBackground(getResources().getDrawable(R.drawable.pick_time_button));
            pickTimeButton.setTextColor(getResources().getColor(R.color.white));
            createCounterButton.setBackground(getResources().getDrawable(R.drawable.unclickable_create_button));
        }
    }
}
