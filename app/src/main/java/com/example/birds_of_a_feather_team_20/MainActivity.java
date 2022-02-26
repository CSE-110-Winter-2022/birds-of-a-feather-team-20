package com.example.birds_of_a_feather_team_20;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.birds_of_a_feather_team_20.model.db.Course;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private NearbyManager nearbyManager;

    public NearbyManager getNearbyManager() {
        return nearbyManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Find Friends");


        // Checks bluetooth permissions
        PermissionsManager permManager = new PermissionsManager(this);
        permManager.checkPermission();

        // Checks bluetooth if enabled
        BluetoothManager bt = new BluetoothManager(this);
        bt.initializeBluetooth();

        MyProfile.singleton(getApplicationContext()); // This line is probably unnecessary

        nearbyManager = new NearbyManager(this);
    }

    @Override
    protected void onStart() {
        Log.i("START", "MainActivity.onStart");

        for (Profile profile : DebugActivity.profilesToAdd()) {
            nearbyManager.sendFakeMessage(this, profile);
        }
        DebugActivity.profilesToAdd().clear();

        super.onStart();
        nearbyManager.republish();
    }

    @Override
    protected void onStop() {
        Log.i("STOP", "MainActivity.onStop");
        super.onStop();

    }

    public void onClickStartStop(View view) {
//        Utilities.toast(this,"Start/Stop");
        Button button = (Button)view;
        if (nearbyManager.getIsScanning()) {
            boolean success = nearbyManager.stopScanning();
            if (success)
                button.setText("Start");
        } else {
            boolean success = nearbyManager.startScanning();
            if (success)
                button.setText("Stop");
        }
    }




    public void onLaunchProfileClicked(View view) {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }
    public void onLaunchDebugClicked(View view) {
        Intent intent = new Intent(this, DebugActivity.class);
        startActivity(intent);
    }

    /**
     * Handles result of User input of requested permissions
     * @param requestCode - Code unique to enabling Bluetooth permissions
     * @param permissions - Bluetooth permissions that are being checked
     * @param grantResults - Results from user
     * Cited Work: https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
     * Cited Work: https://developer.android.com/training/permissions/requesting#allow-system-manage-request-code
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        PermissionsManager pm = new PermissionsManager(this);
        pm.onPermissionsResult(grantResults);
    }

}