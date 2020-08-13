package com.example.exhaustion;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "Kounter";

    public final static String COUNTER_CLICK_TABLE = "CounterClick";
    public final static String KEY_ID = "_id";
    public final static String NAME = "name";
    public final static String NUMBER = "number";
    public final static String TYPE = "type";
    public final static String TIME = "time";
    public final static String STAMP_DATE = "stampDate";
    public final static String STAMP_TIME = "stampTime";

    public final static String COUNTER_TABLE = "Counter";
    public final static String START_VALUE = "startValue";
    public final static String FINISH_VALUE = "finishValue";
    public final static String STEP_VALUE = "stepValue";
    public final static String IS_TIMER = "isTimer";
    public final static String IS_STOPWATCH = "isStopwatch";
    public final static String TIMER_HOURS = "timerHours";
    public final static String TIMER_MINUTES = "timerMinutes";
    public final static String TIMER_SECONDS = "timerSeconds";
    public final static String CURRENT_VALUE_ONE = "currentValue1";
    public final static String CURRENT_VALUE_TWO = "currentValue2";
    public final static String CURRENT_VALUE_THREE = "currentValue3";
    public final static String CURRENT_VALUE_FOUR = "currentValue4";
    public final static String CURRENT_TIME = "currentTime";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + COUNTER_CLICK_TABLE + " (" +
                KEY_ID     + " INTEGER PRIMARY KEY, " +
                NAME       + " TEXT," +        // имя счетчика, к которому относится клик
                NUMBER     + " INTEGER," +     // номер поля, к которому относится клик
                TYPE       + " INTEGER, " +    // тип клика (1 - инкремент, 0 - декремент)
                TIME       + " INTEGER, " +    // время соверешния клика (мс) - относительное зн.
                STAMP_DATE + " TEXT, " +       // дата клика (читаемый вид) - абсолютное зн.
                STAMP_TIME + " TEXT)");        // время клика (читаемый вид) - абсолютное зн.

        db.execSQL("CREATE TABLE " + COUNTER_TABLE + " (" +
                KEY_ID              + " INTEGER PRIMARY KEY, " +
                NAME                + " TEXT," +        // имя счетчика
                NUMBER              + " INTEGER," +     // количество полей (1 - 4)
                START_VALUE         + " INTEGER, " +    // стартовое занчение
                FINISH_VALUE        + " INTEGER, " +    // финишное значение
                STEP_VALUE          + " INTEGER, " +    // шаг счетчика
                IS_TIMER            + " INTEGER, " +    // есть ли таймер
                IS_STOPWATCH        + " INTEGER, " +    // есть ли секундомер
                TIMER_HOURS         + " INTEGER, " +    // часы для таймера
                TIMER_MINUTES       + " INTEGER, " +    // минуты для таймера
                TIMER_SECONDS       + " INTEGER, " +    // секунды для таймера
                CURRENT_VALUE_ONE   + " INTEGER, " +    // текущее значение для поля один
                CURRENT_VALUE_TWO   + " INTEGER, " +    // текущее значение для поля два
                CURRENT_VALUE_THREE + " INTEGER, " +    // текущее значение для поля три
                CURRENT_VALUE_FOUR  + " INTEGER, " +    // текущее значение для поля четыре
                CURRENT_TIME        + " INTEGER)");     // текущее время от начала счетчика (мс)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + COUNTER_CLICK_TABLE);
        db.execSQL("drop table if exists " + COUNTER_TABLE);
        onCreate(db);
    }
}
