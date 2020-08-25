package com.example.exhaustion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity {

    // поток для обновления времени в CounterData и обновления его в базе данных
    Thread backgroundThread = new Thread(new BackgroundThread());

    @Override
    public void onStart() {
        super.onStart();
        if (!backgroundThread.isAlive()) {
            backgroundThread.setName("backgroundThread");
            backgroundThread.start();
        }
    }

    // вешаем меню в toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // скрываем кнопку поварота экрана (потому что для 1 поля не надо)
        MenuItem item = menu.findItem(R.id.rotation);
        item.setVisible(false);
        return true;
    }

    @Override
    public void onBackPressed() {
        backgroundThread.interrupt();
        finish();
    }

    // назначение действий на кнопки меню в toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Вы уверены, что хотите перезапустить счетчик?");
                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (countDownTimer != null)
                            countDownTimer.cancel(); // отмена таймера, потому что он работает независимо от MainActivity

                        //DataBaseHelper.deleteCounter(dataBaseHelper.getWritableDatabase(), globalName);
                        DataBaseHelper.restartCounter(dataBaseHelper.getWritableDatabase(), globalName);

                        backgroundThread.interrupt();
                        finish();
                        startActivity(getIntent());
                        overridePendingTransition(R.anim.fade_in_600, R.anim.fade_out_600);
                    }
                });
                builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

                builder.setCancelable(true);
                builder.create().show();
                break;

            case R.id.info:
                // получить информацию о счетчике
                break;

            case R.id.rotation:
                // изменить ориентацию экрана (для 2, 3, 4 полей)
                break;
        }
        return true;
    }

    public static String[] GetDateAndTime() {
        TimeZone tz = TimeZone.getDefault();
        Calendar calendar = GregorianCalendar.getInstance(tz);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH) + 1;
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int min = calendar.get(Calendar.MINUTE);
        final int sec = calendar.get(Calendar.SECOND);
        @SuppressLint("DefaultLocale") String date = String.format("%02d.%02d.%d", day, month, year);
        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d:%02d", hour, min, sec);
        return new String[]{date, time};
    }

    private void setActualTextSize(int value, Button counterButton) {
        if (value < -9_999) counterButton.setTextSize(144);
        else if (value > -9_999 && value < 9_999) counterButton.setTextSize(174);
        else if (value > 9_999 && value <= 99_999) counterButton.setTextSize(144);
        else if (value > 99_999) counterButton.setTextSize(114);
    }

    DataBaseHelper dataBaseHelper;
    private static final String TAG = "DEBUG LOGS";
    private static MediaPlayer finishSound, timerSound;
    boolean flagTimerStarted = false, flagStopwatchStarted = false;
    Button counterButton;
    Toolbar toolbar;
    //TextView timeScreenHead;
    Chronometer timeScreenHead;
    TextView startTimerTextView, timerPicture, finishTextView, stopwatchTimeAfterFinishTextView;
    Animation upAnimation, fadeInAnimation, fadeOutAnimation;
    CountDownTimer countDownTimer;
    View indicator;

    static boolean active = true;
    static String globalName;

    long timeAtStart;
    int currentValue;
    long currentTime = 0;

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        counterButton = findViewById(R.id.counterButton);
        timeScreenHead = findViewById(R.id.timeScreenHead);
        upAnimation = AnimationUtils.loadAnimation(this, R.anim.up_moving);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        startTimerTextView = findViewById(R.id.startTimerTextView);
        timerPicture = findViewById(R.id.timerPicture);
        finishTextView = findViewById(R.id.finishTextView);
        stopwatchTimeAfterFinishTextView = findViewById(R.id.stopwatchTimeAfterFinishTextView);
        indicator = findViewById(R.id.indicator);

        // по идее, значит, что поток 100% завершится после завершения основного потока
        backgroundThread.setDaemon(true);

        dataBaseHelper = new DataBaseHelper(this);
        final SQLiteDatabase database = dataBaseHelper.getWritableDatabase();

        // значения, переданные из прошлого активити (создание или открытие)
        final boolean extraFlag = getIntent().getBooleanExtra("FLAG", false);
        final String name = getIntent().getStringExtra("NAME");
        globalName = name;
        final int startValue = getIntent().getIntExtra("START_VALUE", 0);
        final int finishValue = getIntent().getIntExtra("FINISH_VALUE", Integer.MAX_VALUE);
        final int stepValue = getIntent().getIntExtra("STEP_VALUE", 1);
        final boolean isTimer = getIntent().getBooleanExtra("TIMER", false);
        final boolean isStopwatch = getIntent().getBooleanExtra("STOPWATCH", false);
        final int timerHours = getIntent().getIntExtra("TIMER_VALUE_HOURS", 0);
        final int timerMinutes = getIntent().getIntExtra("TIMER_VALUE_MINUTES", 0);
        final int timerSeconds = getIntent().getIntExtra("TIMER_VALUE_SECONDS", 0);

        // замена actionBar на toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        // действие на кнопку "назад" в toolbar
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countDownTimer != null) countDownTimer.cancel();
                active = false;
                backgroundThread.interrupt();
                finish();
            }
        });

        // если нет таймера и секундомера, то пропускать условие в обработчике длинного клика перед началом таймера/секундомера
        if (!isStopwatch && !isTimer) {
            flagStopwatchStarted = true;
            flagTimerStarted = true;
            active = true;
            timeAtStart = SystemClock.elapsedRealtime();
        }

        // инициализация, если есть таймер
        if (isTimer) {
            active = false;
            timeScreenHead.setVisibility(View.VISIBLE);
            startTimerTextView.setVisibility(View.VISIBLE);
            if (extraFlag) {
                startTimerTextView.setText("Нажмите, чтобы продолжить таймер");
            } else {
                startTimerTextView.setText("Нажмите, чтобы начать таймер");
            }

            if (timerHours > 0) {
                timeScreenHead.setText(String.format("%02d:%02d:%02d", timerHours, timerMinutes, timerSeconds));
            } else if (timerMinutes > 0) {
                timeScreenHead.setText(String.format("%02d:%02d", timerMinutes, timerSeconds));
            } else {
                timeScreenHead.setText(String.format("%d", timerSeconds));
            }
        }
        // инициализация, если есть секундомер
        else if (isStopwatch) {
            active = false;
            timeScreenHead.setVisibility(View.VISIBLE);
            startTimerTextView.setVisibility(View.VISIBLE);
            if (extraFlag) {
                startTimerTextView.setText("Нажмите, чтобы продолжить секундомер");
            } else {
                startTimerTextView.setText("Нажмите, чтобы начать секундомер");
            }

        }

        // если данные переданы при открытии существующего сетчика (не создании)
        if (extraFlag) {
            Cursor cursor = database.query(DataBaseHelper.COUNTER_TABLE, null, DataBaseHelper.NAME + " = ?",
                    new String[]{name}, null, null, null);
            if (cursor.moveToFirst()) {
                currentValue = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CURRENT_VALUE_ONE));
                currentTime = cursor.getInt(cursor.getColumnIndex(DataBaseHelper.CURRENT_TIME));
            }
            cursor.close();
            counterButton.setText(String.valueOf(currentValue));

            if (currentValue >= finishValue) {
                active = false; // для остановки обновлений
                counterButton.setEnabled(false);
                counterButton.setTextColor(getResources().getColor(R.color.green));
                finishTextView.setVisibility(View.INVISIBLE);
                timeScreenHead.setVisibility(View.INVISIBLE);
                startTimerTextView.setVisibility(View.INVISIBLE);

                if (isStopwatch) {
                    //время секундомера в миллисекундах + перевод в секунды
                    long timeToShow = currentTime;
                    int hoursToShow = (int) TimeUnit.MILLISECONDS.toHours(timeToShow);
                    int minutesToShow = (int) TimeUnit.MILLISECONDS.toMinutes(timeToShow) % 60;
                    int secondsToShow = (int) TimeUnit.MILLISECONDS.toSeconds(timeToShow) % 60;

                    if (hoursToShow > 0) {
                        stopwatchTimeAfterFinishTextView.setText(String.format("за %02d:%02d:%02d", hoursToShow, minutesToShow, secondsToShow));
                    } else if (minutesToShow > 0) {
                        stopwatchTimeAfterFinishTextView.setText(String.format("за %02d:%02d", minutesToShow, secondsToShow));
                    } else {
                        if (timeToShow < 1000) {
                            stopwatchTimeAfterFinishTextView.setText(String.format("за 0.%03d", timeToShow));
                        } else {
                            stopwatchTimeAfterFinishTextView.setText(String.format("за 00:%02d", secondsToShow));
                        }
                    }
                }
            } else {
                if (isStopwatch) {
                    long timeGone = currentTime;
                    int hoursGone = (int) TimeUnit.MILLISECONDS.toHours(timeGone);
                    int minutesGone = (int) TimeUnit.MILLISECONDS.toMinutes(timeGone) % 60;
                    int secondsGone = (int) TimeUnit.MILLISECONDS.toSeconds(timeGone) % 60;
                    if (hoursGone > 0) {
                        timeScreenHead.setText(String.format("%02d:%02d:%02d", hoursGone, minutesGone, secondsGone));
                    } else {
                        timeScreenHead.setText(String.format("%02d:%02d", minutesGone, secondsGone));
                    }
                }

                if (isTimer) {
                    // TODO -  закидывать актуальное время в заголовок
                }
            }
        } else { // если счетчик только создался, а не открылся
            String[] temp = GetDateAndTime();
            String timeOfCreation = temp[0] + " " + temp[1];

            // добавлние нового счетчика в базу данных
            DataBaseHelper.createNewCounter(database, name, 1, startValue, finishValue, stepValue, isTimer, isStopwatch,
                    timerHours, timerMinutes, timerSeconds, timeOfCreation);

            // если есть финиш, то показывать его в отдельном view (слева снизу)
            if (finishValue != Integer.MAX_VALUE) {
                finishTextView.setText("Финиш - " + finishValue);
            }

            // закидываем стартовое значение в счетчик
            counterButton.setText(String.valueOf(startValue));
            if (startValue > 9999 && startValue < 100000) counterButton.setTextSize(144);
        }


        // обработка обычного клика счетчика
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // действия, если есть таймер и он еще не начался
                if (isTimer && !flagTimerStarted) {
                    active = true;
                    timeAtStart = SystemClock.elapsedRealtime();
                    flagTimerStarted = true;
                    startTimerTextView.setVisibility(View.INVISIBLE);

                    // TODO - вычислять значение времени для таймера, учитывая currentTime
                    // TODO - выводить в timeScreenHead актуальное значение времени на момент старта таймера
                    // TODO - если закончилось время таймера

                    int timerTime = timerHours * 3600000 + timerMinutes * 60000 + timerSeconds * 1000;

                    // начало таймера
                    countDownTimer = new CountDownTimer(timerTime, 1000) {
                        @SuppressLint({"SetTextI18n", "DefaultLocale"})
                        @Override
                        public void onTick(long millisUntilFinished) {
                            millisUntilFinished += 1000;
                            int hoursToGo = (int) TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                            int minutesToGo = (int) TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60;
                            int secondsToGo = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;

                            if (hoursToGo > 0) {
                                timeScreenHead.setText(String.format("%02d:%02d:%02d", hoursToGo, minutesToGo, secondsToGo));
                            } else if (minutesToGo > 0) {
                                timeScreenHead.setText(String.format("%02d:%02d", minutesToGo, secondsToGo));
                            } else {
                                timeScreenHead.setText(String.format("%d", secondsToGo));
                            }
                        }

                        @Override
                        public void onFinish() {
                            active = false; // для остановки обновлений
                            timeScreenHead.setText("0");
                            timeScreenHead.startAnimation(upAnimation);
                            timeScreenHead.setVisibility(View.INVISIBLE);
                            counterButton.setEnabled(false);
                            counterButton.setTextColor(getResources().getColor(R.color.red));
                            timerPicture.setVisibility(View.VISIBLE);
                            timerPicture.startAnimation(fadeInAnimation);
                            finishTextView.startAnimation(fadeOutAnimation);
                            finishTextView.setVisibility(View.INVISIBLE);

                            // TODO - условие из настроек

                            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            if (v != null) v.vibrate(800);

                            timerSound = MediaPlayer.create(getApplicationContext(), R.raw.timer_sound);
                            timerSound.start();
                        }
                    }.start();
                }
                // действия, если есть секундомер и он еще не начался
                else if (isStopwatch && !flagStopwatchStarted) {
                    active = true;
                    flagStopwatchStarted = true;
                    timeAtStart = SystemClock.elapsedRealtime();
                    startTimerTextView.setVisibility(View.INVISIBLE);
                    timeScreenHead.setBase(SystemClock.elapsedRealtime() - currentTime);
                    timeScreenHead.start();
                }
                // инкремент счетчика + добавлнеие клика в базу данных + проверка на финиш
                else {
                    int inc = Integer.parseInt(counterButton.getText().toString()) + stepValue;
                    counterButton.setText(String.valueOf(inc));

                    // изменение размера шрифта
                    setActualTextSize(inc, counterButton);

                    // обновление текущего значения в базе данных
                    DataBaseHelper.setCurrentValue(database, inc, name);

                    String[] temp = GetDateAndTime();
                    // добавлние клика в базу данных
                    DataBaseHelper.addClick(database, name, 1, CounterClick.DEC_WITH_CLICK,
                            SystemClock.elapsedRealtime() - timeAtStart, temp[0], temp[1]);

                    DataBaseHelper.printCountersAndClicks(database);

                    // проверка на финиш
                    if (Integer.parseInt(counterButton.getText().toString()) >= finishValue) {
                        active = false; // для остановки обновлений
                        counterButton.setEnabled(false);
                        counterButton.setTextColor(getResources().getColor(R.color.green));
                        if (countDownTimer != null) countDownTimer.cancel();
                        if (isTimer || isStopwatch) {
                            timeScreenHead.startAnimation(upAnimation);
                            timeScreenHead.setVisibility(View.INVISIBLE);
                            timeScreenHead.stop();
                        }

                        // TODO - условие из настроек

                        Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        if (v1 != null) v1.vibrate(800);

                        finishSound = MediaPlayer.create(getApplicationContext(), R.raw.finish_sound);
                        finishSound.start();

                        // анимация конфетти
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

                        // вывод времени секундомера в отдельный view, после достижения финиша
                        if (isStopwatch) {
                            // время секундомера в миллисекундах + перевод в секунды
                            long timeToShow = currentTime + SystemClock.elapsedRealtime() - timeAtStart;
                            int hoursToShow = (int) TimeUnit.MILLISECONDS.toHours(timeToShow);
                            int minutesToShow = (int) TimeUnit.MILLISECONDS.toMinutes(timeToShow) % 60;
                            int secondsToShow = (int) TimeUnit.MILLISECONDS.toSeconds(timeToShow) % 60;

                            if (hoursToShow > 0) {
                                stopwatchTimeAfterFinishTextView.setText(String.format("за %02d:%02d:%02d", hoursToShow, minutesToShow, secondsToShow));
                            } else if (minutesToShow > 0) {
                                stopwatchTimeAfterFinishTextView.setText(String.format("за %02d:%02d", minutesToShow, secondsToShow));
                            } else {
                                if (timeToShow < 1000) {
                                    stopwatchTimeAfterFinishTextView.setText(String.format("за 0.%03d", timeToShow));
                                } else {
                                    stopwatchTimeAfterFinishTextView.setText(String.format("за 00:%02d", secondsToShow));
                                }
                            }
                            stopwatchTimeAfterFinishTextView.startAnimation(fadeInAnimation);
                        }
                    }
                }
            }
        });


        // обработчик длинного нажатия
        counterButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (flagTimerStarted || flagStopwatchStarted) {
                    int dec = Integer.parseInt(counterButton.getText().toString()) - stepValue;
                    counterButton.setText(String.valueOf(dec));

                    // изменение размера шрифта
                    setActualTextSize(dec, counterButton);
                    // обновление текущего значения в базе данных
                    DataBaseHelper.setCurrentValue(database, dec, name);

                    String[] temp = GetDateAndTime();
                    // добавлние клика в базу данных
                    DataBaseHelper.addClick(database, name, 1, CounterClick.DEC_WITH_CLICK,
                            SystemClock.elapsedRealtime() - timeAtStart, temp[0], temp[1]);
                }
                return true;
            }
        });
    }


    class BackgroundThread implements Runnable {
        @Override
        public void run() {
            SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
            while (Helper.isAppRunning(getApplicationContext(), "com.example.exhaustion")) {
                indicator.setBackgroundColor(getResources().getColor(R.color.red));
                try {
                    if (active) {
                        indicator.setBackgroundColor(getResources().getColor(R.color.green));
                        // обновелние текущего времени счетчика в базе данных
                        long newTime = currentTime + SystemClock.elapsedRealtime() - timeAtStart;
                        DataBaseHelper.setCurrentTime(database, newTime, globalName);
                    }
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.d(TAG, "backgroundThread: interrupted");
                    break;
                }
            }
        }
    }
}
