package beaconfinder.fun.berger.de.beaconfinder.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

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

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.util.BeaconListAdapter;

public class MonitorFragment extends Fragment implements BeaconConsumer {

    protected static final String TAG = "MonitoringFrag";

    private static final int PERMISSION_COARSE_LOCATION = 1;

    private ListView listView;
    private TextView editText;

    private BeaconManager beaconManager;

    HashMap<String, Beacon> beaconHashMap;


    List<Beacon> unfindBeaconList = new ArrayList();


    private OnFragmentInteractionListener mListener;

    private BeaconListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bluetooth check
        verifyBluetooth();

        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        //BEACON PARSER
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
//        beaconManager.debug = true;
        beaconHashMap = new HashMap<String, Beacon>();
        if (!Assent.isPermissionGranted(Assent.ACCESS_COARSE_LOCATION)) {
            requestLocationPermission();
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor, container, false);

        //UI
        listView = (ListView) view.findViewById(R.id.listview_beacons);

        adapter = new BeaconListAdapter(listView.getContext(), R.layout.list_item_beacon, new ArrayList(beaconHashMap.values()));
        listView.setAdapter(adapter);

        //Check for bluetooth and Scan for Beacon
//        verifyBluetooth();
        beaconManager.setAndroidLScanningDisabled(false);
        //Start Monitoring and Ranging
        beaconManager.bind(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
//        beaconManager.bind((MainActivity) getActivity());
    }


    @Override
    public void onResume() {
        super.onResume();
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (beaconManager.isBound(this)) {
            beaconManager.setBackgroundMode(true);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(getActivity()).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
//                    finish();
//                    System.exit(0);
                }

            });
            builder.show();

        }

    }

    public void logToDisplay(final String line) {
        getActivity().runOnUiThread(new Runnable() {
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

                    if (getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            BeaconListAdapter adapter = new BeaconListAdapter(listView.getContext(), R.layout.list_item_beacon, new ArrayList(beaconHashMap.values()));
//                            listView.setAdapter(adapter);
                            adapter.clear();
                            adapter.addAll(new ArrayList(beaconHashMap.values()));
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
    public Context getApplicationContext() {
        return getActivity().getApplicationContext();
    }

    @Override
    public void unbindService(ServiceConnection serviceConnection) {
        getActivity().unbindService(serviceConnection);
    }

    @Override
    public boolean bindService(Intent intent, ServiceConnection serviceConnection, int mode) {
        return getActivity().bindService(intent, serviceConnection, mode);
    }

    public List<Beacon> getUnfindBeaconList() {
        return unfindBeaconList;
    }

    public void setUnfindBeaconList(List<Beacon> unfindBeaconList) {
        this.unfindBeaconList = unfindBeaconList;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void requestLocationPermission() {
        Assent.requestPermissions(new AssentCallback() {
            @Override
            public void onPermissionResult(PermissionResultSet permissionResultSet) {
                // Intentionally left blank
            }
        }, PERMISSION_COARSE_LOCATION, Assent.ACCESS_COARSE_LOCATION);
    }

}
