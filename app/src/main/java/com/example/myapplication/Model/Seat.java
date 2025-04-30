package com.example.myapplication.Model;

public class Seat {
    public enum SeatStatus {
        AVAILABLE,
        SELECTED,
        UNAVAILABLE
    }

    private String name;
    private SeatStatus status;

    public Seat(String name, SeatStatus status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}

