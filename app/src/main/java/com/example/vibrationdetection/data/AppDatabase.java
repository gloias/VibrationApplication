package com.example.vibrationdetection.data;


import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.vibrationdetection.data.dao.AccelerometerDao;
import com.example.vibrationdetection.data.dao.GpsDao;
import com.example.vibrationdetection.data.dao.RoadPointDao;
import com.example.vibrationdetection.data.dao.UploadDao;
import com.example.vibrationdetection.data.entities.Accelerometer;
import com.example.vibrationdetection.data.entities.Gps;
import com.example.vibrationdetection.data.entities.RoadPoint;
import com.example.vibrationdetection.data.entities.Upload;


@Database(entities = {Accelerometer.class, Gps.class, RoadPoint.class, Upload.class}, version = 10)
public abstract class AppDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "RoadQualityDatabase.db";
    public abstract AccelerometerDao accelerometerDao();
    public abstract GpsDao gpsDao();
    public abstract RoadPointDao roadPointDao();
    public abstract UploadDao uploadDao();

    private static AppDatabase appDatabaseInstance = null;

    public synchronized static AppDatabase getInstance(Context context) {
        if (appDatabaseInstance == null) {
            appDatabaseInstance = Room.databaseBuilder(context,
                    AppDatabase.class, AppDatabase.DATABASE_NAME)
                    .addMigrations(
                            MIGRATION_7_8,
                            MIGRATION_8_9,
                            MIGRATION_9_10)
                    .build();
        }
        return appDatabaseInstance;
    }

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Gps " +
                    "ADD COLUMN speed REAL NOT NULL DEFAULT -1;");
            database.execSQL("ALTER TABLE RoadPoint " +
                    "ADD COLUMN speed REAL NOT NULL DEFAULT -1;");
        }
    };

    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Accelerometer RENAME TO TempAccelerometer");
            database.execSQL("ALTER TABLE RoadPoint RENAME TO TempRoadPoint");

            database.execSQL("CREATE TABLE Accelerometer (" +
                    "uid INTEGER NOT NULL PRIMARY KEY, " +
                    "ts INTEGER NOT NULL, " +
                    "ax REAL NOT NULL, " +
                    "ay REAL NOT NULL, " +
                    "az REAL NOT NULL, " +
                    "gx REAL NOT NULL, " +
                    "gy REAL NOT NULL, " +
                    "gz REAL NOT NULL " +
                    ");");
            database.execSQL("INSERT INTO Accelerometer " +
                    "SELECT uid, ts, ax, ay, az, gx, gy, gz " +
                    "FROM TempAccelerometer");

            database.execSQL("CREATE TABLE RoadPoint (" +
                    "uid INTEGER NOT NULL PRIMARY KEY, " +
                    "trip_id INTEGER NOT NULL, " +
                    "interpolated INTEGER NOT NULL, " +
                    "timestamp INTEGER NOT NULL, " +
                    "latitude REAL NOT NULL, " +
                    "longitude REAL NOT NULL," +
                    "provider TEXT, " +
                    "accuracy REAL, " +
                    "altitude REAL, " +
                    "ax REAL, " +
                    "ay REAL, " +
                    "az REAL, " +
                    "gx REAL, " +
                    "gy REAL, " +
                    "gz REAL, " +
                    "duration REAL, " +
                    "distance REAL, " +
                    "speed REAL" +
                    ");");
            database.execSQL("INSERT INTO RoadPoint " +
                    "SELECT uid, trip_id, interpolated, timestamp, latitude, longitude, " +
                    "provider, accuracy, altitude, ax, ay, az, gx, gy, gz, " +
                    "duration, distance, speed " +
                    "FROM TempRoadPoint");

            database.execSQL("DROP TABLE TempAccelerometer");
            database.execSQL("DROP TABLE TempRoadPoint");
        }
    };

    private static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Upload` (" +
                    "`trip_id` INTEGER NOT NULL," +
                    "`reference_id` TEXT," +
                    "PRIMARY KEY(`trip_id`)" +
                    ");");
        }
    };
}
