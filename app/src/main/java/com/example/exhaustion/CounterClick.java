package com.example.exhaustion;

class CounterClick {

    public boolean type; // false - декремент, true - инкремент
    public long time; // время клика с начала отсчета (в миллисекундах)
    public String stampDate; // время клика (дата)
    public String stampTime; // время клика (время)

    public CounterClick(boolean type, long time, String stampDate, String stampTime)
    {
        this.type = type;
        this.time = time;
        this.stampDate = stampDate;
        this.stampTime = stampTime;
    }
}
