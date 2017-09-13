package mmazzola.thefloowchallenge.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import mmazzola.thefloowchallenge.model.Journey;

public class JourneyDatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "journeydb";

    public JourneyDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(JourneyDatabase.Journeys.CREATE_TABLE);
        sqLiteDatabase.execSQL(JourneyDatabase.Points.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JourneyDatabase.Points.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + JourneyDatabase.Journeys.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public List<Journey> retrieveJourneys(){
        List<Journey> result = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        final String query = "SELECT * FROM "+JourneyDatabase.Points.TABLE_NAME +" p INNER JOIN "+
                JourneyDatabase.Journeys.TABLE_NAME+" j ON p."+JourneyDatabase.Points.JOURNEY_ID+"=j."+JourneyDatabase.Journeys._ID
                + " ORDER BY p."+JourneyDatabase.Points.JOURNEY_ID;

        Cursor cursor = database.rawQuery(query,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            result.add(new Journey(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return result;
    }

    public void persistJourney(Journey j) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(JourneyDatabase.Journeys.START_TIME, j.getStartTime());
        values.put(JourneyDatabase.Journeys.END_TIME, j.getEndTime());
        values.put(JourneyDatabase.Journeys.DURATION, j.getDurationMillis());
        Long id = database.insert(JourneyDatabase.Journeys.TABLE_NAME, null, values);
        for (LatLng p : j.getPoints()){
            ContentValues v = new ContentValues();
            v.put(JourneyDatabase.Points.LATITUDE, p.latitude);
            v.put(JourneyDatabase.Points.LONGITUDE, p.longitude);
            v.put(JourneyDatabase.Points.JOURNEY_ID,id);
            database.insert(JourneyDatabase.Points.TABLE_NAME, null, v);
        }
    }
}