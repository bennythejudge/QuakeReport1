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

    // get data from the USGS website
    private ArrayList<EarthQuake> fetchHttpData () throws MalformedURLException {
        ArrayList<EarthQuake> earthQuakes = new ArrayList<EarthQuake>();

        Log.d("QuakeReport1", "insde fetchHttpData");

        HttpsURLConnection myConnection;

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // All your networking logic
                // should be here


            }
        });
        // Create URL
        URL usgsUrl = new URL("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-03-17&endtime=2018-03-17&minmag=4&limit=100");

        // Create connection
        try {
            myConnection =
                    (HttpsURLConnection) usgsUrl.openConnection();
            if (myConnection.getResponseCode() == 200) {
                // Success
                // Further processing here
                Log.d("QuakeReport1", "got a 200!");

            } else {
                // Error handling code goes here
                Log.d("QuakeReport1", "I did NOT get a 200! " + myConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return earthQuakes;
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_report1);

        try {
            fetchHttpData();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

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
