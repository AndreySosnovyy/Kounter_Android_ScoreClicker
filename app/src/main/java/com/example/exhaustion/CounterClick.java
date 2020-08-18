package com.example.exhaustion;

class CounterClick {

    public static final int INC_WITH_CLICK = 0;
    public static final int DEC_WITH_CLICK = 1;
    public static final int ADD_SHIFT = 2;
    public static final int REDUCE_SHIFT = 3;

    public int value; // новое значение, которое УЖЕ записано
    public int type;
    public long time; // время клика с начала отсчета (в миллисекундах)
    public String stampDate; // время клика (дата)
    public String stampTime; // время клика (время)

    public CounterClick(int value, int type, long time, String stampDate, String stampTime)
    {
        this.value = value;
        this.type = type;
        this.time = time;
        this.stampDate = stampDate;
        this.stampTime = stampTime;
    }
}
