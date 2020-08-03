package com.example.ungdungcattoc.Interface;

import com.example.ungdungcattoc.Models.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList);
    void onTimeSlotLoadEmpty();
}
