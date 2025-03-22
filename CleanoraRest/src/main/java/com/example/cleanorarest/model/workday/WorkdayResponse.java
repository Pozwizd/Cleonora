package com.example.cleanorarest.model.workday;

import com.example.cleanorarest.model.timeSlot.TimeSlotResponse;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class WorkdayResponse {
    private Long id;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<TimeSlotResponse> timeSlots;
}