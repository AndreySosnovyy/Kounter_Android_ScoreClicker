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
    public final static String CURRENT_VALUE = "currentValue";
    public final static String CURRENT_TIME = "currentTime";

    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + COUNTER_CLICK_TABLE + " (" +
                KEY_ID     + " INTEGER PRIMARY KEY, " +
                NAME       + " TEXT," +
                TYPE       + " INTEGER, " +
                TIME       + " INTEGER, " +
                STAMP_DATE + " TEXT, " +
                STAMP_TIME + " TEXT)");

        db.execSQL("CREATE TABLE " + COUNTER_TABLE + " (" +
                KEY_ID        + " INTEGER PRIMARY KEY, " +
                NAME          + " TEXT," +
                START_VALUE       + " INTEGER, " +
                FINISH_VALUE  + " INTEGER, " +
                STEP_VALUE    + " INTEGER, " +
                IS_TIMER      + " INTEGER, " +
                IS_STOPWATCH  + " INTEGER, " +
                TIMER_HOURS   + " INTEGER, " +
                TIMER_MINUTES + " INTEGER, " +
                TIMER_SECONDS + " INTEGER, " +
                CURRENT_VALUE + " INTEGER, " +
                CURRENT_TIME  + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + COUNTER_CLICK_TABLE);
        db.execSQL("drop table if exists " + COUNTER_TABLE);
        onCreate(db);
    }
}
