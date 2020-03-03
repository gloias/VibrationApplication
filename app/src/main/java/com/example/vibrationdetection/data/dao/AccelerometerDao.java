package com.example.vibrationdetection.data.dao;

//import android.arch.lifecycle.LiveData;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.vibrationdetection.data.entities.Accelerometer;

import java.util.List;


@Dao
public interface AccelerometerDao {
    @Query("SELECT * FROM accelerometer ORDER BY ts ASC")
    List<Accelerometer> getAll();

//    @Query("SELECT * FROM accelerometer ORDER BY ts ASC")
//    LiveData<List<Accelerometer>> getAllLiveData();

    @Insert
    long[] insertAll(Accelerometer... accelerometers);

    @Delete
    void delete(Accelerometer accelerometer);

    @Query("DELETE FROM accelerometer")
    int deleteAll();
}
