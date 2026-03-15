package com.raju.getmyshow.booking.service;

import com.raju.getmyshow.booking.entity.SeatInventory;

import java.util.function.Function;

public class TestImpl implements Function<SeatInventory, Long> {

    @Override
    public Long apply(SeatInventory seatInventory) {
        return seatInventory.getSeatId();
    }
}
