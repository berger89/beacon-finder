package beaconfinder.fun.berger.de.beaconfinder.util;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.List;

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.fragment.MonitorFragment;

import static beaconfinder.fun.berger.de.beaconfinder.R.id.eddystone_tlm;
import static beaconfinder.fun.berger.de.beaconfinder.R.id.eddystone_uid;
import static beaconfinder.fun.berger.de.beaconfinder.R.id.eddystone_url;
import static beaconfinder.fun.berger.de.beaconfinder.R.id.iBeacon;

/**
 * Created by Berger on 28.04.2016.
 */
public class BeaconListAdapter extends ArrayAdapter<BeaconUtil> {

    private int layoutResource;
    private MonitorFragment monitoringFrag;

    private TextView distanceTV;
    private TextView rssi_dbmTV;
    private TextView tx_dbmTV;
    private TextView distTV;
    private TextView beaconNameTV;
    private TextView uuid_numTV;
    private TextView maj_numTV;
    private TextView min_numTV;
    private TextView instanceTV;
    private TextView namespaceTV;

    private TextView tele_versTV;
    private TextView uptimeTV;
    private TextView batteryTV;
    private TextView tx_countTV;
    private TextView urlTV;

    private ImageView bluetoothIV;
    private TextView iBeaconHeader;
    private TextView eddystoneUidHeader;
    private TextView eddystoneTlmHeader;
    private TextView eddystoneUrlHeader;
    private TextView urlLinkTV;
    private TextView raw_dataTV;

    public BeaconListAdapter(Context context, int layoutResource, List<BeaconUtil> beaconUtilsList) {
        super(context, layoutResource, beaconUtilsList);
        this.layoutResource = layoutResource;
        FragmentManager fm = ((Activity) context).getFragmentManager();
        monitoringFrag = (MonitorFragment) fm.findFragmentByTag("MonitorFragment");
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BeaconUtil beacon = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(layoutResource, null);
            //init Views
            initViews(convertView);
        }
        //set all Views visible
        showViews();


//            distanceTV = (TextView) convertView.findViewById(R.id.distance);
//            tx_dbmTV = (TextView) convertView.findViewById(R.id.tx_dbm);
//            distTV = (TextView) convertView.findViewById(R.id.dist_m);

        bluetoothIV = (ImageView) convertView.findViewById(R.id.bluetooth_image);


        if (beacon != null) {
            //Beacon Name
            beaconNameTV.setText(beacon.getId().split(":")[0]);

            //RAW DATA
            raw_dataTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), beacon.toString(), Toast.LENGTH_SHORT).show();
                }
            });

            //RSSI
            rssi_dbmTV.append(beacon.getLastAddedBeacon().getRssi() + "");
            if (beacon.getLastAddedBeacon().getDistance() < 0.5) {
                // This beacon is immediate (< 0.5 meters)
                bluetoothIV.setImageResource(R.drawable.wifi_icon_immediate);
            } else if (beacon.getLastAddedBeacon().getDistance() < 3.0) {
                // This beacon is near (0.5 to 3 meters)
                bluetoothIV.setImageResource(R.drawable.wifi_icon_near);
            } else {
                // This beacon is far (> 3 meters)
                bluetoothIV.setImageResource(R.drawable.wifi_icon_far);
            }

            //TODO EDDYSTONE
            //Eddystone UID
            if (beacon.getEddystoneUID() != null) {
                namespaceTV.setText("Namespace: " + beacon.getEddystoneUID().getId1());
                instanceTV.setText("Instance: " + beacon.getEddystoneUID().getId2());

                logEddystoneTelemetry(beacon.getEddystoneUID());

            } else {
                eddystoneUidHeader.setVisibility(View.GONE);
                namespaceTV.setVisibility(View.GONE);
                instanceTV.setVisibility(View.GONE);
            }
            //URL
            if (beacon.getEddystoneURL() != null) {
                final String url = UrlBeaconUrlCompressor.uncompress(beacon.getEddystoneURL().getId1().toByteArray());
                urlLinkTV.append(url);
                //TV format
                makeTextViewHyperlink(urlLinkTV);
                if (!url.isEmpty())
                    urlLinkTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            monitoringFrag.startActivity(intent);
                        }
                    });
            } else {
                eddystoneUrlHeader.setVisibility(View.GONE);
                urlTV.setVisibility(View.GONE);
                urlLinkTV.setVisibility(View.GONE);
            }
            //TLM
            if (beacon.getEddystoneTLM() != null) {
                logEddystoneTelemetry(beacon.getEddystoneTLM());
            } else {
                eddystoneTlmHeader.setVisibility(View.GONE);
                tele_versTV.setVisibility(View.GONE);
                uptimeTV.setVisibility(View.GONE);
                batteryTV.setVisibility(View.GONE);
                uptimeTV.setVisibility(View.GONE);
                tx_countTV.setVisibility(View.GONE);
            }
            //iBeacon
            if (beacon.getiBeacon() != null)
                // Just an old fashioned iBeacon or AltBeacon...
                logGenericBeacon(beacon.getiBeacon());
            else {
                iBeaconHeader.setVisibility(View.GONE);
                uuid_numTV.setVisibility(View.GONE);
                maj_numTV.setVisibility(View.GONE);
                min_numTV.setVisibility(View.GONE);
            }
