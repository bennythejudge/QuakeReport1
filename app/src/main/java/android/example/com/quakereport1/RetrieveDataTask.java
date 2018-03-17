package android.example.com.quakereport1;

import android.os.AsyncTask;

import java.net.URL;

/**
 * Created by neo on 18/03/2018.
 */

public class RetrieveDataTask extends AsyncTask<URL, Integer, Long> {
    @Override
    protected Long doInBackground(URL... urls) {
        return null;
    }

    @Override
    protected void onPostExecute(Long aLong) {
        super.onPostExecute(aLong);
    }
}
