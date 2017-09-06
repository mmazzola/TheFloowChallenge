package mmazzola.thefloowchallenge.activity;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public interface MapActivity {

    public void updateMapWithJourney(List<LatLng> draw);

    public void clearMap();
}
