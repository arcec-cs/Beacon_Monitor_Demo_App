package com.example.beacon_monitoring;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {
    private static final String TAG = "Main Activity";
/*
    private static final String TAG = "Main Activity";

    Identifier uUID = Identifier.parse("11111111-1111-1111-1111-111111111111");
    Identifier majorId = Identifier.parse("0");
    Identifier minorId = Identifier.parse("0");

    private BeaconManager beaconManager = null;
    public String test = "test";
    //converts string to a Byte Array
    byte[] testByte = test.getBytes();
    //Identifier UUID = new  Identifier(testByte);
*/

    private BeaconManager beaconManager = null;
    private Region anyRegion;
    private Region expectedRegion;
    private ArrayList<String> idListDelimeter;// one imp where pathfinding returns a string with both the Major and Minor Id with a delimeter in the middle
    private ArrayList <String> majorIdList;
    private ArrayList <String> minorIdList;
    private String minorIdArray[]={ "0","1","2","3"};
    private String majorIdArray[]={"0","0","0","0"};
    private String majorId;
    private String minorId;

    private int beaconIndexer=0;
    private int beaconIdListSize=4;
    private boolean firstScan= true;


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

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                for(Beacon oneBeacon : beacons) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    Log.d("ID:", "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                    displayAlert("didEnterRegion", "EnteringRegion: " + region.getUniqueId() + "Time: "+ formattedDate +
                            "distance: " + oneBeacon.getDistance() + " id:" + oneBeacon.getId1() + "/" + oneBeacon.getId2() + "/" + oneBeacon.getId3());
                }

            }
        });


    }


/*
    // Monitoring Example altbeacon.github.io sample code
    @Override
    public void onBeaconServiceConnect() { //this is the onclick essentially/ the call  back method when an event happens like the action listener
        beaconManager.addMonitorNotifier(new MonitorNotifier() {// MonitorNotifier is an interface and is being used as an anonymus class/ anonymous class' are made dynamically when needed. they make the code consise
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
/*
    // method called when startScanning button pressed; specifies region and calls beaconManger to search for Raspberry Pi
    private void startScanning() {
        Log.d(TAG, "-----------startScanning()----");
        //Define Region to search for; Region is made up of Identifers that correspoind to a specific beacon

        try {
            //null is a wildcard which means search for any beacon
            Region region = new Region("RegionPiDemo",
                    uUID, majorId, minorId);
            beaconManager.startMonitoringBeaconsInRegion(region);
            //RemoteException to catch Errors with the Android services beaconManager is accessing
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
*/

    // method called when startScanning button pressed; specifies region and calls beaconManger to search for Raspberry Pi
    private void startScanning() {
        Log.d(TAG, "-----------startScanning()----");
        //Define Region to search for; Region is made up of Identifers that correspoind to a specific beacon
        Identifier uUID = Identifier.parse("11111111-1111-1111-1111-111111111111");
        try {
            //null is a wildcard which means search for any beacon
            Region region = new Region("RegionPiDemo",uUID
                    , null, null);
            beaconManager.startRangingBeaconsInRegion(region);
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
