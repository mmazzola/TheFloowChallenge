package mmazzola.thefloowchallenge.model;

import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mmazzola.thefloowchallenge.dao.JourneyDatabase;

public class Journey {

    private Long id;
    private LatLng startPoint;
    private LatLng endPoint;
    private Date startTime;
    private Date endTime;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private List<LatLng> points;

    private long durationMillis;

    public String getStartTime() {
        return sdf.format(startTime);
    }

    public String getEndTime() {
        return sdf.format(endTime);
    }

    public Journey(Cursor cursor){
        try {
            this.id = cursor.getLong(cursor.getColumnIndexOrThrow(JourneyDatabase.Journeys._ID));
            this.startTime = sdf.parse(cursor.getString(cursor.getColumnIndexOrThrow(JourneyDatabase.Journeys.START_TIME)));
            this.endTime = sdf.parse(cursor.getString(cursor.getColumnIndexOrThrow(JourneyDatabase.Journeys.END_TIME)));
            this.durationMillis = cursor.getLong(cursor.getColumnIndexOrThrow(JourneyDatabase.Journeys.DURATION));
            this.points = new ArrayList<>();
            while(!cursor.isAfterLast() && this.id == cursor.getLong(cursor.getColumnIndexOrThrow(JourneyDatabase.Points.JOURNEY_ID))){
                addPoint(new LatLng(cursor.getDouble(cursor.getColumnIndexOrThrow(JourneyDatabase.Points.LATITUDE)),cursor.getDouble(cursor.getColumnIndexOrThrow(JourneyDatabase.Points.LONGITUDE))));
                cursor.moveToNext();
            }
        } catch (ParseException e) {
            //
        }
    }

    public Journey(LatLng startPoint) {
        this.startPoint = startPoint;
        this.startTime = new Date();
        this.points = new ArrayList<>();
        addPoint(this.startPoint);
    }

    public void addPoint(LatLng point){
        if(this.points != null){
            if(this.points.isEmpty()){
                this.startPoint = point;
            }
            this.points.add(point);
            this.endPoint = point;
        }
    }

    public void endJourney(){
        this.endTime = new Date();
        this.durationMillis = (endTime.getTime()-startTime.getTime());
    }

    public Long getId() {
        return id;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public LatLng getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(LatLng startPoint) {
        this.startPoint = startPoint;
    }

    public LatLng getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(LatLng endPoint) {
        this.endPoint = endPoint;
    }

    public long getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(long durationMillis) {
        this.durationMillis = durationMillis;
    }
}
