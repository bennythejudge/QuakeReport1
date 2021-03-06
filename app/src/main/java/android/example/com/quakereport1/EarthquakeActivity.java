package android.example.com.quakereport1;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


// loader: the MainActivity must implement the LoaderCallbacks interface
public class EarthquakeActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<EarthQuake>> {

    private static final int EARTHQUAKE_LOADER_ID = 1;
    private EarthQuakeAdapter mAdapter;
    private ListView earthQuakeListView;
    private TextView mEmptyStateTextView;
    private ProgressBar mProgBar;
    private static final String
            USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quake_report1);

        earthQuakeListView = (ListView) findViewById(R.id.list);
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        mProgBar = (ProgressBar) findViewById(R.id.prog_bar);
        earthQuakeListView.setEmptyView(mEmptyStateTextView);
        mAdapter = new EarthQuakeAdapter(this, new ArrayList<EarthQuake>());
        earthQuakeListView.setAdapter(mAdapter);

        earthQuakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // when clicked send create intent to open url
                EarthQuake currentEarthquake = mAdapter.getItem(position);

                String url = currentEarthquake.getUrl();
                Toast.makeText(getApplicationContext(),
                        "intent at " + url,
                        Toast.LENGTH_SHORT).show();
                Intent openUrlIntent = new Intent(Intent.ACTION_VIEW);
                openUrlIntent.setData(Uri.parse(url));
                startActivity(openUrlIntent);
            }
        });

        Context context = this.getApplicationContext();
        // check if there is internet connection
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (isConnected) {
            Log.v("MAIN", "about to get the LoaderManager");
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);
        } else {
            Log.d("onCreate", "No network connectivity");
            mProgBar.setVisibility(View.INVISIBLE);
            mEmptyStateTextView.setText(R.string.no_earthquakes);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // =========================================================================================
    // THIS IS THE NEW WORLD OF LOADERS!!!
    // loader: we need to override the Loader methods
    @Override
    public Loader<List<EarthQuake>> onCreateLoader(int i, Bundle bundle) {

        // retrieve the newly added preference
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default)
        );

        // retrieve the preference for order by
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );


        // now prepare the Url
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        String[] times = getStartEndTime();
        Log.d("onCreateLoader", "today: " + times[1] + " times[0]: " + times[0]);

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "100");
        uriBuilder.appendQueryParameter("starttime", times[0]);
        uriBuilder.appendQueryParameter("endtime", times[1]);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        // call the API
        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<EarthQuake>> loader, List<EarthQuake> earthQuakeList) {
        // hide the progress bar
        mProgBar.setVisibility(View.INVISIBLE);

        // here update the UI this is called when the loader has finished loading
        mAdapter.clear();
        Log.d("onLoadFinished", "now update the UI with: " + earthQuakeList);
        if (earthQuakeList != null && ! earthQuakeList.isEmpty()) {
            mAdapter.addAll(earthQuakeList);
        } else {
            mProgBar.setVisibility(View.INVISIBLE);
            mEmptyStateTextView.setText(R.string.no_earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<EarthQuake>> loader) {
        // this is called when the the mainactivity or the loader is closing and we can clear
        // the existing data to free up memory
        mProgBar.setVisibility(View.INVISIBLE);
        Log.d("onLoaderReset", "Doing a reset <<<<<<<<<<<<<<<<<<<<<<<<========");
        mAdapter.clear();
    }
    // =========================================================================================



    // =========================================================================================
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
    // =========================================================================================
}
