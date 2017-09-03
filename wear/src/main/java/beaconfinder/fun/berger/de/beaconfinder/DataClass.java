package beaconfinder.fun.berger.de.beaconfinder;

import android.app.Activity;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

public class DataClass {
    public interface IDateCallback {
        void call(final Collection<Beacon> beacons);
    }

    private IDateCallback callerActivity;

    public DataClass(Activity activity) {
        callerActivity = (IDateCallback) activity;
    }


    public void show(final Collection<Beacon> beacons) {
        callerActivity.call(beacons);

    }
}