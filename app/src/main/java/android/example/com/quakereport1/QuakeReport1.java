package android.example.com.quakereport1;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class QuakeReport1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_report1);

        Log.v("MAIN", "QUAKE REPORT1");

        // create the list of earthquake
        ArrayList<EarthQuake> earthQuakes = new ArrayList<>();

        earthQuakes.add(new EarthQuake(
                "8.9",
                "San Francisco",
                "2012-01-01"));
        earthQuakes.add(new EarthQuake(
                "9.1",
                "NYC",
                "2013-01-01"));
        earthQuakes.add(new EarthQuake(
                "1.9",
                "Bertrange",
                "2014-01-01"));
        earthQuakes.add(new EarthQuake(
                "2.9",
                "Luxembourg",
                "2015-01-01"));
        earthQuakes.add(new EarthQuake(
                "3.9",
                "Los Angelese",
                "2016-01-01"));
        earthQuakes.add(new EarthQuake(
                "4.9",
                "Perugia",
                "2017-01-01"));
        earthQuakes.add(new EarthQuake(
                "5.9",
                "Rome",
                "2018-01-01"));
        earthQuakes.add(new EarthQuake(
                "6.9",
                "Frankfurt",
                "2019-01-01"));

        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        earthQuakes = QueryUtils.extractEarthquakes();
        Log.v("MAIN", String.valueOf(earthQuakes));

        EarthQuakeAdapter adapter =
                new EarthQuakeAdapter(this, earthQuakes);

        earthquakeListView.setAdapter(adapter);
    }
}
