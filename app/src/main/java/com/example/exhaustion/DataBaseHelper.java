package com.example.exhaustion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;
import android.util.Log;

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
                KEY_ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT," +        // имя счетчика, к которому относится клик
                NUMBER + " INTEGER," +     // номер поля, к которому относится клик
                TYPE + " INTEGER, " +    // тип клика (1 - инкремент, 0 - декремент)
                TIME + " INTEGER, " +    // время соверешния клика (мс) - относительное зн.
                STAMP_DATE + " TEXT, " +       // дата клика (читаемый вид) - абсолютное зн.
                STAMP_TIME + " TEXT)");        // время клика (читаемый вид) - абсолютное зн.

        db.execSQL("CREATE TABLE " + COUNTER_TABLE + " (" +
                KEY_ID + " INTEGER PRIMARY KEY, " +
                NAME + " TEXT," +        // имя счетчика
                NUMBER + " INTEGER," +     // количество полей (1 - 4)
                START_VALUE + " INTEGER, " +    // стартовое занчение
                FINISH_VALUE + " INTEGER, " +    // финишное значение
                STEP_VALUE + " INTEGER, " +    // шаг счетчика
                IS_TIMER + " INTEGER, " +    // есть ли таймер
                IS_STOPWATCH + " INTEGER, " +    // есть ли секундомер
                TIMER_HOURS + " INTEGER, " +    // часы для таймера
                TIMER_MINUTES + " INTEGER, " +    // минуты для таймера
                TIMER_SECONDS + " INTEGER, " +    // секунды для таймера
                CURRENT_VALUE_ONE + " INTEGER, " +    // текущее значение для поля один
                CURRENT_VALUE_TWO + " INTEGER, " +    // текущее значение для поля два
                CURRENT_VALUE_THREE + " INTEGER, " +    // текущее значение для поля три
                CURRENT_VALUE_FOUR + " INTEGER, " +    // текущее значение для поля четыре
                CURRENT_TIME + " INTEGER)");     // текущее время от начала счетчика (мс)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + COUNTER_CLICK_TABLE);
        db.execSQL("drop table if exists " + COUNTER_TABLE);
        onCreate(db);
    }

    private static final String TAG = "DEBUG LOGS";

    public static void printCounters(SQLiteDatabase database) {
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
            Log.d("mLog", "0 rows");
        cursor.close();
    }

    public static void printClicks(SQLiteDatabase database) {
        Cursor cursor = database.query(DataBaseHelper.COUNTER_CLICK_TABLE, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DataBaseHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DataBaseHelper.NAME);
            int numberIndex = cursor.getColumnIndex(DataBaseHelper.NUMBER);
            int typeIndex = cursor.getColumnIndex(DataBaseHelper.TYPE);
            int timeIndex = cursor.getColumnIndex(DataBaseHelper.TIME);
            int stampDateIndex = cursor.getColumnIndex(DataBaseHelper.STAMP_DATE);
            int stampTimeIndex = cursor.getColumnIndex(DataBaseHelper.STAMP_TIME);

            do {
                Log.d(TAG, "ID = " + cursor.getInt(idIndex) +
                        ", name = " + cursor.getString(nameIndex) +
                        ", number = " + cursor.getInt(numberIndex) +
                        ", type = " + cursor.getInt(typeIndex) +
                        ", time = " + cursor.getInt(timeIndex) +
                        ", stampDate = " + cursor.getString(stampDateIndex) +
                        ", stampTime = " + cursor.getString(stampTimeIndex));
            } while (cursor.moveToNext());
            Log.d(TAG, "\n");
        } else
            Log.d("mLog", "0 rows");
        cursor.close();
    }

    public static void printCountersAndClicks(SQLiteDatabase database) {
        printCounters(database);
        printClicks(database);
        Log.d(TAG, "----------------------------------------------------");
    }

    public static void addClick(SQLiteDatabase database, String name, int number, int type, long time, String dateStamp, String timeStamp) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.NAME, name);
        contentValues.put(DataBaseHelper.NUMBER, number);
        contentValues.put(DataBaseHelper.TYPE, type);
        contentValues.put(DataBaseHelper.TIME, time);
        contentValues.put(DataBaseHelper.STAMP_DATE, dateStamp);
        contentValues.put(DataBaseHelper.STAMP_TIME, timeStamp);
        database.insert(DataBaseHelper.COUNTER_CLICK_TABLE, null, contentValues);
        contentValues.clear();
    }

    public static void setCurrentValue(SQLiteDatabase database, int value, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CURRENT_VALUE_ONE, value);
        int updCount = database.update(DataBaseHelper.COUNTER_TABLE, contentValues,
                DataBaseHelper.NAME + " = ?", new String[]{name});
        contentValues.clear();
        if (updCount != 1) {
            Log.d(TAG, "ERROR : UNABLE TO UPDATE CURRENT VALUE");
        }
    }

    public static void setCurrentTime(SQLiteDatabase database, long time, String name) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.CURRENT_TIME, time);
        int updCount = database.update(DataBaseHelper.COUNTER_TABLE, contentValues,
                DataBaseHelper.NAME + " = ?", new String[]{name});
        contentValues.clear();
        if (updCount != 1) {
            Log.d(TAG, "ERROR : UNABLE TO UPDATE CURRENT TIME");
        }
    }

    public static void createNewCounter(SQLiteDatabase database, String name, int number, int startValue, int finishValue, int stepValue,
                                        boolean isTimer, boolean isStopwatch, int timerHours, int timerMinutes, int timerSeconds) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DataBaseHelper.NAME, name);
        contentValues.put(DataBaseHelper.NUMBER, number);
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
    }

    public static void deleteCounter(SQLiteDatabase database, String name) {
        int delCount = database.delete(DataBaseHelper.COUNTER_TABLE, DataBaseHelper.NAME + " = ?", new String[]{name});
        if (delCount != 1) {
            Log.d(TAG, "UNABLE TO DELETE CURRENT COUNTER FROM DATABASE AFTER FINISH() OR DELETED MORE THEN 1");
        }
    }
}
