package beaconfinder.fun.berger.de.beaconfinder.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.util.BeaconListAdapter;

public class MonitorFragment extends Fragment implements BeaconConsumer, RangeNotifier {

    protected static final String TAG = "MonitoringFrag";

    private static final int PERMISSION_COARSE_LOCATION = 1;

    private ListView listView;

    private TextView editText;

    private BeaconManager beaconManager;

    private HashMap<String, Beacon> beaconHashMap;

    private List<Beacon> unfindBeaconList = new ArrayList();

    private OnFragmentInteractionListener mListener;

    private BeaconListAdapter adapter;

    //Animation
    ImageView startButtonOuterCircle;
    ImageButton stopButton;
    ImageView pulsingRing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bluetooth check
        verifyBluetooth();

        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        //BEACON PARSER
//        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT));
        // Detect the main identifier (UID) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        // Detect the telemetry (TLM) frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        // Detect the URL frame:
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        // Detect Eddystone-EID
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

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

        //Animation
        startButtonOuterCircle = (ImageView) view.findViewById(R.id.scan_circle);

        stopButton = (ImageButton) view.findViewById(R.id.stop_scan_button);

        pulsingRing = (ImageView) view.findViewById(R.id.pulse_ring);
        pulsingRing.setVisibility(View.INVISIBLE);

        startAnimation();

        //UI
        listView = (ListView) view.findViewById(R.id.listview_beacons);

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
        Region region = new Region("all-beacons-region", null, null, null);
        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        beaconManager.addRangeNotifier(this);
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

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {


        unfindBeaconList.clear();
        System.out.println(new Date());
        if (beacons.size() > 0) {
            stopAnimation();
//            //Gelistete Beacon die bei der Suche nicht mehr gefunden wurden icon ausgrauen
//            for (Beacon foundBeacon : beaconHashMap.values())
//                if (!beacons.contains(foundBeacon))
//                    unfindBeaconList.add(foundBeacon);
//
//            //Gefunde Beacon auflisten oder aktualisieren
//            for (Beacon beacon : beacons) {
//                List<Long> l = new ArrayList<>();
//                l.add(new Date().getTime());
//                beacon.setExtraDataFields(l);
//                beaconHashMap.put(beacon.getBluetoothAddress(), beacon);
//            }
            for (Beacon beacon : beacons)
                beaconHashMap.put(beacon.getId1().toHexString(), beacon);

            if (getActivity() == null)
                return;

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Parcelable state = listView.onSaveInstanceState();

//                            BeaconListAdapter adapter = new BeaconListAdapter(listView.getContext(), R.layout.list_item_beacon, new ArrayList(beaconHashMap.values()));
//                            listView.setAdapter(adapter);
                    adapter = new BeaconListAdapter(listView.getContext(), R.layout.list_item_beacon2, new ArrayList(beaconHashMap.values()));
                    adapter.clear();
                    List arrayList = new ArrayList(beaconHashMap.values());
                    //Sort
                    Collections.sort(arrayList, new Comparator<Beacon>() {
                        @Override
                        public int compare(Beacon b1, Beacon b2) {
                            String mac1 = b1.getBluetoothAddress();
                            String mac2 = b2.getBluetoothAddress();

                            return mac1.compareTo(mac2);
                        }
                    });
                    adapter.addAll(arrayList);
                    // Save the ListView state (= includes scroll position) as a Parceble e.g. set new items
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Log.d("############", "Items " + beaconHashMap.get(i));
                        }
                    });

                    // Restore previous state (including selected item index and scroll position)
                    listView.onRestoreInstanceState(state);
                }
            });

        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
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


    private void startAnimation() {
        startButtonOuterCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_in));
//        startButton.setImageResource(R.drawable.ic_circle);
//        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        pulseAnimation();
    }

    private void stopAnimation() {
        if (getActivity() != null)
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    startButtonOuterCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_out));
                    pulsingRing.setVisibility(View.INVISIBLE);
                    pulsingRing.clearAnimation();
                    stopButton.setVisibility(View.INVISIBLE);

                }
            });

    }

    private void pulseAnimation() {
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_pulse));
        pulsingRing.setVisibility(View.VISIBLE);
        pulsingRing.startAnimation(set);
    }

}
