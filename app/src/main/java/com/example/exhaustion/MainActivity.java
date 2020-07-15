package com.example.exhaustion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "myLogs";
    private static MediaPlayer finishSound, timerSound;
    boolean flagTimerStarted = false;
    boolean flagStopwatchStarted = false;

    Button counterButton;
    Toolbar toolbar;
    Chronometer timeScreenHead;
    TextView startTimerTextView, timerPicture, finishTextView, stopwatchTimeAfterFinishTextView;
    Animation upAndDownAnimation, upAnimation, fadeInAnimation, fadeOutAnimation;
    CountDownTimer countDownTimer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.rotation);
        item.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.reset:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Вы уверены, что хотите перезапустить счетчик?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        if (countDownTimer != null) countDownTimer.cancel();
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

                builder.setCancelable(true);
                builder.create().show();
                break;

            case R.id.info:
                // получить информацию о счетчике (наверно в новом лэйауте)
                break;

            case R.id.rotation:

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
//                EditText nameField = view.findViewById(R.id.nameField);
//                nameField.setText("");
                if (countDownTimer != null) countDownTimer.cancel();
                finish();
            }
        });

        counterButton = findViewById(R.id.counterButton);
        timeScreenHead = findViewById(R.id.timeScreenHead);
        //upAndDownAnimation = AnimationUtils.loadAnimation(this, R.anim.up_down_moving);
        //upAndDownAnimation.setRepeatCount(Animation.INFINITE);
        upAnimation = AnimationUtils.loadAnimation(this, R.anim.up_moving);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        startTimerTextView = findViewById(R.id.startTimerTextView);
        timerPicture = findViewById(R.id.timerPicture);
        finishTextView = findViewById(R.id.finishTextView);
        stopwatchTimeAfterFinishTextView = findViewById(R.id.stopwatchTimeAfterFinishTextView);

        if (Integer.parseInt(finishValue) != Integer.MAX_VALUE)
        {
            finishTextView.setText("Финиш - " + Integer.parseInt(finishValue));
        }

        counterButton.setText(startValue);

        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTimer.equals("true") && !flagTimerStarted)
                {
                    flagTimerStarted = true;
                    startTimerTextView.setVisibility(View.INVISIBLE);

                    int timerTime = Integer.parseInt(timerHours) * 3600000 + Integer.parseInt(timerMinutes) * 60000 + Integer.parseInt(timerSeconds) * 1000;

                    // начало таймера
                    countDownTimer = new CountDownTimer(timerTime, 1000 )
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
                            timerPicture.setVisibility(View.VISIBLE);
                            timerPicture.startAnimation(fadeInAnimation);

                            // условие из настроек

                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            assert v != null;
                            v.vibrate(800);

                            timerSound = MediaPlayer.create(getApplicationContext(), R.raw.timer_sound);
                            timerSound.start();
                        }
                    };
                    countDownTimer.start();
                }
                else if (isStopwatch.equals("true") && !flagStopwatchStarted)
                {
                    flagStopwatchStarted = true;
                    startTimerTextView.setVisibility(View.INVISIBLE);
                    timeScreenHead.setBase(SystemClock.elapsedRealtime());
                    timeScreenHead.start();
                }
                else
                {
                    int inc = Integer.parseInt(counterButton.getText().toString()) + Integer.parseInt(stepValue);
                    counterButton.setText(String.valueOf(inc));

                    if (inc > 9999 && inc < 100000) counterButton.setTextSize(144);
                    else if (inc > 99999) counterButton.setTextSize(114);

                    if (Integer.parseInt(counterButton.getText().toString()) >= Integer.parseInt(finishValue))
                    {
                        counterButton.setEnabled(false);;
                        counterButton.setTextColor(getResources().getColor(R.color.green));
                        if (countDownTimer != null) countDownTimer.cancel();
                        if (isTimer.equals("true") || isStopwatch.equals("true"))
                        {
                            timeScreenHead.startAnimation(upAnimation);
                            timeScreenHead.setVisibility(View.INVISIBLE);
                        }

                        // условие из настроек
                        Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        assert v1 != null;
                        v1.vibrate(800);

                        finishSound = MediaPlayer.create(getApplicationContext(), R.raw.finish_sound);
                        finishSound.start();

                        final KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
                        konfettiView.build()
                                .addColors(getResources().getColor(R.color.red),
                                           getResources().getColor(R.color.blue),
                                           getResources().getColor(R.color.yellow),
                                           getResources().getColor(R.color.lightOrange),
                                           getResources().getColor(R.color.green),
                                           getResources().getColor(R.color.greenBlue))
                                .setDirection(0.0, 359.0)
                                .setSpeed(3f, 8f)
                                .setFadeOutEnabled(true)
                                .setTimeToLive(3000L)
                                .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                                .addSizes(new Size(12, 5f))
                                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                                .streamFor(400, 1000L);

                        finishTextView.startAnimation(fadeOutAnimation);
                        finishTextView.setVisibility(View.INVISIBLE);
                        timeScreenHead.stop();

                        long timeToShow = SystemClock.elapsedRealtime() - timeScreenHead.getBase();
                        int hoursToShow = (int) TimeUnit.MILLISECONDS.toHours(timeToShow);
                        int minutesToShow = (int) TimeUnit.MILLISECONDS.toMinutes(timeToShow) % 60;
                        int secondsToShow = (int) TimeUnit.MILLISECONDS.toSeconds(timeToShow) % 60;

                        if (hoursToShow > 0)
                        {
                            stopwatchTimeAfterFinishTextView.setText(String.format("за %02d:%02d:%02d", hoursToShow, minutesToShow, secondsToShow));
                        }
                        else if (minutesToShow > 0)
                        {
                            stopwatchTimeAfterFinishTextView.setText(String.format("за %02d:%02d", minutesToShow, secondsToShow));
                        }
                        else
                        {
                            if (timeToShow < 1000)
                            {
                                stopwatchTimeAfterFinishTextView.setText(String.format("за 0.%03d", timeToShow));
                            }
                            else
                            {
                                stopwatchTimeAfterFinishTextView.setText(String.format("за 00:%02d", secondsToShow));
                            }
                        }
                        stopwatchTimeAfterFinishTextView.startAnimation(fadeInAnimation);
                    }
                }
            }
        });

        counterButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int dec = Integer.parseInt(counterButton.getText().toString()) - Integer.parseInt(stepValue);
                counterButton.setText(String.valueOf(dec));

                if (dec <= 9999) counterButton.setTextSize(174);
                else if (dec <= 99999) counterButton.setTextSize(144);

                if (Integer.parseInt(counterButton.getText().toString()) <= Integer.parseInt(finishValue))
                {
                    counterButton.setEnabled(false);;
                    counterButton.setTextColor(getResources().getColor(R.color.green));
                    if (countDownTimer != null) countDownTimer.cancel();
                    if (isTimer.equals("true") || isStopwatch.equals("true"))
                    {
                        timeScreenHead.startAnimation(upAnimation);
                        timeScreenHead.setVisibility(View.INVISIBLE);
                    }

                    final KonfettiView konfettiView = findViewById(R.id.viewKonfetti);
                    konfettiView.build()
                            .addColors(getResources().getColor(R.color.red),
                                    getResources().getColor(R.color.blue),
                                    getResources().getColor(R.color.yellow),
                                    getResources().getColor(R.color.lightOrange),
                                    getResources().getColor(R.color.green),
                                    getResources().getColor(R.color.greenBlue))
                            .setDirection(0.0, 359.0)
                            .setSpeed(3f, 8f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(3000L)
                            .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                            .addSizes(new Size(12, 5f))
                            .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                            .streamFor(400, 1000L);

                    timeScreenHead.stop();

                    // условие из настроек
                    Vibrator v2 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    assert v2 != null;
                    v2.vibrate(800);

                    finishSound = MediaPlayer.create(getApplicationContext(), R.raw.finish_sound);
                    finishSound.start();

                    long timeToShow = SystemClock.elapsedRealtime() - timeScreenHead.getBase();
                    int hoursToShow = (int) TimeUnit.MILLISECONDS.toHours(timeToShow);
                    int minutesToShow = (int) TimeUnit.MILLISECONDS.toMinutes(timeToShow) % 60;
                    int secondsToShow = (int) TimeUnit.MILLISECONDS.toSeconds(timeToShow) % 60;

                    if (hoursToShow > 0)
                    {
                        stopwatchTimeAfterFinishTextView.setText(String.format("за %02d:%02d:%02d", hoursToShow, minutesToShow, secondsToShow));
                    }
                    else if (minutesToShow > 0)
                    {
                        stopwatchTimeAfterFinishTextView.setText(String.format("за %02d:%02d", minutesToShow, secondsToShow));
                    }
                    else
                    {
                        if (timeToShow < 1000)
                        {
                            stopwatchTimeAfterFinishTextView.setText(String.format("за 0.%03d", timeToShow));
                        }
                        else
                        {
                            stopwatchTimeAfterFinishTextView.setText(String.format("за 00:%02d", secondsToShow));
                        }
                    }
                    stopwatchTimeAfterFinishTextView.startAnimation(fadeInAnimation);
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
            timeScreenHead.setVisibility(View.VISIBLE);
            startTimerTextView.setVisibility(View.VISIBLE);
            startTimerTextView.setText("Нажмите, чтобы начать секундомер");
        }
    }
}
