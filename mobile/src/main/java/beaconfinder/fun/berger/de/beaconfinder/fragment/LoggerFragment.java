package beaconfinder.fun.berger.de.beaconfinder.fragment;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.util.BeaconsDBListAdapter;
import beaconfinder.fun.berger.de.beaconfinder.webservice.api.IBeaconAPI;
import beaconfinder.fun.berger.de.beaconfinder.webservice.pojo.Beacon;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoggerFragment extends Fragment {

    String url = "http://10.10.16.193:8080";
    private List<Beacon> beacons;
    private ListView beaconsListV;
    private BeaconsDBListAdapter adapter;

    public LoggerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rest, container, false);
        beaconsListV = (ListView) view.findViewById(R.id.listview_beacons_db);

        adapter = new BeaconsDBListAdapter(beaconsListV.getContext(), R.layout.list_item_beacon_db, new ArrayList());
        beaconsListV.setAdapter(adapter);

        getReport();

        return view;
    }


    void getReport() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        IBeaconAPI service = retrofit.create(IBeaconAPI.class);

        Call<List<Beacon>> call = service.getBeacons();

        call.enqueue(new Callback<List<Beacon>>() {


            @Override
            public void onFailure(Call<List<Beacon>> call, Throwable t) {
                t.printStackTrace();
                System.out.print(call);
            }

            @Override
            public void onResponse(Call<List<Beacon>> call, Response<List<Beacon>> response) {
                beacons = new ArrayList<Beacon>();

                try {
                    for (Beacon beacon : response.body()) {
                        beacons.add(beacon);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter.clear();
                adapter.addAll(beacons);

            }
        });
    }

}
