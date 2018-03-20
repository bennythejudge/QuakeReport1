package android.example.com.quakereport1;

import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by neo on 18/03/2018.
 */

public class RetrieveDataTask extends AsyncTask<Void, String, InputStream> {
    @Override
    protected InputStream doInBackground(Void... params) {
        Log.d("RetrieveDataTask", "inside RetrieveDataTask");

        HttpsURLConnection myConnection;
        InputStream responseBody = null;

        URL usgsUrl = null;
        try {
            usgsUrl = new URL("https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2018-03-17&endtime=2018-03-17&minmag=4&limit=100");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        String responseString = null;

        // Create connection
        try {
            myConnection =
                    (HttpsURLConnection) usgsUrl.openConnection();
            int response = -1;
            try {
                response = myConnection.getResponseCode();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("RetrieveDataTask", "http response: " + response);
            if (response == HttpURLConnection.HTTP_OK) {
//                // Success
//                // Further processing here
                Log.d("RetrieveDataTask", "got a 200!");
//                Log.d("RetrieveDataTask", "myConnection: " + String.valueOf(myConnection.getContent()));
                responseBody = myConnection.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("RetrieveDataTask", "returning responseBody: ");
        return responseBody;
    }

    @Override
    protected void onPostExecute(InputStream inputStream) {
        super.onPostExecute(inputStream);

        try {
            Log.v("onPostExecute", "I have inputStream: " + inputStream.read());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //    @Override
//    protected void onProgressUpdate(Integer... progress) {
//        setProgressPercent(progress[0]);
//    }
}
