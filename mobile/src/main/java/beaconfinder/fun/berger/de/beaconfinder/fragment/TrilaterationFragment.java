package beaconfinder.fun.berger.de.beaconfinder.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.commons.math3.fitting.leastsquares.LeastSquaresOptimizer;
import org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizer;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.util.LovelyView;
import beaconfinder.fun.berger.de.beaconfinder.util.trilateration.NonLinearLeastSquaresSolver;
import beaconfinder.fun.berger.de.beaconfinder.util.trilateration.Ponto;
import beaconfinder.fun.berger.de.beaconfinder.util.trilateration.TrilaterationFunction;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrilaterationFragment extends Fragment implements BeaconConsumer {

    private static final int PERMISSION_COARSE_LOCATION = 1;

    private LovelyView centroid;
    DecimalFormat f = new DecimalFormat("#0.00");
    private BeaconManager beaconManager;

    public TrilaterationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Bluetooth check
        verifyBluetooth();

        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        //BEACON PARSER
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
//        beaconManager.debug = true;
        if (!Assent.isPermissionGranted(Assent.ACCESS_COARSE_LOCATION)) {
            requestLocationPermission();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_trilateration, container, false);
        centroid = (LovelyView) view.findViewById(R.id.custView4);

//        beaconManager.setAndroidLScanningDisabled(false);

//        beaconManager.setForegroundBetweenScanPeriod(30000L);
        //Start Monitoring and Ranging
        beaconManager.bind(this);

//        getPos();


        // Inflate the layout for this fragment
        return view;
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

    private void getPos(double[] distancesBeacon) {
        double[][] positions = new double[][]{{0.0, 0.0}, {0.0, 360.0}, {360.0, 180.0}};
        double[] distances = new double[]{100.0, 100.0, 100.0};
//        double[] distances = distancesBeacon;
        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());

        System.out.println(distancesBeacon[0]);
        System.out.println(distancesBeacon[1]);
        System.out.println(distancesBeacon[2]);

        LeastSquaresOptimizer.Optimum optimum = solver.solve();

// the answer
        double[] calculatedPosition = optimum.getPoint().toArray();
        centroid.setX(new Double(calculatedPosition[0]).floatValue());
        centroid.setY(new Double(calculatedPosition[1]).floatValue());
        centroid.setLabelText(f.format(calculatedPosition[0]) + "..." + f.format(calculatedPosition[1]));

// error and geometry information
        RealVector standardDeviation = optimum.getSigma(0);
        RealMatrix covarianceMatrix = optimum.getCovariances(0);
    }

    @Override
    public void onBeaconServiceConnect() {

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                final double[] distances = new double[3];
                if (beacons.size() > 0) {

                    //Gefunde Beacon auflisten oder aktualisieren
                    for (Beacon beacon : beacons) {
                        if (beacon.getId2().toString().equals("87") && beacon.getId3().toString().equals("61738"))
                            distances[0] = beacon.getDistance() * 100;
                        else if (beacon.getId2().toString().equals("24") && beacon.getId3().toString().equals("22613"))
                            distances[1] = beacon.getDistance() * 100;
                        else if (beacon.getId2().toString().equals("87") && beacon.getId3().toString().equals("60330"))
                            distances[2] = beacon.getDistance() * 100;
                    }

                    if (getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            getPos(distances);

                            getLocationByTrilateration(new Ponto(0, 360), distances[0], new Ponto(360, 0), distances[1], new Ponto(0, 0), distances[2]);
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

    public Ponto getLocationByTrilateration(Ponto ponto1, double distance1, Ponto ponto2, double distance2, Ponto ponto3, double distance3) {

        //DECLARACAO DE VARIAVEIS
        Ponto retorno = new Ponto();
        double[] P1 = new double[2];
        double[] P2 = new double[2];
        double[] P3 = new double[2];
        double[] ex = new double[2];
        double[] ey = new double[2];
        double[] p3p1 = new double[2];
        double jval = 0;
        double temp = 0;
        double ival = 0;
        double p3p1i = 0;
        double triptx;
        double xval;
        double yval;
        double t1;
        double t2;
        double t3;
        double t;
        double exx;
        double d;
        double eyy;

        //TRANSFORMA OS PONTOS EM VETORES
        //PONTO 1
        P1[0] = ponto1.getX();
        P1[1] = ponto1.getY();
        //PONTO 2
        P2[0] = ponto2.getX();
        P2[1] = ponto2.getY();
        //PONTO 3
        P3[0] = ponto3.getX();
        P3[1] = ponto3.getY();

        //TRANSFORMA O VALOR DE METROS PARA A UNIDADE DO MAPA
        //DISTANCIA ENTRE O PONTO 1 E A MINHA LOCALIZACAO
        distance1 = (distance1 / 100000);
        //DISTANCIA ENTRE O PONTO 2 E A MINHA LOCALIZACAO
        distance2 = (distance2 / 100000);
        //DISTANCIA ENTRE O PONTO 3 E A MINHA LOCALIZACAO
        distance3 = (distance3 / 100000);

        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            t = t1 - t2;
            temp += (t * t);
        }
        d = Math.sqrt(temp);
        for (int i = 0; i < P1.length; i++) {
            t1 = P2[i];
            t2 = P1[i];
            exx = (t1 - t2) / (Math.sqrt(temp));
            ex[i] = exx;
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = t1 - t2;
            p3p1[i] = t3;
        }
        for (int i = 0; i < ex.length; i++) {
            t1 = ex[i];
            t2 = p3p1[i];
            ival += (t1 * t2);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            t = t1 - t2 - t3;
            p3p1i += (t * t);
        }
        for (int i = 0; i < P3.length; i++) {
            t1 = P3[i];
            t2 = P1[i];
            t3 = ex[i] * ival;
            eyy = (t1 - t2 - t3) / Math.sqrt(p3p1i);
            ey[i] = eyy;
        }
        for (int i = 0; i < ey.length; i++) {
            t1 = ey[i];
            t2 = p3p1[i];
            jval += (t1 * t2);
        }
        xval = (Math.pow(distance1, 2) - Math.pow(distance2, 2) + Math.pow(d, 2)) / (2 * d);
        yval = ((Math.pow(distance1, 2) - Math.pow(distance3, 2) + Math.pow(ival, 2) + Math.pow(jval, 2)) / (2 * jval)) - ((ival / jval) * xval);

        t1 = ponto1.getX();
        t2 = ex[0] * xval;
        t3 = ey[0] * yval;
        triptx = t1 + t2 + t3;
        retorno.setX(triptx);
        t1 = ponto1.getY();
        t2 = ex[1] * xval;
        t3 = ey[1] * yval;
        triptx = t1 + t2 + t3;
        retorno.setY(triptx);


        centroid.setX(new Double(retorno.getX()).floatValue());
        centroid.setY(new Double(retorno.getY()).floatValue());


        centroid.setLabelText(new Double(retorno.getX()).floatValue() + " - " + new Double(retorno.getY()).floatValue());

        return retorno;
    }
}
