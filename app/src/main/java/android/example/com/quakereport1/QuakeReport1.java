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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class QuakeReport1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_report1);

        // start the http client thread
        RetrieveDataTask myTask = new RetrieveDataTask();
        myTask.execute();

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


    public class RetrieveDataTask extends AsyncTask<Void, String, JSONArray> {

        /**
         * Tag for the log messages
         */
        public final String LOG_TAG = RetrieveDataTask.class.getSimpleName();

        private String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2017-03-17&endtime=2018-03-17&minmag=4&limit=100";

        // read the responsebody containing the JSON payload
        // java is f* ugly! viva python
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            Log.d(LOG_TAG, "readFromStream about to return: " + output.toString());
            return output.toString();
        }

        // assemble the URL
        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e(LOG_TAG, "Error with creating URL", exception);
                return null;
            }
            Log.d(LOG_TAG, "createUrl about to return: " + url.toString());
            return url;
        }

        // make http request and return the response body as a string
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                Log.e("TAG", "exception in makeHttpRequest " + e.getLocalizedMessage());
                throw e;
                // TODO: Handle the exception
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    // function must handle java.io.IOException here
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private JSONArray extractFeatureFromJson(String earthquakeJSON) {
            Log.d("extractFeatureFromJson", "earthquakeJSON: " + earthquakeJSON);
            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
                JSONArray featureArray = baseJsonResponse.getJSONArray("features");

                Log.d("extractFeatureFromJson", "featureArray.length(): " + featureArray.length());

                // If there are results in the features array
                if (featureArray.length() > 0) {
                    // Extract out the first feature (which is an earthquake)
                    JSONObject firstFeature = featureArray.getJSONObject(0);
                    JSONObject properties = firstFeature.getJSONObject("properties");

                    // Extract out the title, time, and tsunami values
                    String title = properties.getString("title");
                    long time = properties.getLong("time");
                    int tsunamiAlert = properties.getInt("tsunami");

                    Log.d("extractFeatureFromJson", "returning: "
                            + title + " " + time + " " + tsunamiAlert);
                    // Create a new {@link Event} object
                    return featureArray;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
            }
            return null;
        }

        @Override
        protected JSONArray doInBackground(Void... params) {
            Log.d("RetrieveDataTask", "inside doInBackground");

            URL url = createUrl(USGS_REQUEST_URL);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            JSONArray JSONearthquakes = extractFeatureFromJson(jsonResponse);

            Log.d("RetrieveDataTask", "earthquake: " + JSONearthquakes);
            return JSONearthquakes;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);
            Toast.makeText(QuakeReport1.this, "finito http call" + jsonArray, Toast.LENGTH_LONG)
            .show();
        }
    }
}
