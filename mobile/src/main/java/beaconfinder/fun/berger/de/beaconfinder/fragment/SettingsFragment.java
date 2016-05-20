package beaconfinder.fun.berger.de.beaconfinder.fragment;


import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import beaconfinder.fun.berger.de.beaconfinder.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final int MAX_VALUE_ID = 65535;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_scanning);

//        bindPreferenceSummaryToValue(findPreference("key_tracking_age"));
//        bindPreferenceSummaryToValue(findPreference("key_scan_period"));
//        bindPreferenceSummaryToValue(findPreference("key_between_scan_period"));
        bindPreferenceSummaryToValue(findPreference("key_beacon_uuid"));
        bindPreferenceSummaryToValue(findPreference("key_major"));
        bindPreferenceSummaryToValue(findPreference("key_minor"));
        bindPreferenceSummaryToValue(findPreference("key_power"));
        bindPreferenceSummaryToValue(findPreference("key_beacon_advertisement"));
//        bindPreferenceSummaryToValue(findPreference("key_logging"));
    }

    private Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(index >= 0 ? listPreference.getEntries()[index] : null);
            } else if (preference instanceof CheckBoxPreference) {
                // Intentionally left blank.
            } else if (!(Integer.parseInt(value.toString()) < MAX_VALUE_ID)){
                Toast.makeText(getActivity(), "Please enter a value between 0 - " + MAX_VALUE_ID, Toast.LENGTH_LONG).show();
                return false;
            } else preference.setSummary(stringValue);
            return true;
        }
    };

    private void bindPreferenceSummaryToValue(Preference preference) {

        if (!(preference instanceof CheckBoxPreference)) {
            // Set the listener to watch for value changes.
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }
    }
}
