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
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // make the items in the list clickable
        

        earthQuakes = QueryUtils.extractEarthquakes();
        Log.v("MAIN", String.valueOf(earthQuakes));

        EarthQuakeAdapter adapter =
                new EarthQuakeAdapter(this, earthQuakes);

        earthquakeListView.setAdapter(adapter);
    }
}
