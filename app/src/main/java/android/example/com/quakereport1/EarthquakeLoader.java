package android.example.com.quakereport1;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Created by neo on 27/03/2018.
 */

public class EarthquakeLoader extends AsyncTaskLoader<List<EarthQuake>> {

    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    private String mUrl;

    public EarthquakeLoader(Context context, String url) {
        super(context);
        Log.d(LOG_TAG, "I am in the constructor with url: " + url);
        mUrl = url;
    }

    @Override
    public List<EarthQuake> loadInBackground() {
        // if the URL is empty, do nothing
        if (this.mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Earthquake> earthquakes = QueryUtils.extractEarthquakes(mUrl);
        return earthquakes;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
