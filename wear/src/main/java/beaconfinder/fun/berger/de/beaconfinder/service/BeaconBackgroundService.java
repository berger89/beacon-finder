package beaconfinder.fun.berger.de.beaconfinder.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.github.brunodles.simplepreferences.lib.DaoPreferences;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import beaconfinder.fun.berger.de.beaconfinder.MainActivity;
import beaconfinder.fun.berger.de.beaconfinder.keyboard.DaoInputObject;
import beaconfinder.fun.berger.de.beaconfinder.rest.BeaconService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Berger on 01.08.2016.
 */
public class BeaconBackgroundService extends Service implements BeaconConsumer {

    public Context context = this;
    private BeaconManager beaconManager;
    private DaoPreferences dao = new DaoPreferences(this);
    private DaoInputObject daoInputObject = new DaoInputObject();
    private String uuid;
    private String maj;
    private String min;
    private String methode;
    private Handler handler;
    private Runnable runnable;
    private double dist;


    @Override
    public void onCreate() {
        Toast.makeText(context, "Service created!", Toast.LENGTH_LONG).show();

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.setBackgroundScanPeriod(1500l);
        beaconManager.setBackgroundBetweenScanPeriod(30000l);
        beaconManager.setForegroundScanPeriod(2000l);
        beaconManager.setForegroundBetweenScanPeriod(4000l);
        //Start Monitoring and Ranging
        beaconManager.bind(this);

//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//                Toast.makeText(context, "Service is still running", Toast.LENGTH_LONG).show();
//                handler.postDelayed(runnable, 5000);
//            }
//        };
//
//        handler.postDelayed(runnable, 5000);
    }


    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                boolean run = false;
                if (dao.load(daoInputObject, "service") != null)
                    run = dao.load(daoInputObject, "service").service;
                if (run)
                    if (beacons.size() > 0) {

                        //Gefunde Beacon auflisten oder aktualisieren
                        for (Beacon beacon : beacons) {
                            List<Long> l = new ArrayList<>();
                            l.add(new Date().getTime());
                            beacon.setExtraDataFields(l);

                            if (dao.load(daoInputObject, "uuid") != null)
                                uuid = dao.load(daoInputObject, "uuid").name;
                            if (dao.load(daoInputObject, "maj") != null)
                                maj = dao.load(daoInputObject, "maj").name;
                            if (dao.load(daoInputObject, "min") != null)
                                min = dao.load(daoInputObject, "min").name;
                            if (dao.load(daoInputObject, "methode") != null)
                                methode = dao.load(daoInputObject, "methode").name;
                            if (dao.load(daoInputObject, "dist") != null)
                                dist = Double.parseDouble(dao.load(daoInputObject, "dist").name);

                            if (beacon.getDistance() <= dist && beacon.getId1().toUuid().toString().contains(uuid) && beacon.getId2().toString().equals(maj) && beacon.getId3().toString().equals(min)) {
                                String ip = "";
                                if (dao.load(daoInputObject, "ip") != null)
                                    ip = dao.load(daoInputObject, "ip").name;
                                BeaconService beaconService = new Retrofit.Builder()
                                        .baseUrl(ip)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build().create(BeaconService.class);
                                Call<Boolean> call = beaconService.getBeaconLocation(methode);
                                call.enqueue(new Callback<Boolean>() {
                                    @Override
                                    public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                        Toast.makeText(BeaconBackgroundService.this, "Request response: " + response.body(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onFailure(Call<Boolean> call, Throwable t) {
                                        Toast.makeText(BeaconBackgroundService.this, "Failure!", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }

                        if (this == null)
                            return;

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
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        /* IF YOU WANT THIS SERVICE KILLED WITH THE APP THEN UNCOMMENT THE FOLLOWING LINE */
        Toast.makeText(this, "Service stopped", Toast.LENGTH_LONG).show();
        beaconManager.unbind(this);
    }
}

