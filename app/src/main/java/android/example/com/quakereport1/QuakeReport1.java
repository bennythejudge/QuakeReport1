package android.example.com.quakereport1;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class QuakeReport1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_report1);

        // start the http client thread
        new RetrieveDataTask().execute();

        Log.v("myOwnWork", "after call to RetrieveDataTask");

        // this is where we used to get the earthquakes from the hardcoded array
        final ArrayList<EarthQuake> earthQuakes = QueryUtils.extractEarthquakes();

        Log.v("MAIN", String.valueOf(earthQuakes));

        Log.v("MAIN", "QUAKE REPORT1");

        // create the list of earthquake
        ListView earthquakeListView = (ListView) findViewById(R.id.list);

        // make the items in the list clickable
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // when clicked send create intent to open url
                String url = earthQuakes.get(i).getUrl();
                Toast.makeText(getApplicationContext(),
                        "intent at " + url,
                        Toast.LENGTH_SHORT).show();
                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW);
                openUrlIntent.setData(Uri.parse(url));
                startActivity(openUrlIntent);
            }
        });


        EarthQuakeAdapter adapter =
                new EarthQuakeAdapter(this, earthQuakes);

        earthquakeListView.setAdapter(adapter);
    }
}
