package com.example.beacon_monitoring;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String TAG = "Main Activity";

    private BeaconManager beaconManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creates Start Scanning and Stop Scanning Buttons in our demo
        Button startScanningButton = (Button) findViewById(R.id.startButton);
        Button stopScanningButton = (Button) findViewById(R.id.stopButton);
        // will call method on button press
        startScanningButton.setOnClickListener((v) -> {
            startScanning();
        });
        stopScanningButton.setOnClickListener((v) -> {
            stopScanning();
        });

        // for android 10 devices one must get permission for coarse location in order to use beacon services
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1234);

        // Monitoring Example altbeacon.github.io sample code
        //Creates BeaconManager object; sets the
        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().
                // holds ibeacon specific data like a manufacturer ID and Beacon IDs
                        setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.bind(this);
    }

    // Monitoring Example altbeacon.github.io sample code
    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    // Monitoring Example altbeacon.github.io sample code
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                displayAlert("didEnterRegion", "EnteringRegion: " + region.getUniqueId() +
                        "  Beacon detected UUID/major/minor: " + region.getId1() + "/ " + region.getId2() + "/ " + region.getId3());
            }

            @Override
            public void didExitRegion(Region region) {
                displayAlert("didExitRegion", "ExitingRegion" + region.getUniqueId() +
                        "Beacon lost UUID/major/minor:" + region.getId1() + "/ " + region.getId2() + "/ " + region.getId3());
            }

            @Override// need to implement because of  Monitor Notifier interface
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });
    }

    // method called when startScanning button pressed; specifies region and calls beaconManger to search for Raspberry Pi
    private void startScanning() {
        Log.d(TAG, "-----------startScanning()----");
        //Define Region to search for; Region is made up of Identifers that correspoind to a specific beacon
        try {
            //null is a wildcard which means search for any beacon
            Region region = new Region("RegionPiDemo",
                    null, null, null);
            beaconManager.startMonitoringBeaconsInRegion(region);
            //RemoteException to catch Errors with the Android services beaconManager is accessing
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    // comments same as startScanning()
    private void stopScanning() {
        Log.d(TAG, "------------stopScanning()---");

        try {
            Region region = new Region("RegionPiDemo",
                    null, null, null);
            beaconManager.stopMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //Sets data and Shows alert on our screen to show the user that the becon is in or out of range
    private void displayAlert(final String title, final String message) {
        //Avoid synconiztion problems with The alert message debugging with runOnUiThread
        runOnUiThread(() -> {
            // create altert dialog and set data to show user
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle(title);
            alertDialog.setMessage(message);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();

        });
    }
}
