package android.example.com.quakereport1;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;


/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public class QueryUtils {

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Return a list of {@link EarthQuake} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<EarthQuake> extractEarthquakes(String response) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<EarthQuake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            /*
            Convert SAMPLE_JSON_RESPONSE String into a JSONObject
            Extract “features” JSONArray
            Loop through each feature in the array
            Get earthquake JSONObject at position i
            Get “properties” JSONObject
            Extract “mag” for magnitude
            Extract “place” for location
            Extract “time” for time
            Create Earthquake java object from magnitude, location, and time
            Add earthquake to list of earthquakes
            */
            JSONObject jsonObj = new JSONObject(response);
            JSONArray features = jsonObj.getJSONArray("features");
            for (int i=0; i<features.length(); i++) {
                JSONObject c = features.getJSONObject(i);
                JSONObject p = c.getJSONObject("properties");
                //Log.v("EarthQuake", String.valueOf(p));
                double mag = p.getDouble("mag");
                String location = p.getString("place");
                Long time = p.getLong("time");
//                Log.v("EQ", "mag: " + mag + " place: " + location + " time: " + time);
                // convert time to long and convert then to Date format
                // no longer need to convert as I can extract from json as long
                // Date date = new Date(Long.parseLong(time));
                // extract the URL
                String url = p.getString("url");
//                Log.d("QueryUtils", "url: " + url);
                earthquakes.add(new EarthQuake(mag, location, time, url));
            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
        // Return the list of earthquakes
        return earthquakes;
    }
}

