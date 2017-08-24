package mmazzola.thefloowchallenge.model;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Journey {

    private LatLng startPoint;
    private LatLng endPoint;

    private List<LatLng> points;

    private long duration;

    private PolylineOptions drawoptions = new PolylineOptions().color(Color.RED).width(5);
    private Polyline draw;

    public PolylineOptions getDrawOptions() {
        return drawoptions;
    }

    public Polyline getDraw() {
        return draw;
    }

    public void setDraw(Polyline draw) {
        this.draw = draw;
    }

    public Journey(LatLng startPoint) {
        this.startPoint = startPoint;
        this.points = new ArrayList<>();
        addPoint(this.startPoint);
    }

    public void addPoint(LatLng point){
        if(this.points != null){
            this.points.add(point);
            this.drawoptions.add(point);
            this.endPoint = point;
        }
    }
}
