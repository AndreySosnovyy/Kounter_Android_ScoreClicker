package com.example.exhaustion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
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
    Button pickTimeButton, createCounterButton;
    Toolbar toolbar;

    CompoundButton previousRB;
    Animation scaleAnimation, reverseScaleAnimation, pickTimeButtonAnimation, pickTimeButtonAnimationReverse;

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

        startValueField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
        finishValueField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(5)});
        stepValueField.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});

        previousRB = radioButton1;
        scaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale);
        reverseScaleAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_reverse);
        pickTimeButtonAnimation = AnimationUtils.loadAnimation(this, R.anim.pick_time_button_anim);
        pickTimeButtonAnimationReverse = AnimationUtils.loadAnimation(this, R.anim.pick_time_button_anim_reverse);

        setSupportActionBar(toolbar);



        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0)
                {
                    createCounterButton.setBackground(getResources().getDrawable(R.drawable.pick_time_button));
                }
                else
                {
                    createCounterButton.setBackground(getResources().getDrawable(R.drawable.unclickable_create_button));
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        createCounterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nameField.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Введите название счётчика", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Intent intent = new Intent();

                    if (radioButton1.isChecked())
                    {
                        intent = new Intent(getApplicationContext(), MainActivity.class);
                    }
                    else if (radioButton2.isChecked())
                    {
                        intent = new Intent(getApplicationContext(), Main2Activity.class);
                    }
                    else if (radioButton3.isChecked())
                    {
                        intent = new Intent(getApplicationContext(), Main3Activity.class);
                    }
                    else if (radioButton4.isChecked())
                    {
                        intent = new Intent(getApplicationContext(), Main4Activity.class);
                    }

                    String startValue, finishValue, stepValue;
                    if (startValueField.getText().toString().equals(""))
                    {
                        startValue = "0";
                    }
                    else
                    {
                        startValue = startValueField.getText().toString();
                    }

                    if (finishValueField.getText().toString().equals(""))
                    {
                        finishValue = "" + Integer.MAX_VALUE;
                    }
                    else
                    {
                        finishValue = finishValueField.getText().toString();
                    }

                    if (stepValueField.getText().toString().equals(""))
                    {
                        stepValue = "1";
                    }
                    else
                    {
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
                if (previousRB != buttonView)
                {
                    radioButton1.startAnimation(scaleAnimation);
                    //previousRB.startAnimation(reverseScaleAnimation);
                    radioButton1textView.setTextColor(getResources().getColor(R.color.white));
                    radioButton2textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton3textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton4textView.setTextColor(getResources().getColor(R.color.blue));
                    previousRB.setChecked(false);
                    previousRB = buttonView;
                }
            }
        });
        radioButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView)
                {
                    radioButton2.startAnimation(scaleAnimation);
                    //previousRB.startAnimation(reverseScaleAnimation);
                    radioButton2textView.setTextColor(getResources().getColor(R.color.white));
                    radioButton1textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton3textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton4textView.setTextColor(getResources().getColor(R.color.blue));
                    previousRB.setChecked(false);
                    previousRB = (RadioButton) buttonView;
                }
            }
        });
        radioButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView)
                {
                    radioButton3.startAnimation(scaleAnimation);
                    //previousRB.startAnimation(reverseScaleAnimation);
                    radioButton3textView.setTextColor(getResources().getColor(R.color.white));
                    radioButton1textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton2textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton4textView.setTextColor(getResources().getColor(R.color.blue));
                    previousRB.setChecked(false);
                    previousRB = (RadioButton) buttonView;
                }
            }
        });
        radioButton4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (previousRB != buttonView)
                {
                    radioButton4.startAnimation(scaleAnimation);
                    //previousRB.startAnimation(reverseScaleAnimation);
                    radioButton4textView.setTextColor(getResources().getColor(R.color.white));
                    radioButton1textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton2textView.setTextColor(getResources().getColor(R.color.blue));
                    radioButton3textView.setTextColor(getResources().getColor(R.color.blue));
                    previousRB.setChecked(false);
                    previousRB = (RadioButton) buttonView;
                }
            }
        });

        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    stopwatchSwitch.setChecked(false);
                    pickTimeButton.setVisibility(View.VISIBLE);
                    pickTimeButton.setClickable(true);
                    pickTimeButton.startAnimation(pickTimeButtonAnimation);
                }
                else
                {
                    pickTimeButton.startAnimation(pickTimeButtonAnimationReverse);
                    pickTimeButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pickTimeButton.setVisibility(View.INVISIBLE);
                            pickTimeButton.setClickable(false);
                        }
                    }, 90);
                }
            }
        });

        stopwatchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && timerSwitch.isChecked())
                {
                    timerSwitch.setChecked(false);
                    pickTimeButton.startAnimation(pickTimeButtonAnimationReverse);
                    pickTimeButton.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pickTimeButton.setVisibility(View.INVISIBLE);
                            pickTimeButton.setClickable(false);
                        }
                    }, 90);
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

    @Override
    public void getTime(int hours, int minutes, int seconds) {
        //Toast.makeText(this, hours + " " + minutes + " " + seconds, Toast.LENGTH_SHORT).show();
        if (!(hours == 0 && minutes == 0 && seconds == 0))
        {
            timerHours = hours;
            timerMinutes = minutes;
            timerSeconds = seconds;

            pickTimeButton.setText(hours + "ч " + minutes + "м " + seconds + "с");
            pickTimeButton.setBackground(getResources().getDrawable(R.drawable.piked_time_button));
            pickTimeButton.setTextColor(getResources().getColor(R.color.blue));
        }
        else
        {
            pickTimeButton.setText("Выбрать время");
            pickTimeButton.setBackground(getResources().getDrawable(R.drawable.pick_time_button));
            pickTimeButton.setTextColor(getResources().getColor(R.color.white));
        }
    }
}
