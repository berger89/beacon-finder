package beaconfinder.fun.berger.de.beaconfinder;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

/**
 * Created by Berger on 04.08.2016.
 */
public interface ServiceCallbacks {
    void doSomething(Collection<Beacon> beacons);
}
