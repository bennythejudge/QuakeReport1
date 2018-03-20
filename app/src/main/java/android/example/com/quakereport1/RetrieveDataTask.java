package android.example.com.quakereport1;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

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

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by neo on 18/03/2018.
 */

public class RetrieveDataTask extends AsyncTask<Void, String, String> {

    /** Tag for the log messages */
    public static final String LOG_TAG = RetrieveDataTask.class.getSimpleName();

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

    private String extractFeatureFromJson(String earthquakeJSON) {
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
                return new String(title + time + tsunamiAlert);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
        }
        return null;
    }

    @Override
    protected String doInBackground(Void... params) {
        Log.d("RetrieveDataTask", "inside doInBackground");

        URL url = createUrl(USGS_REQUEST_URL);
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            // TODO Handle the IOException
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        String earthquake = extractFeatureFromJson(jsonResponse);

        Log.d("RetrieveDataTask", "earthquake: " + earthquake);
        return earthquake;
    }

    @Override
    protected void onPostExecute(String earthquake) {
        super.onPostExecute(earthquake);

        Log.v("onPostExecute", "I have earthquake: " + earthquake);
    }

    //    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
//    }
}
