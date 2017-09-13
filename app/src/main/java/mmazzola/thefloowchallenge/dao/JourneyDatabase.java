package mmazzola.thefloowchallenge.dao;

import android.provider.BaseColumns;

public class JourneyDatabase {
    private JourneyDatabase() {
    }

    public static class Journeys implements BaseColumns {
        public static final String TABLE_NAME = "JOURNEY";
        public static final String START_TIME = "start";
        public static final String END_TIME = "end";
        public static final String DURATION = "duration";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                START_TIME + " TEXT, " +
                END_TIME + " TEXT, " +
                DURATION + " INTEGER" + ")";
    }

    public static class Points implements BaseColumns {
        public static final String TABLE_NAME = "POINTS";
        public static final String LATITUDE = "latitude";
        public static final String LONGITUDE = "longitude";
        public static final String JOURNEY_ID = "journey_id";


        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LATITUDE + " REAL, " +
                LONGITUDE + " REAL, " +
                JOURNEY_ID + " INTEGER, "+
                "FOREIGN KEY(" + JOURNEY_ID + ") REFERENCES " +
                Journeys.TABLE_NAME + "(" + Journeys._ID + ") " + ")";
    }
}