package beaconfinder.fun.berger.de.beaconfinder.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.webservice.pojo.Beacon;

/**
 * Created by Berger on 28.04.2016.
 */
public class BeaconsDBListAdapter extends ArrayAdapter<Beacon> {

    private int layoutResource;

    public BeaconsDBListAdapter(Context context, int layoutResource, List<Beacon> threeStringsList) {
        super(context, layoutResource, threeStringsList);
        this.layoutResource = layoutResource;
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

            viewHolder.uuid_numTV = (TextView) view.findViewById(R.id.uuid_num);
            viewHolder.location = (TextView) view.findViewById(R.id.loctv);
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

            if (viewHolder.uuid_numTV != null) {
                viewHolder.uuid_numTV.setText(beacon.getUuid()+"");
            }
            if (viewHolder.location != null) {
                viewHolder.location.setText(beacon.getLocation());
            }
            if (viewHolder.maj_numTV != null) {
                viewHolder.maj_numTV.setText(beacon.getMaj()+"");
            }
            if (viewHolder.min_numTV != null) {
                viewHolder.min_numTV.setText(beacon.getMin()+"");
            }

        }
        return view;

    }

    static class ViewHolderItem {
        TextView uuid_numTV;
        TextView location;
        TextView maj_numTV;
        TextView min_numTV;
        ImageView bluetoothIV;
    }
}

