package android.example.com.quakereport1;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.example.com.quakereport1.EarthQuake;
import android.example.com.quakereport1.QueryUtils;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<EarthQuake>> {

    /** Tag for log messages */
    private static final String LOG_TAG = EarthquakeLoader.class.getName();

    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<EarthQuake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        Log.d("loadInBackground", "url: " + mUrl.toString());

        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<EarthQuake> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}