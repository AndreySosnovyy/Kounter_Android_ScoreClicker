package com.example.exhaustion;

import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class CounterData {

    public String name;
    public int number;
    public int startValue;
    public int finishValue;
    public int stepValue;
    public boolean isTimer;
    public boolean isStopwatch;
    public int timerHours;
    public int timerMinutes;
    public int timerSeconds;
    public int currentValueOne;
    public int currentValueTwo;
    public int currentValueThree;
    public int currentValueFour;
    public ArrayList<CounterClick> clickTimeArray = new ArrayList<>(); // добавлять объект в списочный массив на каждый клик
    public long currentTime; // периодически обновляется (300 мс) (время от начала счетчика)

    public CounterData(String name, int number, int startValue, int finishValue, int stepValue, boolean isTimer, int timerHours, int timerMinutes, int timerSeconds, boolean isStopwatch) {
        this.name = name;
        this.number = number;
        this.startValue = startValue;
        this.finishValue = finishValue;
        this.stepValue = stepValue;
        this.isTimer = isTimer;
        this.timerHours = timerHours;
        this.timerMinutes = timerMinutes;
        this.timerSeconds = timerSeconds;
        this.isStopwatch = isStopwatch;
    }

    public void setCurrentValueOne(int currentValueOne) {
        this.currentValueOne = currentValueOne;
    }

    public void setCurrentValueTwo(int currentValueTwo) {
        this.currentValueTwo = currentValueTwo;
    }

    public void setCurrentValueThree(int currentValueThree) {
        this.currentValueThree = currentValueThree;
    }

    public void setCurrentValueFour(int currentValueFour) {
        this.currentValueFour = currentValueFour;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
