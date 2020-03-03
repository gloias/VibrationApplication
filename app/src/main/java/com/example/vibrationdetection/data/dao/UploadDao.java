package com.example.vibrationdetection.data.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.vibrationdetection.data.entities.Upload;

import java.util.List;


@Dao
public interface UploadDao {

    @Query("SELECT * FROM upload WHERE reference_id NOT NULL")
    List<Upload> getAllUploads();

    @Insert
    void insertAll(Upload... uploads);

    @Delete
    void delete(Upload upload);
}
