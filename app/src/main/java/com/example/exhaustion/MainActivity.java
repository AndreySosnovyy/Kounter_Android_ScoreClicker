package com.example.exhaustion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myLogs";
    boolean flagTimerStarted = false;

    Button counterButton;
    Toolbar toolbar;
    TextView timeScreenHead, startTimerTextView, timerPicture;
    Animation upAndDownAnimation, upAnimation, fadeInAnimation;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.reset:
                Intent intent = getIntent();
                finish();
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                break;

            case R.id.info:
                // получить информацию о счетчике (наверно в новом лэйауте)
                break;
        }
        return true;
    }

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String name = getIntent().getStringExtra("NAME");
        final String startValue = getIntent().getStringExtra("START_VALUE");
        final String finishValue = getIntent().getStringExtra("FINISH_VALUE");
        final String stepValue = getIntent().getStringExtra("STEP_VALUE");
        final String isTimer = getIntent().getStringExtra("TIMER");
        final String isStopwatch = getIntent().getStringExtra("STOPWATCH");
        final String timerHours = getIntent().getStringExtra("TIMER_VALUE_HOURS");
        final String timerMinutes = getIntent().getStringExtra("TIMER_VALUE_MINUTES");
        final String timerSeconds = getIntent().getStringExtra("TIMER_VALUE_SECONDS");

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);


        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.activity_menu, null);
                EditText nameField = view.findViewById(R.id.nameField);
                nameField.setText("");
                finish();
            }
        });

        counterButton = findViewById(R.id.counterButton);
        timeScreenHead = findViewById(R.id.timeScreenHead);
        //upAndDownAnimation = AnimationUtils.loadAnimation(this, R.anim.up_down_moving);
        //upAndDownAnimation.setRepeatCount(Animation.INFINITE);
        upAnimation = AnimationUtils.loadAnimation(this, R.anim.up_moving);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        startTimerTextView = findViewById(R.id.startTimerTextView);
        timerPicture = findViewById(R.id.timerPicture);

        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimer.equals("true"))
                {
                    if (!flagTimerStarted) // первый клик, чтобы начать таймер
                    {
                        flagTimerStarted = true;
                        startTimerTextView.setVisibility(View.INVISIBLE);

                        int timerTime = Integer.parseInt(timerHours) * 3600000 + Integer.parseInt(timerMinutes) * 60000 + Integer.parseInt(timerSeconds) * 1000;

                        // начало таймера
                        new CountDownTimer(timerTime, 1000 )
                        {
                            @SuppressLint({"SetTextI18n", "DefaultLocale"})
                            @Override
                            public void onTick(long millisUntilFinished) {
                                millisUntilFinished += 1000;
                                int hoursToGo = (int) TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                                int minutesToGo = (int) TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                                int secondsToGo = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                                if (hoursToGo > 0)
                                {
                                    timeScreenHead.setText(String.format("%02d:%02d:%02d", hoursToGo, minutesToGo, secondsToGo));
                                }
                                else if (minutesToGo > 0)
                                {
                                    timeScreenHead.setText(String.format("%02d:%02d", minutesToGo, secondsToGo));
                                }
                                else
                                {
                                    timeScreenHead.setText(String.format("%d", secondsToGo));
                                }
                            }

                            @Override
                            public void onFinish() {
                                timeScreenHead.setText("0");
                                timeScreenHead.startAnimation(upAnimation);
                                timeScreenHead.setVisibility(View.INVISIBLE);
                                counterButton.setEnabled(false);;
                                counterButton.setTextColor(getResources().getColor(R.color.red));
                                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                v.vibrate(800);
                                timerPicture.setVisibility(View.VISIBLE);
                                timerPicture.startAnimation(fadeInAnimation);
                            }
                        }.start();
                    }
                    else
                    {
                        int inc = Integer.parseInt(counterButton.getText().toString()) + 1;
                        counterButton.setText(String.valueOf(inc));
                        if (inc > 9999)
                        {
                            counterButton.setTextSize(144);
                        }
                    }
                }
                else
                {
                    int inc = Integer.parseInt(counterButton.getText().toString()) + 1;
                    counterButton.setText(String.valueOf(inc));
                    if (inc > 9999)
                    {
                        counterButton.setTextSize(144);
                    }
                }
            }
        });

        counterButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int dec = Integer.parseInt(counterButton.getText().toString()) - 1;
                counterButton.setText(String.valueOf(dec));
                if (dec <= 9999)
                {
                    counterButton.setTextSize(174);
                }
                return true;
            }
        });

        if (isTimer.equals("true"))
        {
            timeScreenHead.setVisibility(View.VISIBLE);
            startTimerTextView.setVisibility(View.VISIBLE);
            if (Integer.parseInt(timerHours) > 0)
            {
                timeScreenHead.setText(String.format("%02d:%02d:%02d", Integer.parseInt(timerHours), Integer.parseInt(timerMinutes), Integer.parseInt(timerSeconds)));
            }
            else if (Integer.parseInt(timerMinutes) > 0)
            {
                timeScreenHead.setText(String.format("%02d:%02d", Integer.parseInt(timerMinutes), Integer.parseInt(timerSeconds)));
            }
            else
            {
                timeScreenHead.setText(String.format("%d", Integer.parseInt(timerSeconds)));
            }
        }
        else if (isStopwatch.equals("true"))
        {

        }

//        Log.d(TAG, "NAME = " + name);
//        Log.d(TAG, "START = " + startValue);
//        Log.d(TAG, "FINISH = " + finishValue);
//        Log.d(TAG, "STEP = " + stepValue);
//        Log.d(TAG, "TIMER = " + isTimer);
//        Log.d(TAG, "STOPWATCH = " + isStopwatch);
//        Log.d(TAG, "HOURS = " + timerHours);
//        Log.d(TAG, "MINUTES = " + timerMinutes);
//        Log.d(TAG, "SECONDS = " + timerSeconds);
    }
}
