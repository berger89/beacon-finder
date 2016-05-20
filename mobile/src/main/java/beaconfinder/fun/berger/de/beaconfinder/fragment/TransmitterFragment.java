package beaconfinder.fun.berger.de.beaconfinder.fragment;


import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconTransmitter;

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.util.Transmitter;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransmitterFragment extends Fragment {
    private static final int PERMISSION_COARSE_LOCATION = 1;

    private Transmitter transmitter;
    private BeaconManager beaconManager;
    private boolean isTransmitting=false;

    ImageView startButtonOuterCircle;
    ImageButton startButton;
    ImageButton stopButton;
    ImageView pulsingRing;


    public TransmitterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transmitter, container, false);

        startButtonOuterCircle = (ImageView) view.findViewById(R.id.scan_circle);

        startButton = (ImageButton) view.findViewById(R.id.start_scan_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanButtonClick();
            }
        });
        stopButton = (ImageButton) view.findViewById(R.id.stop_scan_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanButtonClick();
            }
        });

        pulsingRing = (ImageView) view.findViewById(R.id.pulse_ring);

        beaconManager = BeaconManager.getInstanceForApplication(getActivity());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) initBeaconTransmitService();


        return view;
    }


    void onScanButtonClick() {
        if (!Assent.isPermissionGranted(Assent.ACCESS_COARSE_LOCATION)) {
            requestLocationPermission();
        } else {
            startAnimation();
            toggleTransmitting();
        }
    }

    private void toggleTransmitting() {
        if (!isTransmitting) startTransmitting();
        else stopTransmitting();
    }

    private void stopTransmitting() {
        isTransmitting = false;
        transmitter.stopTransmitting();
        stopAnimation();
    }

    private void startTransmitting() {
        if (!beaconManager.checkAvailability()) {
            requestBluetooth();
        } else {
            if (!(BeaconTransmitter.checkTransmissionSupported(getActivity()) == BeaconTransmitter.SUPPORTED)) {
                notifyTransmittingNotSupported();
            } else {
                isTransmitting = true;
                transmitter.startTransmitting();
                startAnimation();
            }
        }
    }


    private void startAnimation() {
        startButtonOuterCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_in));
//        startButton.setImageResource(R.drawable.ic_circle);
        startButton.setVisibility(View.INVISIBLE);
        stopButton.setVisibility(View.VISIBLE);
        pulseAnimation();
    }

    private void stopAnimation() {
        startButtonOuterCircle.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_zoom_out));
        startButton.setImageResource(R.drawable.ic_button_transmit);
        stopButton.setVisibility(View.INVISIBLE);
        pulsingRing.clearAnimation();
    }

    private void pulseAnimation() {
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.anim_pulse));
        pulsingRing.startAnimation(set);
    }


    private void notifyTransmittingNotSupported() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.transmitting_not_supported))
                .setMessage(getString(R.string.transmitting_not_supported_message))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


    private void requestBluetooth() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.bluetooth_not_enabled))
                .setMessage(getString(R.string.please_enable_bluetooth))
                .setPositiveButton(R.string.settings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Initializing intent to go to bluetooth settings.
                        Intent bltSettingsIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                        startActivity(bltSettingsIntent);
                    }
                })
                .show();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initBeaconTransmitService() {
        transmitter = new Transmitter(getActivity());
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
