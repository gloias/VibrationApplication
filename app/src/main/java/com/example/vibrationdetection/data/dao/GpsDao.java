package com.example.vibrationdetection.data.dao;

//import android.arch.lifecycle.LiveData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.vibrationdetection.data.entities.Gps;

import java.util.List;


@Dao
public interface GpsDao {
    @Query("SELECT * FROM gps ORDER BY timestamp ASC")
    List<Gps> getAll();

//    @Query("SELECT * FROM gps ORDER BY timestamp ASC")
//    LiveData<List<Gps>> getAllLiveData();

    @Insert
    long[] insertAll(Gps... gps);

    @Delete
    void delete(Gps gps);

    @Query("DELETE FROM gps")
    int deleteAll();
}
