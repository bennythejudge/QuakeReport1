/**
 * Created by neo on 16/04/2018.
 */

package android.example.com.quakereport1;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }
    public static class EarthquakePreferenceFragment
            extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener
    {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMag = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMag);
        }

        private void bindPreferenceSummaryToValue(Preference minMag) {
            minMag.setOnPreferenceChangeListener(this);
            SharedPreferences prefs =
                    PreferenceManager.
                    getDefaultSharedPreferences(minMag.getContext());
            String preferenceString = prefs.getString(minMag.getKey(), "");
            onPreferenceChange(minMag, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object o) {
            // get the value changed as a string
            String sValue = o.toString();
            preference.setSummary(sValue);
            return true;
        }
    }
}
