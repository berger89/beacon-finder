package beaconfinder.fun.berger.de.beaconfinder;

import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Berger on 28.04.2016.
 */
public class BeaconListAdapter extends ArrayAdapter<Beacon> {

    private int layoutResource;
    private MonitoringActivity monitoringActivity;

    public BeaconListAdapter(Context context, int layoutResource, List<Beacon> threeStringsList) {
        super(context, layoutResource, threeStringsList);
        this.layoutResource = layoutResource;
        monitoringActivity = (MonitoringActivity) context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;

        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            view = layoutInflater.inflate(layoutResource, null);
            // well set up the ViewHolder

            viewHolder = new ViewHolderItem();

            viewHolder.distanceTV = (TextView) view.findViewById(R.id.distance);
            viewHolder.rssi_dbmTV = (TextView) view.findViewById(R.id.rssi_dbm);
            viewHolder.tx_dbmTV = (TextView) view.findViewById(R.id.tx_dbm);
            viewHolder.uuid_numTV = (TextView) view.findViewById(R.id.uuid_num);
            viewHolder.typeTV = (TextView) view.findViewById(R.id.type);
            viewHolder.distTV = (TextView) view.findViewById(R.id.dist_m);
            viewHolder.maj_numTV = (TextView) view.findViewById(R.id.maj_num);
            viewHolder.min_numTV = (TextView) view.findViewById(R.id.min_num);
            viewHolder.bluetoothIV = (ImageView) view.findViewById(R.id.bluetooth_image);
            // store the holder with the view.

            view.setTag(viewHolder);

        } else {
            // we've just avoided calling findViewById() on resource everytime
            // just use the viewHolder
            viewHolder = (ViewHolderItem) convertView.getTag();
        }


        Beacon beacon = getItem(position);

        if (beacon != null) {


            if (viewHolder.distanceTV != null) {
                if (beacon.getDistance() < 0.51)
                    viewHolder.distanceTV.setText("immediate");
                else if (beacon.getDistance() < 3.01)
                    viewHolder.distanceTV.setText("near");
                else if (beacon.getDistance() > 3.0)
                    viewHolder.distanceTV.setText("far");
                if (monitoringActivity.getUnfindBeaconList().contains(beacon)) {
                    viewHolder.bluetoothIV.setImageResource(R.drawable.wifi_icon_grey);
                    if (beacon.getExtraDataFields() != null && beacon.getExtraDataFields().size() > 0) {
                        Date d1 = new Date();
                        Date d2 = new Date();
                        d2.setTime(beacon.getExtraDataFields().get(0));
                        long seconds = (d1.getTime() - d2.getTime()) / 1000;
                        viewHolder.distanceTV.setText(seconds + "seconds");
                    }
                }
            }


            if (viewHolder.rssi_dbmTV != null) {
                viewHolder.rssi_dbmTV.setText(beacon.getRssi() + " dBm");
            }

            if (viewHolder.tx_dbmTV != null) {
                viewHolder.tx_dbmTV.setText(beacon.getTxPower() + " dBm");
            }
            if (viewHolder.uuid_numTV != null) {
                viewHolder.uuid_numTV.setText(beacon.getId1().toString().split("-")[4] + "");
            }
            if (viewHolder.typeTV != null) {
                viewHolder.typeTV.setText(beacon.getBluetoothName());
            }
            if (viewHolder.distTV != null) {
                viewHolder.distTV.setText(new DecimalFormat("#.###").format(beacon.getDistance()) + "m");
            }
            if (viewHolder.maj_numTV != null) {
                viewHolder.maj_numTV.setText(beacon.getId2() + "");
            }
            if (viewHolder.min_numTV != null) {
                viewHolder.min_numTV.setText(beacon.getId3() + "");
            }

        }
        return view;

    }

    static class ViewHolderItem {
        TextView distanceTV;
        TextView rssi_dbmTV;
        TextView tx_dbmTV;
        TextView uuid_numTV;
        TextView typeTV;
        TextView distTV;
        TextView maj_numTV;
        TextView min_numTV;
        ImageView bluetoothIV;
    }
}

