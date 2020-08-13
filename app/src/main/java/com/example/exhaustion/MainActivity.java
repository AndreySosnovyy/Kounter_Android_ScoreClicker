package com.example.exhaustion;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
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

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class MainActivity extends AppCompatActivity {

    static boolean active = true;
    static String globalName;
    // тред для обновления времени в CounterData и обновления его в базе данных
    Thread backgroundThread = new Thread(new BackgroundThread());

    @Override
    public void onStart() {
        super.onStart();
        backgroundThread.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
        backgroundThread.interrupt();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        active = true;
    }

    DataBaseHelper dataBaseHelper;
    private static final String TAG = "DEBUG LOGS";
    private static MediaPlayer finishSound, timerSound;
    boolean flagTimerStarted = false, flagStopwatchStarted = false, flagBaseTimeSet = false;
    CounterData currentCounter;
    Button counterButton;
    Toolbar toolbar;
    Chronometer timeScreenHead;
    TextView startTimerTextView, timerPicture, finishTextView, stopwatchTimeAfterFinishTextView;
    Animation upAnimation, fadeInAnimation, fadeOutAnimation;
    CountDownTimer countDownTimer;

    // вешаем меню в toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        // скрываем кнопку поварота экрана (потому что для 1 поля не надо)
        MenuItem item = menu.findItem(R.id.rotation);
        item.setVisible(false);
        return true;
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
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        if (countDownTimer != null) countDownTimer.cancel();
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

    public String[] GetDateAndTime() {
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

    @SuppressLint({"ClickableViewAccessibility", "DefaultLocale", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // значения, переданные из прошлого активити
        final boolean isTimer, isStopwatch;
        final String name = getIntent().getStringExtra("NAME");
        globalName = name;
        final String sStartValue = getIntent().getStringExtra("START_VALUE");
        final int startValue = Integer.parseInt(sStartValue);
        final String sFinishValue = getIntent().getStringExtra("FINISH_VALUE");
        final int finishValue = Integer.parseInt(sFinishValue);
        final String sStepValue = getIntent().getStringExtra("STEP_VALUE");
        final int stepValue = Integer.parseInt(sStepValue);
        final String sIsTimer = getIntent().getStringExtra("TIMER");
        if (sIsTimer.equals("true")) isTimer = true;
        else isTimer = false;
        final String sIsStopwatch = getIntent().getStringExtra("STOPWATCH");
        if (sIsStopwatch.equals("true")) isStopwatch = true;
        else isStopwatch = false;
        final String sTimerHours = getIntent().getStringExtra("TIMER_VALUE_HOURS");
        final int timerHours = Integer.parseInt(sTimerHours);
        final String sTimerMinutes = getIntent().getStringExtra("TIMER_VALUE_MINUTES");
        final int timerMinutes = Integer.parseInt(sTimerMinutes);
        final String sTimerSeconds = getIntent().getStringExtra("TIMER_VALUE_SECONDS");
        final int timerSeconds = Integer.parseInt(sTimerSeconds);

        dataBaseHelper = new DataBaseHelper(this);
        final SQLiteDatabase database = dataBaseHelper.getWritableDatabase();

        final ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.NAME, name);
        contentValues.put(DataBaseHelper.NUMBER, 1);
        contentValues.put(DataBaseHelper.START_VALUE, startValue);
        contentValues.put(DataBaseHelper.FINISH_VALUE, finishValue);
        contentValues.put(DataBaseHelper.STEP_VALUE, stepValue);
        contentValues.put(DataBaseHelper.IS_TIMER, isTimer);
        contentValues.put(DataBaseHelper.IS_STOPWATCH, isStopwatch);
        contentValues.put(DataBaseHelper.TIMER_HOURS, timerHours);
        contentValues.put(DataBaseHelper.TIMER_MINUTES, timerMinutes);
        contentValues.put(DataBaseHelper.TIMER_SECONDS, timerSeconds);
        contentValues.put(DataBaseHelper.CURRENT_VALUE_ONE, startValue);
        contentValues.put(DataBaseHelper.CURRENT_TIME, 0);
        database.insert(DataBaseHelper.COUNTER_TABLE, null, contentValues);
        contentValues.clear();

        // если нет таймера и секундомера, то пропускать условие в обработчике длинного клика перед началом таймера/секундомера
        if (!isStopwatch && !isTimer) {
            flagStopwatchStarted = true;
            flagTimerStarted = true;
        }

        // создание объекта для текущего счетчика
        currentCounter = new CounterData(name, 1, startValue, finishValue, stepValue, isTimer, timerHours, timerMinutes, timerSeconds, isStopwatch);

        // замена actionBar на toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        // действие на кнопку "назад" в toolbar
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_black_24dp));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getLayoutInflater().inflate(R.layout.activity_menu, null);
                if (countDownTimer != null) countDownTimer.cancel();
                finish();
            }
        });

        counterButton = findViewById(R.id.counterButton);
        timeScreenHead = findViewById(R.id.timeScreenHead);
        upAnimation = AnimationUtils.loadAnimation(this, R.anim.up_moving);
        fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out);
        startTimerTextView = findViewById(R.id.startTimerTextView);
        timerPicture = findViewById(R.id.timerPicture);
        finishTextView = findViewById(R.id.finishTextView);
        stopwatchTimeAfterFinishTextView = findViewById(R.id.stopwatchTimeAfterFinishTextView);

        // если есть финиш, то показывать его в отдельном view (слева снизу)
        if (finishValue != Integer.MAX_VALUE) {
            finishTextView.setText("Финиш - " + finishValue);
        }

        // закидываем стартовое значение в счетчик
        counterButton.setText(startValue + "");
        if (startValue > 9999 && startValue < 100000) counterButton.setTextSize(144);

        // обработка обычного клика счетчика
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // установка начального времени для запоминания времени каждого клика (и секундомера)
                if (!flagBaseTimeSet) {
                    timeScreenHead.setBase(SystemClock.elapsedRealtime());
                    flagBaseTimeSet = true;
                }

                // действия, если есть таймер и он еще не начался
                if (isTimer && !flagTimerStarted) {
                    flagTimerStarted = true;
                    startTimerTextView.setVisibility(View.INVISIBLE);
                    //timeScreenHead.setBase(SystemClock.elapsedRealtime());

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
                            assert v != null;
                            v.vibrate(800);

                            timerSound = MediaPlayer.create(getApplicationContext(), R.raw.timer_sound);
                            timerSound.start();
                        }
                    }.start();
                }
                // действия, если есть секундомер и он еще не начался
                else if (isStopwatch && !flagStopwatchStarted) {
                    flagStopwatchStarted = true;
                    startTimerTextView.setVisibility(View.INVISIBLE);
                    //timeScreenHead.setBase(SystemClock.elapsedRealtime());
                    timeScreenHead.start();
                }
                // инкремент счетчика + добавлнеие клика в базу данных + проверка на финиш
                else {
                    int inc = Integer.parseInt(counterButton.getText().toString()) + stepValue;
                    counterButton.setText(String.valueOf(inc));

                    // изменение размера шрифта
                    if (inc > 9999 && inc < 100000) counterButton.setTextSize(144);
                    else if (inc > 99999) counterButton.setTextSize(114);

                    // добавлнеие клика в объект счетчика
                    String[] temp = GetDateAndTime();
                    currentCounter.clickTimeArray.add(new CounterClick(true, SystemClock.elapsedRealtime() - timeScreenHead.getBase(), temp[0], temp[1]));

                    // обновление текущего значения в объкте счетчика
                    currentCounter.setCurrentValueOne(inc);

                    // обновление текущего значения в базе данных
                    contentValues.put(DataBaseHelper.CURRENT_VALUE_ONE, inc);
                    int updCount = database.update(DataBaseHelper.COUNTER_TABLE, contentValues,
                            DataBaseHelper.NAME + " = ?", new String[]{name});
                    contentValues.clear();
                    if (updCount != 1) {
                        Log.d(TAG, "ERROR : UNABLE TO UPDATE CURRENT VALUE");
                    }

                    // добавлнеие клика в базу данных
                    SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
                    contentValues.put(DataBaseHelper.NAME, name);
                    contentValues.put(DataBaseHelper.NUMBER, 1);
                    contentValues.put(DataBaseHelper.TYPE, true);
                    contentValues.put(DataBaseHelper.TIME, currentCounter.clickTimeArray.get(currentCounter.clickTimeArray.size() - 1).time);
                    contentValues.put(DataBaseHelper.STAMP_DATE, currentCounter.clickTimeArray.get(currentCounter.clickTimeArray.size() - 1).stampDate);
                    contentValues.put(DataBaseHelper.STAMP_TIME, currentCounter.clickTimeArray.get(currentCounter.clickTimeArray.size() - 1).stampTime);
                    database.insert(DataBaseHelper.COUNTER_CLICK_TABLE, null, contentValues);
                    contentValues.clear();


                    Cursor cursor = database.query(DataBaseHelper.COUNTER_TABLE, null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        int idIndex = cursor.getColumnIndex(DataBaseHelper.KEY_ID);
                        int nameIndex = cursor.getColumnIndex(DataBaseHelper.NAME);
                        int numberIndex = cursor.getColumnIndex(DataBaseHelper.NUMBER);
                        int startIndex = cursor.getColumnIndex(DataBaseHelper.START_VALUE);
                        int finishIndex = cursor.getColumnIndex(DataBaseHelper.FINISH_VALUE);
                        int stepIndex = cursor.getColumnIndex(DataBaseHelper.STEP_VALUE);
                        int timerIndex = cursor.getColumnIndex(DataBaseHelper.IS_TIMER);
                        int stopwatchIndex = cursor.getColumnIndex(DataBaseHelper.IS_STOPWATCH);
                        int hoursIndex = cursor.getColumnIndex(DataBaseHelper.TIMER_HOURS);
                        int minutesIndex = cursor.getColumnIndex(DataBaseHelper.TIMER_MINUTES);
                        int secondsIndex = cursor.getColumnIndex(DataBaseHelper.TIMER_SECONDS);
                        int currentValueIndex = cursor.getColumnIndex(DataBaseHelper.CURRENT_VALUE_ONE);
                        int currentTimeIndex = cursor.getColumnIndex(DataBaseHelper.CURRENT_TIME);

                        do {
                            Log.d(TAG, "ID = " + cursor.getInt(idIndex) +
                                    ", name = " + cursor.getString(nameIndex) +
                                    ", number = " + cursor.getInt(numberIndex) +
                                    ", start = " + cursor.getInt(startIndex) +
                                    ", finish = " + cursor.getInt(finishIndex) +
                                    ", step = " + cursor.getInt(stepIndex) +
                                    ", timer = " + cursor.getInt(timerIndex) +
                                    ", stopwatch = " + cursor.getInt(stopwatchIndex) +
                                    ", hours = " + cursor.getInt(hoursIndex) +
                                    ", minutes = " + cursor.getInt(minutesIndex) +
                                    ", seconds = " + cursor.getInt(secondsIndex) +
                                    ", value = " + cursor.getInt(currentValueIndex) +
                                    ", time = " + cursor.getInt(currentTimeIndex));
                        } while (cursor.moveToNext());
                    } else
                        Log.d("mLog","0 rows");
                    cursor.close();


                    // проверка на финиш
                    if (Integer.parseInt(counterButton.getText().toString()) >= finishValue) {
                        active = false; // для остановки обновлений
                        counterButton.setEnabled(false);
                        counterButton.setTextColor(getResources().getColor(R.color.green));
                        if (countDownTimer != null) countDownTimer.cancel();
                        if (isTimer || isStopwatch) {
                            timeScreenHead.startAnimation(upAnimation);
                            timeScreenHead.setVisibility(View.INVISIBLE);
                        }

                        // TODO - условие из настроек

                        Vibrator v1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        assert v1 != null;
                        v1.vibrate(800);

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
                        timeScreenHead.stop();

                        // вывод времени секундомера в отдельный view, после достижения финиша
                        if (isStopwatch) {
                            // время секундомера в миллисекундах + перевод в секунды
                            long timeToShow = SystemClock.elapsedRealtime() - timeScreenHead.getBase();
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

                        // ======================================================
                        for (int i = 0; i < currentCounter.clickTimeArray.size(); i++) {
                            CounterClick cc = currentCounter.clickTimeArray.get(i);
                            Log.d(TAG, cc.type + " " + cc.time + " " + cc.stampDate + " " + cc.stampTime);
                        }
                        // ======================================================
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

                    if (dec <= 9999) counterButton.setTextSize(174);
                    else if (dec <= 99999) counterButton.setTextSize(144);

                    // добавление клика в объект счетчика
                    String[] temp = GetDateAndTime();
                    currentCounter.clickTimeArray.add(new CounterClick(false, SystemClock.elapsedRealtime() - timeScreenHead.getBase(), temp[0], temp[1]));

                    // обновлние текущего значения в объекте счетчика
                    currentCounter.setCurrentValueOne(dec);

                    // обновление текущего значения в базе данных
                    contentValues.put(DataBaseHelper.CURRENT_VALUE_ONE, dec);
                    int updCount = database.update(DataBaseHelper.COUNTER_TABLE, contentValues,
                            DataBaseHelper.NAME + " = ?", new String[]{name});
                    contentValues.clear();
                    if (updCount != 1) {
                        Log.d(TAG, "ERROR : UNABLE TO UPDATE CURRENT VALUE");
                    }

                    // добавлние клика в базу данных
                    contentValues.put(DataBaseHelper.NAME, name);
                    contentValues.put(DataBaseHelper.NUMBER, 1);
                    contentValues.put(DataBaseHelper.TYPE, false);
                    contentValues.put(DataBaseHelper.TIME, currentCounter.clickTimeArray.get(currentCounter.clickTimeArray.size() - 1).time);
                    contentValues.put(DataBaseHelper.STAMP_DATE, currentCounter.clickTimeArray.get(currentCounter.clickTimeArray.size() - 1).stampDate);
                    contentValues.put(DataBaseHelper.STAMP_TIME, currentCounter.clickTimeArray.get(currentCounter.clickTimeArray.size() - 1).stampTime);
                    database.insert(DataBaseHelper.COUNTER_CLICK_TABLE, null, contentValues);
                    contentValues.clear();
                }
                return true;
            }
        });

        // инициализация, если есть таймер
        if (isTimer) {
            timeScreenHead.setVisibility(View.VISIBLE);
            startTimerTextView.setVisibility(View.VISIBLE);
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
            timeScreenHead.setVisibility(View.VISIBLE);
            startTimerTextView.setVisibility(View.VISIBLE);
            startTimerTextView.setText("Нажмите, чтобы начать секундомер");
        }
    }


    class BackgroundThread implements Runnable {
        @Override
        public void run() {

            SQLiteDatabase database = dataBaseHelper.getWritableDatabase();
            ContentValues contentValues = new ContentValues();

            while (active) {
                Log.d(TAG, "BACKGROUND THREAD RUN");
                long currentTime = SystemClock.elapsedRealtime() - timeScreenHead.getBase();
                currentCounter.setCurrentTime(currentTime); // обновлнение текущего времени в объекте счетчика

                // обновелние текущего времени счетчика в базе данных
                contentValues.put(DataBaseHelper.CURRENT_TIME, SystemClock.elapsedRealtime() - timeScreenHead.getBase());
                int updCount = database.update(DataBaseHelper.COUNTER_TABLE, contentValues,
                        DataBaseHelper.NAME + " = ?", new String[]{globalName});
                contentValues.clear();
                if (updCount != 1) {
                    Log.d(TAG, "ERROR : UNABLE TO UPDATE CURRENT TIME");
                }

                // задержка
                try {
                    Thread.sleep(330);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

//                Cursor cursor = database.query(DataBaseHelper.COUNTER_CLICK_TABLE, null, null, null, null, null, null);
//                if (cursor.moveToFirst()) {
//                    int idIndex = cursor.getColumnIndex(DataBaseHelper.KEY_ID);
//                    int nameIndex = cursor.getColumnIndex(DataBaseHelper.NAME);
//                    int numberIndex = cursor.getColumnIndex(DataBaseHelper.NUMBER);
//                    int typeIndex = cursor.getColumnIndex(DataBaseHelper.TYPE);
//                    int timeIndex = cursor.getColumnIndex(DataBaseHelper.TIME);
//                    int stampDateIndex = cursor.getColumnIndex(DataBaseHelper.STAMP_DATE);
//                    int stampTimeIndex = cursor.getColumnIndex(DataBaseHelper.STAMP_TIME);
//
//                    do {
//                        Log.d(TAG, "ID = " + cursor.getInt(idIndex) +
//                                ", name = " + cursor.getString(nameIndex) +
//                                ", number = " + cursor.getInt(numberIndex) +
//                                ", type = " + cursor.getInt(typeIndex) +
//                                ", time = " + cursor.getInt(timeIndex) +
//                                ", stampDate = " + cursor.getString(stampDateIndex) +
//                                ", stampTime = " + cursor.getString(stampTimeIndex));
//                    } while (cursor.moveToNext());
//                } else
//                    Log.d("mLog","0 rows");
//                cursor.close();
            }
        }
    }
}
