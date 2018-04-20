/**
 * Created by neo on 16/04/2018.
 */

package android.example.com.quakereport1;

import android.content.SharedPreferences;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("THECHALET", "inside SettingsActivity");
        setContentView(R.layout.settings_activity);
    }

    public static class EarthquakePreferenceFragment
            extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Log.d("THECHALET", "inside onCreate inside EarthquakePreferenceFragment");

            Preference minMag = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMag);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

        }

        private void bindPreferenceSummaryToValue(Preference preference) {

            Log.d("THECHALET", "inside bindPreferenceSummaryToValue");

            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            Log.d("THECHALET", "inside onPreferenceChange preference: " + preference);

            String stringValue = value.toString();

            Log.d("THECHALET", "point 9 inside onPreferenceChange2 stringValue: " + stringValue);


            if (preference instanceof ListPreference) {
                Log.d("THECHALET", "inside onPreferenceChange inside if");

                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                Log.d("THECHALET", "point 10 prefIndex : " + prefIndex);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    Log.d("THECHALET", "inside onPreferenceChange inside if about to setSummary");
                    Log.d("THECHALET", "prefIndex: " + prefIndex);
                    Log.d("THECHALET", "labels: " + labels);
                    Log.d("THECHALET", "labels length: " + labels.length);

                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                Log.d("THECHALET", "inside onPreferenceChange inside else");

                preference.setSummary(stringValue);

                Log.d("THECHALET", "inside onPreferenceChange after setSummray");

            }
            return true;
        }
    }
}
