package mmazzola.thefloowchallenge.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
}