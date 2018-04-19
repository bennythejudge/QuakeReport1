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
        setContentView(R.layout.settings_activity);
    }



    public static class EarthquakePreferenceFragment
            extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference minMag = findPreference(getString(R.string.settings_min_magnitude_key));
            bindPreferenceSummaryToValue(minMag);

            Preference orderBy = findPreference(getString(R.string.settings_order_by_key));
            bindPreferenceSummaryToValue(orderBy);

        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }




//        private void bindPreferenceSummaryToValue(Preference preference) {
//            preference.setOnPreferenceChangeListener(this);
//            SharedPreferences prefs =
//                    PreferenceManager.
//                            getDefaultSharedPreferences(preference.getContext());
//            String preferenceString = prefs.getString(preference.getKey(), "");
//            onPreferenceChange(preference, preferenceString);
//        }

//        @Override
//        public boolean onPreferenceChange(Preference preference, Object o) {
//            // get the value changed as a string
//            String sValue = o.toString();
//            if (preference instanceof ListPreference) {
//                ListPreference listPref = (ListPreference) preference;
//                int prefIndex = listPref.findIndexOfValue(sValue);
//                Log.d("onPreferenceChange1",
//                        "in 1st if with sValue: " + sValue + " prefIndex: " +
//                                String.valueOf(prefIndex));
//
//                if (prefIndex >= 0) {
//                    CharSequence[] labels = listPref.getEntries();
//                    Log.d("onPreferenceChange3",
//                            "in if with prefIndex: " +
//                                    String.valueOf(prefIndex) +
//                            " and labels: " + labels +
//                    " labels.length: " + labels.length);
//                    if (labels.length < 1) {
//                        Log.d("onPreferenceChange3", "do nothing");
//                    } else {
//                        preference.setSummary(labels[prefIndex]);
//                    }
//                }
//            } else {
//                Log.d("onPreferenceChange2",
//                        "going for else with sValue: " + sValue +
//                                " preference: " + preference);
//                preference.setSummary(sValue);
//            }
//            return true;
//        }
//    }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }
    }
}
