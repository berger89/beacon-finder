package beaconfinder.fun.berger.de.beaconfinder.preference;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.afollestad.assent.Assent;
import com.afollestad.assent.AssentCallback;
import com.afollestad.assent.PermissionResultSet;
import com.github.brunodles.simplepreferences.lib.DaoPreferences;

import org.altbeacon.beacon.BeaconManager;

import beaconfinder.fun.berger.de.beaconfinder.R;
import beaconfinder.fun.berger.de.beaconfinder.keyboard.DaoInputObject;
import beaconfinder.fun.berger.de.beaconfinder.keyboard.KeyBoardActivity;
import beaconfinder.fun.berger.de.beaconfinder.service.BeaconBackgroundService;

public class SettingsActivity extends Activity {

    private static final int PERMISSION_COARSE_LOCATION = 1;

    private EditText ip;
    private EditText uuid;
    private DaoPreferences dao = new DaoPreferences(this);
    private DaoInputObject daoInputObject = new DaoInputObject();
    private EditText maj;
    private EditText min;
    private EditText methode;
    private Switch switchBackgroundService;
    private EditText dist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ip = (EditText) findViewById(R.id.ipEditText);
        uuid = (EditText) findViewById(R.id.uuidEditText);
        maj = (EditText) findViewById(R.id.majEditText);
        min = (EditText) findViewById(R.id.minEditText);
        methode = (EditText) findViewById(R.id.methodEditText);
        switchBackgroundService = (Switch) findViewById(R.id.switch_background_service);
        dist = (EditText) findViewById(R.id.distEditText);

        //Permission
        Assent.setActivity(this, this);
        verifyBluetooth();
        if (!Assent.isPermissionGranted(Assent.ACCESS_COARSE_LOCATION)) {
            requestLocationPermission();
        }

        boolean run = false;
        if (dao.load(daoInputObject, "service") != null)
            run = dao.load(daoInputObject, "service").service;
        switchBackgroundService.setChecked(isMyServiceRunning(BeaconBackgroundService.class));

        switchBackgroundService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startService(new Intent(SettingsActivity.this, BeaconBackgroundService.class));
                    DaoInputObject daoInputObject = new DaoInputObject();
                    DaoPreferences dao = new DaoPreferences(SettingsActivity.this);
                    daoInputObject.service = true;
                    dao.commit(daoInputObject, "service");
                } else {
                    stopService(new Intent(SettingsActivity.this, BeaconBackgroundService.class));
                    DaoInputObject daoInputObject = new DaoInputObject();
                    DaoPreferences dao = new DaoPreferences(SettingsActivity.this);
                    daoInputObject.service = false;
                    dao.commit(daoInputObject, "service");
                }
            }
        });

        initEditTextViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (data != null && data.getStringExtra("key") != null) {
            String field = data.getStringExtra("key");
            if (field.equals("ip")) {
                daoInputObject = dao.load(daoInputObject, "ip");
                ip.setText(daoInputObject.name);
            } else if (field.equals("uuid")) {
                daoInputObject = dao.load(daoInputObject, "uuid");
                uuid.setText(daoInputObject.name);
            } else if (field.equals("maj")) {
                daoInputObject = dao.load(daoInputObject, "maj");
                maj.setText(daoInputObject.name);
            } else if (field.equals("min")) {
                daoInputObject = dao.load(daoInputObject, "min");
                min.setText(daoInputObject.name);
            } else if (field.equals("methode")) {
                daoInputObject = dao.load(daoInputObject, "methode");
                methode.setText(daoInputObject.name);
            } else if (field.equals("dist")) {
                daoInputObject = dao.load(daoInputObject, "dist");
                dist.setText(daoInputObject.name + "");
            }
        }

    }

    private void initEditTextViews() {
        ip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, KeyBoardActivity.class);
                myIntent.putExtra("ip", ip.getText().toString()); //Optional parameters
                SettingsActivity.this.startActivityForResult(myIntent, 0);
            }
        });

        uuid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, KeyBoardActivity.class);
                myIntent.putExtra("uuid", uuid.getText().toString()); //Optional parameters
                SettingsActivity.this.startActivityForResult(myIntent, 0);
            }
        });

        maj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, KeyBoardActivity.class);
                myIntent.putExtra("maj", maj.getText().toString()); //Optional parameters
                SettingsActivity.this.startActivityForResult(myIntent, 0);
            }
        });

        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, KeyBoardActivity.class);
                myIntent.putExtra("min", min.getText().toString()); //Optional parameters
                SettingsActivity.this.startActivityForResult(myIntent, 0);
            }
        });
        methode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, KeyBoardActivity.class);
                myIntent.putExtra("methode", methode.getText().toString()); //Optional parameters
                SettingsActivity.this.startActivityForResult(myIntent, 0);
            }
        });
        dist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(SettingsActivity.this, KeyBoardActivity.class);
                myIntent.putExtra("dist", dist.getText().toString()); //Optional parameters
                SettingsActivity.this.startActivityForResult(myIntent, 0);
            }
        });

        daoInputObject = new DaoInputObject();
        if (dao.load(daoInputObject, "ip").name != null)
            ip.setText((dao.load(daoInputObject, "ip")).name);
        else {
            daoInputObject.name = ip.getText().toString();
            dao.commit(daoInputObject, "ip");
        }
        daoInputObject = new DaoInputObject();
        if (dao.load(daoInputObject, "uuid").name != null)
            uuid.setText((dao.load(daoInputObject, "uuid")).name);
        else {
            daoInputObject.name = uuid.getText().toString();
            dao.commit(daoInputObject, "uuid");
        }
        daoInputObject = new DaoInputObject();
        if (dao.load(daoInputObject, "maj").name != null)
            maj.setText((dao.load(daoInputObject, "maj")).name);
        else {
            daoInputObject.name = maj.getText().toString();
            dao.commit(daoInputObject, "maj");
        }
        daoInputObject = new DaoInputObject();
        if (dao.load(daoInputObject, "min").name != null)
            min.setText((dao.load(daoInputObject, "min")).name);
        else {
            daoInputObject.name = min.getText().toString();
            dao.commit(daoInputObject, "min");
        }
        daoInputObject = new DaoInputObject();
        if (dao.load(daoInputObject, "methode").name != null)
            methode.setText((dao.load(daoInputObject, "methode")).name);
        else {
            daoInputObject.name = methode.getText().toString();
            dao.commit(daoInputObject, "methode");
        }
        daoInputObject = new DaoInputObject();
        if (dao.load(daoInputObject, "dist").name != null)
            dist.setText((dao.load(daoInputObject, "dist")).name);
        else {
            daoInputObject.name = dist.getText().toString();
            dao.commit(daoInputObject, "dist");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Assent.setActivity(this, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Assent.handleResult(permissions, grantResults);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void verifyBluetooth() {

        try {
            if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Bluetooth LE not available");
            builder.setMessage("Sorry, this device does not support Bluetooth LE.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                @Override
                public void onDismiss(DialogInterface dialog) {
//                    finish();
                    System.exit(0);
                }

            });
            builder.show();

        }

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
