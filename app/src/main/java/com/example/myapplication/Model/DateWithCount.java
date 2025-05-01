package com.example.myapplication.Model;

public class DateWithCount {
    private String date;
    private Long eventCount;

    public DateWithCount(String date, Long eventCount) {
        this.date = date;
        this.eventCount = eventCount;
    }

    public String getDate() {
        return date;
    }

    public Long getEventCount() {
        return eventCount;
    }
}