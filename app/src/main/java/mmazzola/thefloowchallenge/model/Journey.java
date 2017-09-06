package mmazzola.thefloowchallenge.model;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Journey {

    private LatLng startPoint;
    private LatLng endPoint;
    private Date startTime;
    private Date endTime;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private List<LatLng> points;

    private long duration;

    public String getStartTime() {
        return sdf.format(startTime);
    }

    public String getEndTime() {
        return sdf.format(endTime);
    }

    public Journey(LatLng startPoint) {
        this.startPoint = startPoint;
        this.startTime = new Date();
        this.points = new ArrayList<>();
        addPoint(this.startPoint);
    }

    public void addPoint(LatLng point){
        if(this.points != null){
            this.points.add(point);
            this.endPoint = point;
        }
    }

    public void endJourney(){
        this.endTime = new Date();
    }

    public List<LatLng> getPoints() {
        return points;
    }
}
