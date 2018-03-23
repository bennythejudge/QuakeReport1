package android.example.com.quakereport1;

import android.content.Context;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class QuakeReport1 extends AppCompatActivity {

    ListView earthquakeListView;
    EarthQuakeAdapter adapter;
    ArrayList<EarthQuake> earthQuakes;
    Context quakeReport1Context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_report1);

        // start the http client thread
        RetrieveDataTask myTask = new RetrieveDataTask();
        myTask.execute();

        Log.v("myOwnWork", "after call to RetrieveDataTask");

        Log.v("MAIN", "QUAKE REPORT1");

    }



    public class RetrieveDataTask extends AsyncTask<Void, String, String> {

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

        private String[] getStartEndTime() {
            String[] ret = new String[2];
            // what day is today?
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String today = df.format(new Date());
            Log.d("getStartEndTime", "Today is: " + today);

            ret[0] = "2018-01-31";
            ret[1] = today;
            return ret;
        }

        @Override
        protected String doInBackground(Void... params) {
            Log.d("RetrieveDataTask", "inside doInBackground");

            String[] times = getStartEndTime();
            Log.d("doInBackground", "today: " + times[1]);
            String sUrl = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=" +
                    times[0] + "&endtime=" + times[1] + "&minmag=4&limit=100";


            URL url = createUrl(sUrl);
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // return the jsonResponse as it is here as a String

            // Extract relevant fields from the JSON response and create an {@link Event} object
            // JSONArray JSONearthquakes = extractFeatureFromJson(jsonResponse);

            Log.d("RetrieveDataTask", "earthquake: " + jsonResponse);

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String jsonResponse) {
            super.onPostExecute(jsonResponse);
            Log.d("onPostExecute", "content: " + jsonResponse);
            Toast.makeText(QuakeReport1.this, "Data Received", Toast.LENGTH_LONG)
            .show();

            // create the list of earthquake
            earthquakeListView = (ListView) findViewById(R.id.list);

            // this is where we used to get the earthquakes from the hardcoded array
            earthQuakes = QueryUtils.extractEarthquakes(jsonResponse);

            Log.v("onPostExecute", String.valueOf(earthQuakes));

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


            adapter =
                    new EarthQuakeAdapter(quakeReport1Context, earthQuakes);

            earthquakeListView.setAdapter(adapter);
        }
    }
}
