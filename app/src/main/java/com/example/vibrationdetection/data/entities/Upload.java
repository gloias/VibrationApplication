package com.example.vibrationdetection.data.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Upload {
    @PrimaryKey
    @ColumnInfo(name = "trip_id")
    private long tripId;

    @ColumnInfo(name = "reference_id")
    private String referenceId;

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
}

