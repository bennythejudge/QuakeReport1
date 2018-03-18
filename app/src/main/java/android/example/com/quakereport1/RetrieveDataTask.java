package android.example.com.quakereport1;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by neo on 18/03/2018.
 */

public class RetrieveDataTask extends AsyncTask<URL, Integer, Long> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Long doInBackground(URL... urls) {
        Log.d("RetrieveDataTask", "insde RetrieveDataTask");

        HttpsURLConnection myConnection;
        URL usgsUrl = null;
        try {
            usgsUrl = new URL("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-03-17&endtime=2018-03-17&minmag=4&limit=100");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Create connection
        try {
            myConnection =
                    (HttpsURLConnection) usgsUrl.openConnection();
            if (myConnection.getResponseCode() == 200) {
                // Success
                // Further processing here
                Log.d("QuakeReport1", "got a 200!");
                Log.d("QuakeReport1", "myConnection: " + String.valueOf(myConnection.getContent()));
                InputStream responseBody = myConnection.getInputStream();
                InputStreamReader responseBodyReader =
                        new InputStreamReader(responseBody, "UTF-8");
                JsonReader jsonReader = new JsonReader(responseBodyReader);
            } else {
                // Error handling code goes here
                Log.d("QuakeReport1", "I did NOT get a 200! " + myConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

//    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
//    }

    protected void onPostExecute(JSONObject result) {
        Log.d("onPostExecute", "JSONObject result: " + result);
    }
}
