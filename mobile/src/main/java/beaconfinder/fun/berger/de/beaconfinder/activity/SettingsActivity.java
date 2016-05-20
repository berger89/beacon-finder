package beaconfinder.fun.berger.de.beaconfinder.activity;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.fragment.SettingsFragment;


/**
 * Beacon Scanner, file created on 07/03/16.
 *
 * @author Boyd Hogerheijde
 * @author Mitchell de Vries
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().add(android.R.id.content, new SettingsFragment()).commit();
        setTitle(R.string.settings);
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
}
