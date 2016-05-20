package beaconfinder.fun.berger.de.beaconfinder.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;

/**
 * Beacon Transmitter
 *
 * @author Berger
 */
public class Transmitter {

    private Context context;
    private SharedPreferences preferences;
    private BeaconTransmitter beaconTransmitter;

    public Transmitter(Context context) {
        this.context = context;
        preferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        setUpTransmitter();
        setAdvertisingMode();
    }

    public void startTransmitting() {
        beaconTransmitter.startAdvertising();
    }

    public void stopTransmitting() {
        beaconTransmitter.stopAdvertising();
    }

    private void setUpTransmitter() {
        BeaconParser beaconParser = new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT);
        beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        beaconTransmitter.setBeacon(createBeacon());
    }

    private Beacon createBeacon() {
        return new Beacon.Builder()
                    .setId1(preferences.getString("key_beacon_uuid", "0"))
                    .setId2(preferences.getString("key_major", "0"))
                    .setId3(preferences.getString("key_minor", "0"))
                    .setManufacturer(0x0118)
                    .setTxPower(Integer.parseInt(preferences.getString("key_power", "-59")))
                    .setDataFields(Arrays.asList(new Long[]{0l}))
                    .build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setAdvertisingMode() {
        beaconTransmitter.setAdvertiseMode(Integer.parseInt(preferences.getString("key_beacon_advertisement", "10")));
    }

}
