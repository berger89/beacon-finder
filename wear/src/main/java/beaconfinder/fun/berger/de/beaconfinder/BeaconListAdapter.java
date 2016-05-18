package beaconfinder.fun.berger.de.beaconfinder;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;

import java.text.DecimalFormat;
import java.util.List;


/**
 * Created by Berger on 28.04.2016.
 */
public class BeaconListAdapter extends WearableListView.Adapter {
    private List<Beacon> mDataset;
    private final Context mContext;
    private final LayoutInflater mInflater;

    // Provide a suitable constructor (depends on the kind of dataset)
    public BeaconListAdapter(Context context, List<Beacon> dataset) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = dataset;
    }

    // Provide a reference to the type of views you're using
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        TextView distanceTV;
        TextView rssi_dbmTV;
        TextView tx_dbmTV;
        TextView uuid_numTV;
        TextView typeTV;
        TextView distTV;
        TextView maj_min;
        TextView min_numTV;
        ImageView bluetoothIV;

        public ItemViewHolder(View itemView) {
            super(itemView);
            // find the text view within the custom item's layout
            distTV = (TextView) itemView.findViewById(R.id.distance);
            typeTV = (TextView) itemView.findViewById(R.id.type);
            maj_min = (TextView) itemView.findViewById(R.id.maj_min);
//            bluetoothIV = (ImageView) itemView.findViewById(R.id.bluetooth_image);
        }
    }

    // Create new views for list items
    // (invoked by the WearableListView's layout manager)
    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                          int viewType) {
        // Inflate our custom layout for list items
        return new ItemViewHolder(mInflater.inflate(R.layout.list_item_beacon, null));
    }

    // Replace the contents of a list item
    // Instead of creating new views, the list tries to recycle existing ones
    // (invoked by the WearableListView's layout manager)
    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder,
                                 int position) {
        // retrieve the text view
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        // replace text contents
//        view.setText(mDataset[position]);
        // replace list item's metadata

        Beacon beacon = mDataset.get(position);

        if (beacon != null) {


            if (itemHolder.distanceTV != null) {
                if (beacon.getDistance() < 0.51)
                    itemHolder.distanceTV.setText("immediate");
                else if (beacon.getDistance() < 3.01)
                    itemHolder.distanceTV.setText("near");
                else if (beacon.getDistance() > 3.0)
                    itemHolder.distanceTV.setText("far");
//                if (monitoringFrag.getUnfindBeaconList().contains(beacon)) {
//                    viewHolder.bluetoothIV.setImageResource(R.drawable.wifi_icon_grey);
//                    if (beacon.getExtraDataFields() != null && beacon.getExtraDataFields().size() > 0) {
//                        Date d1 = new Date();
//                        Date d2 = new Date();
//                        d2.setTime(beacon.getExtraDataFields().get(0));
//                        long seconds = (d1.getTime() - d2.getTime()) / 1000;
//                        viewHolder.distanceTV.setText(seconds + "seconds");
//                    }
//                }
            }


            if (itemHolder.rssi_dbmTV != null) {
                itemHolder.rssi_dbmTV.setText(beacon.getRssi() + " dBm");
            }

            if (itemHolder.tx_dbmTV != null) {
                itemHolder.tx_dbmTV.setText(beacon.getTxPower() + " dBm");
            }
            if (itemHolder.uuid_numTV != null) {
                itemHolder.uuid_numTV.setText(beacon.getId1().toString().split("-")[4] + "");
            }
            if (itemHolder.typeTV != null) {
                itemHolder.typeTV.setText(beacon.getBluetoothName());
            }
            if (itemHolder.distTV != null) {
                itemHolder.distTV.setText("Distance: "+new DecimalFormat("#.###").format(beacon.getDistance()) + "m");
            }
            if (itemHolder.maj_min != null) {
                itemHolder.maj_min.setText("Maj, Min: "+beacon.getId2() + ", "+beacon.getId3());
            }
            if (itemHolder.min_numTV != null) {
                itemHolder.min_numTV.setText(beacon.getId3() + "");
            }

        }

        holder.itemView.setTag(position);
    }

    // Return the size of your dataset
    // (invoked by the WearableListView's layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}

