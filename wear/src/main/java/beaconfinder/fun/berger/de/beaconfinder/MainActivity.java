package beaconfinder.fun.berger.de.beaconfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.wearable.view.WatchViewStub;
import android.support.wearable.view.WearableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;

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

public class MainActivity extends Activity implements BeaconConsumer, WearableListView.ClickListener {

    private static final int PERMISSION_COARSE_LOCATION = 1;

    private WearableListView listView;
    private BeaconManager beaconManager;
    private HashMap<String, Beacon> beaconHashMap;
    private List<Beacon> unfindBeaconList = new ArrayList();
    private int positionList;
    private TextView headerListView;
//    private TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (WearableListView) findViewById(R.id.listview_beacons);
                headerListView = (TextView) findViewById(R.id.header);
                listView.setAdapter(new BeaconListAdapter(listView.getContext(), new ArrayList<Beacon>()));
                listView.addOnScrollListener(new WearableListView.OnScrollListener() {
                    @Override
                    public void onScroll(int i) {
//                        toast(i+" onScroll");

                    }

                    @Override
                    public void onAbsoluteScrollChange(int i) {
//                        toast(i+" onAbsoluteScrollChange");
                        if (i > 0) {
                            headerListView.setY(-i);
                        }
                    }

                    @Override
                    public void onScrollStateChanged(int i) {
//                        toast(i + " onScrollStateChanged");


                    }

                    @Override
                    public void onCentralPositionChanged(int i) {
//                        toast(i + " onCentralPositionChanged");
                        positionList = i;

                    }
                });
                listView.setClickListener(MainActivity.this);
//                textView = (TextView) findViewById(R.id.testText);

            }
        });

        Assent.setActivity(this, this);

        verifyBluetooth();
        beaconManager = BeaconManager.getInstanceForApplication(this);
        //BEACON PARSER
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
//        beaconManager.debug = true;
        beaconHashMap = new HashMap<String, Beacon>();
        if (!Assent.isPermissionGranted(Assent.ACCESS_COARSE_LOCATION)) {
            requestLocationPermission();
        }

//        beaconManager.setAndroidLScanningDisabled(false);

        beaconManager.setBackgroundScanPeriod(1500l);
        beaconManager.setBackgroundBetweenScanPeriod(30000l);
        beaconManager.setForegroundScanPeriod(2000l);
        beaconManager.setForegroundBetweenScanPeriod(4000l);
        //Start Monitoring and Ranging
        beaconManager.bind(this);


    }


    private void requestLocationPermission() {
        Assent.requestPermissions(new AssentCallback() {
            @Override
            public void onPermissionResult(PermissionResultSet permissionResultSet) {
                // Intentionally left blank
            }
        }, PERMISSION_COARSE_LOCATION, Assent.ACCESS_COARSE_LOCATION);
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
//                        finish();
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
//                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                unfindBeaconList.clear();
                System.out.println(new Date());
                if (beacons.size() > 0) {

                    //Gelistete Beacon die bei der Suche nicht mehr gefunden wurden icon ausgrauen
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

                    if (this == null)
                        return;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BeaconListAdapter adapter = new BeaconListAdapter(listView.getContext(), new ArrayList(beacons));
                            listView.setAdapter(adapter);
                            if (!(positionList + 1 > beacons.size()))
                                listView.scrollToPosition(positionList);
                            else {
                                listView.scrollToPosition(0);
                                headerListView.setY(-1);
                            }

                        }
                    });

                }
            }

        });

        try {
            Region region = new Region("myRangingUniqueId", null, null, null);
            beaconManager.stopMonitoringBeaconsInRegion(region);
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
        }
    }

    @Override
    public void onClick(WearableListView.ViewHolder viewHolder) {
        switch (viewHolder.getAdapterPosition()) {
            case 0:
                Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(this, "TEST2", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onTopEmptyRegionClick() {
        //
    }

    @Override
    protected void onResume() {
        super.onResume();
        Assent.setActivity(this, this);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) Assent.setActivity(this, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    public void toast(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
    }
}
