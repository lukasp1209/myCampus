package com.example.my_campus_core.models;

import java.time.DayOfWeek;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TimeSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;

    public boolean overlapsWith(TimeSlot other) {
        if (this.dayOfWeek != other.dayOfWeek) {
            return false; // Different days → no overlap
        }
        // Correct overlap check:
        return this.startTime.isBefore(other.endTime) &&
                other.startTime.isBefore(this.endTime);
    }
}