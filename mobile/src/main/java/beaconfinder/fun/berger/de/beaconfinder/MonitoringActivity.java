package beaconfinder.fun.berger.de.beaconfinder;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MonitoringActivity extends Activity implements BeaconConsumer {
    protected static final String TAG = "MonitoringActivity";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private ListView listView;
    private TextView editText;

    private BeaconManager beaconManager;

    HashMap<String, Beacon> beaconHashMap;


    List<Beacon> unfindBeaconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitoring);
        editText = (TextView) findViewById(R.id.log_text);
        listView = (ListView) findViewById(R.id.listview_beacons);
        beaconHashMap = new HashMap<String, Beacon>();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().clear();
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setForegroundBetweenScanPeriod(3000l);
        beaconManager.bind(this);

        verifyBluetooth();
        logToDisplay("Application just launched");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    @TargetApi(23)
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                PERMISSION_REQUEST_COARSE_LOCATION);
                    }

                });
                builder.show();
            }
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((BeaconReferenceApplication) this.getApplication()).setMonitoringActivity(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((BeaconReferenceApplication) this.getApplication()).setMonitoringActivity(this);
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Bluetooth not enabled");
                builder.setMessage("Please enable bluetooth in settings and restart this application.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                        System.exit(0);
                    }
                });
                builder.show();
            }
        } catch (RuntimeException e) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    public void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
//                editText.append(line + "\n");
            }
        });
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                int i = 1;
                unfindBeaconList = new ArrayList();
                System.out.println(new Date());
                if (beacons.size() > 0) {
                    //Gelistete Beacon die bei der Suche nichht mehr gefunden wurden icon ausgrauen
                    for (Beacon foundBeacon : beaconHashMap.values())
                        if (!beacons.contains(foundBeacon))
                            unfindBeaconList.add(foundBeacon);

                    //Gefunde Beacon auflisten oder aktualisieren
                    for (Beacon beacon : beacons) {
                        List<Long> l = new ArrayList<>();
                        l.add(new Date().getTime());
                        beacon.setExtraDataFields(l);
                        beaconHashMap.put(beacon.getBluetoothAddress(), beacon);
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BeaconListAdapter adapter = new BeaconListAdapter(listView.getContext(), R.layout.list_item_beacon, new ArrayList(beaconHashMap.values()));
                            listView.setAdapter(adapter);
                        }
                    });

                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    public List<Beacon> getUnfindBeaconList() {
        return unfindBeaconList;
    }

    public void setUnfindBeaconList(List<Beacon> unfindBeaconList) {
        this.unfindBeaconList = unfindBeaconList;
    }

}