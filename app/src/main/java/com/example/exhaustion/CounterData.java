package com.example.exhaustion;

import java.util.ArrayList;

public class CounterData {

    public String name;
    public int startValue;
    public int finishValue;
    public int stepValue;
    public boolean isTimer;
    public boolean isStopwatch;
    public int timerHours;
    public int timerMinutes;
    public int timerSeconds;
    public int currentValue; // обновлять каждый клик
    public ArrayList<CounterClick> clickTimeArray = new ArrayList<>(); // добавлять объект в списочный массив на каждый клик
    public long currentTime; // периодически обновляется (300 мс) (время от начала счетчика)

    public CounterData(String name, int startValue, int finishValue, int stepValue, boolean isTimer, int timerHours, int timerMinutes, int timerSeconds, boolean isStopwatch) {
        this.name = name;
        this.startValue = startValue;
        this.finishValue = finishValue;
        this.stepValue = stepValue;
        this.isTimer = isTimer;
        this.timerHours = timerHours;
        this.timerMinutes = timerMinutes;
        this.timerSeconds = timerSeconds;
        this.isStopwatch = isStopwatch;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }
}