//            }
        }
        return convertView;

    }

    /**
     * log Eddystome TLM data.
     *
     * @param beacon
     */
    private void logEddystoneTelemetry(Beacon beacon) {
        // Do we have telemetry data?
        if (beacon.getExtraDataFields().size() > 0) {
            long telemetryVersion = beacon.getExtraDataFields().get(0);
            long batteryMilliVolts = beacon.getExtraDataFields().get(1);
            long pduCount = beacon.getExtraDataFields().get(3);
            long uptime = beacon.getExtraDataFields().get(4);
            tele_versTV.append("" + telemetryVersion);
            uptimeTV.append("" + uptime);
            batteryTV.append("" + batteryMilliVolts);
            tx_countTV.append("" + pduCount);
        } else {
            eddystoneTlmHeader.setVisibility(View.GONE);
            tele_versTV.setVisibility(View.GONE);
            uptimeTV.setVisibility(View.GONE);
            batteryTV.setVisibility(View.GONE);
            uptimeTV.setVisibility(View.GONE);
            tx_countTV.setVisibility(View.GONE);
        }
    }

    /**
     * log iBeacon and AltBeacon data.
     *
     * @param beacon
     */
    private void logGenericBeacon(Beacon beacon) {
        uuid_numTV.append(beacon.getId1() + "");

        if (beacon.getId2() != null) {
            maj_numTV.append(beacon.getId2() + "");
        }
        if (beacon.getId3() != null) {
            min_numTV.append(beacon.getId3() + "");
        }
    }

//    @Override
//    public void notifyDataSetChanged() {
//        this.setNotifyOnChange(false);
//
//        this.sort(new Comparator<Beacon>() {
//            @Override
//            public int compare(Beacon lhs, Beacon rhs) {
//                return lhs.getBluetoothName().compareTo(rhs.getBluetoothName());
//            }
//        });
//
//        this.setNotifyOnChange(true);
//    }

    /**
     * init Views
     *
     * @param convertView
     */
    private void initViews(View convertView) {
        beaconNameTV = (TextView) convertView.findViewById(R.id.name);
        uuid_numTV = (TextView) convertView.findViewById(R.id.uuid);
        maj_numTV = (TextView) convertView.findViewById(R.id.maj_num);
        min_numTV = (TextView) convertView.findViewById(R.id.min_num);
        rssi_dbmTV = (TextView) convertView.findViewById(R.id.rssi);
        urlTV = (TextView) convertView.findViewById(R.id.url);
        urlLinkTV = (TextView) convertView.findViewById(R.id.url_link);
        tx_countTV = (TextView) convertView.findViewById(R.id.tx_count);
        uptimeTV = (TextView) convertView.findViewById(R.id.uptime);
        batteryTV = (TextView) convertView.findViewById(R.id.battery);
        tele_versTV = (TextView) convertView.findViewById(R.id.tele_vers);
        namespaceTV = (TextView) convertView.findViewById(R.id.namespace);
        instanceTV = (TextView) convertView.findViewById(R.id.instance);
        raw_dataTV = (TextView) convertView.findViewById(R.id.raw_data);

        //Headers
        iBeaconHeader = (TextView) convertView.findViewById(R.id.iBeacon);
        eddystoneUidHeader = (TextView) convertView.findViewById(R.id.eddystone_uid);
        eddystoneTlmHeader = (TextView) convertView.findViewById(R.id.eddystone_tlm);
        eddystoneUrlHeader = (TextView) convertView.findViewById(R.id.eddystone_url);

    }

    /**
     * visible all Views
     */
    private void showViews() {
        beaconNameTV.setVisibility(View.VISIBLE);
        uuid_numTV.setVisibility(View.VISIBLE);
        maj_numTV.setVisibility(View.VISIBLE);
        min_numTV.setVisibility(View.VISIBLE);
        rssi_dbmTV.setVisibility(View.VISIBLE);
        urlTV.setVisibility(View.VISIBLE);
        urlLinkTV.setVisibility(View.VISIBLE);
        tx_countTV.setVisibility(View.VISIBLE);
        uptimeTV.setVisibility(View.VISIBLE);
        batteryTV.setVisibility(View.VISIBLE);
        tele_versTV.setVisibility(View.VISIBLE);
        namespaceTV.setVisibility(View.VISIBLE);
        instanceTV.setVisibility(View.VISIBLE);
        //Headers
        iBeaconHeader.setVisibility(View.VISIBLE);
        eddystoneUidHeader.setVisibility(View.VISIBLE);
        eddystoneTlmHeader.setVisibility(View.VISIBLE);
        eddystoneUrlHeader.setVisibility(View.VISIBLE);
    }

    /**
     * Sets a hyperlink style to the textview.
     */
    public static void makeTextViewHyperlink(TextView tv) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(tv.getText());
        ssb.setSpan(new URLSpan("#"), 0, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(ssb, TextView.BufferType.SPANNABLE);
    }

}

