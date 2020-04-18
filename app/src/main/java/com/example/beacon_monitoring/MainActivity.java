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

import java.io.PrintWriter;
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
    private int beaconIndexer=0;
    private int beaconIdListSize=4;
    private boolean allBeaconsVisited= false;
    private double passedThreshold = 2.00;// acceptable value based off of measured distance analysis that you have passed the beacon.
    //these values are to prevent causing a reroute based off of  picking up  the expected beacon twice
    private String foundMajorId= null;
    private String foundMinorId= "placeHolder";

    private BeaconManager beaconManager = null;
    private Region anyRegion;
    private Region expectedRegion;
    private ArrayList<String> idListDelimeter;// one imp where pathfinding returns a string with both the Major and Minor Id with a delimeter in the middle
    private ArrayList <String> majorIdList;
    private ArrayList <String> minorIdList;
    private String minorIdArray[]={ "0","1","2","3"};
    private String majorIdArray[]={"0","0","0","0"};
    private String uuIdString = "11111111-1111-1111-1111-111111111111";// a univeral id (Constant)to differentiate differnt proximity networks. picked for simplicity.
    private String majorIdString = minorIdArray[beaconIndexer];
    private String minorIdString = majorIdArray[beaconIndexer];
    private Identifier uuId= Identifier.parse(uuIdString); // regions are defined by Identifer objects
    private Identifier expectedMajorId = Identifier.parse(majorIdString);
    private Identifier expectMinorId = Identifier.parse(minorIdString);




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
            int rangingIterationNumber= 1;

            @Override
            //output to log
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) { // will range every 1001 ms until stop ranging is called

                    Log.d("Ranging", "RangingIteration");//testing
                    for (Beacon oneBeacon : beacons) {// loop through all of the available beacons found by the ranging call
                        if (oneBeacon.getDistance() < passedThreshold && oneBeacon.getId2().toString().equals(expectedMajorId) && oneBeacon.getId3().toString().equals(expectMinorId))// if  we have passed the  the beacon we were expecting
                        {
                            Log.d("Ranging", "Expected beacon found  found Major:\" + expectedMajorId + \" Minor:\" + expectMinorId + \" distance");
                            Toast.makeText(MainActivity.this,
                                    "Expected beacon", Toast.LENGTH_LONG).show();
                            beaconIndexer++;// points to next beacon identifiers
                            foundMajorId= oneBeacon.getId2().toString();
                            foundMinorId= oneBeacon.getId3().toString();

                            if(beaconIndexer > majorIdArray.length)// if gone through all of the beacon
                            {
                                Log.d("Ranging", " Before stopScanning():all beacons have been found");
                                stopScanning();
                                Log.d("Ranging", "all beacons have been found");//testing
                                Toast.makeText(MainActivity.this,
                                        "AllBeaconsFound", Toast.LENGTH_LONG).show();

                                //ToDo Eric call back that everything was successful???
                            }


                        } else if (oneBeacon.getDistance() < passedThreshold && (!oneBeacon.getId3().toString().equals(foundMinorId)) )// we have have passed a  beacon and it is on the same floor.
                        {
                            Log.d("Ranging", " Before stopScanning():all beacons have been found");
                            stopScanning();
                            Log.d("Ranging", "Expected beacon found  found Major:\" + expectedMajorId + \" Minor:\" + expectMinorId + \" distance");
                            Toast.makeText(MainActivity.this,
                                    " Unexpected beacon found Major:", Toast.LENGTH_LONG).show();

                            //ToDo Eric send call back to indicate reroute();
                        }
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

    // Start the RangingScan for raspberry pi/ unlike beaconMonitoring in a previox branch we shall only call this method once
    private void startScanning() {
        Log.d(TAG, "-----------startScanning()----");
        try {
            //range for all beacons within our proximity network
            Region region = new Region("RegionPiDemo",uuId
                    ,null,  null);
            beaconManager.startRangingBeaconsInRegion(region);// starts the BE
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
                    uuId, null, null);
            beaconManager.stopMonitoringBeaconsInRegion(region);
            Toast.makeText(MainActivity.this,
                    "Ranging Beacons Stopped", Toast.LENGTH_LONG).show();

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
