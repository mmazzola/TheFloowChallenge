package mmazzola.thefloowchallenge.activity;

import android.support.v7.app.AppCompatActivity;

import mmazzola.thefloowchallenge.model.Journey;

public abstract class MapActivity extends AppCompatActivity{

    public abstract void updateMapWithJourney(Journey j);

    public abstract void clearMap();
}
