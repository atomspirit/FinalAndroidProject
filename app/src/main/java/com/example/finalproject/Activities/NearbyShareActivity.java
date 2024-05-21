package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.R;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;

public class NearbyShareActivity extends ConnectionsActivity {

    private static final String TAG = "NearbyShare";
    private static final String SERVICE_ID = "com.example.finalproject.SERVICE_ID";
    private String username;
    private String roomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_share);

        username = getApplicationContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_username", "");
        roomCode = getApplicationContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_room", "");


        Button startAdvertisingButton = findViewById(R.id.btn_start_advertising);
        startAdvertisingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAdvertising();
            }
        });

        Button stopAdvertisingButton = findViewById(R.id.btn_stop_advertising);
        stopAdvertisingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAdvertising();
            }
        });

        Button startDiscoveringButton = findViewById(R.id.btn_start_discovering);
        startDiscoveringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovering();
            }
        });

        Button stopDiscoveringButton = findViewById(R.id.btn_stop_discovering);
        stopDiscoveringButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopDiscovering();
            }
        });
    }

    @Override
    protected String getName() {
        return username;
    }

    @Override
    protected String getServiceId() {
        return roomCode;
    }

    @Override
    protected Strategy getStrategy() {
        return Strategy.P2P_STAR;
    }

    @Override
    protected void onAdvertisingStarted() {
        Log.d(TAG, "Advertising started successfully.");
    }

    @Override
    protected void onAdvertisingFailed() {
        Log.d(TAG, "Advertising failed to start.");
    }

    @Override
    protected void onDiscoveryStarted() {
        Log.d(TAG, "Discovery started successfully.");
    }

    @Override
    protected void onDiscoveryFailed() {
        Log.d(TAG, "Discovery failed to start.");
    }

    @Override
    protected void onEndpointDiscovered(Endpoint endpoint) {
        Log.d(TAG, "Discovered endpoint: " + endpoint.getName());
        connectToEndpoint(endpoint);
    }

    @Override
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        Log.d(TAG, "Connection initiated with endpoint: " + endpoint.getName());
        acceptConnection(endpoint);
    }

    @Override
    protected void onEndpointConnected(Endpoint endpoint) {
        Log.d(TAG, "Connected to endpoint: " + endpoint.getName());
        String message = "greetings, human. im, " + username;
        Payload payload = Payload.fromBytes(message.getBytes(StandardCharsets.UTF_8));
        send(payload);
    }

    @Override
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Log.d(TAG, "Disconnected from endpoint: " + endpoint.getName());
    }

    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {
        if(!isDiscovering())
            return;
        String receivedMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
        Log.d(TAG, "Received payload from endpoint: " + endpoint.getName() + " msg: " + receivedMessage);
        Toast.makeText(getApplicationContext(),"msg: " + receivedMessage,Toast.LENGTH_LONG).show();
        Room.setCatcher(roomCode,username);
    }

    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        Log.d(TAG, "Connection failed with endpoint: " + endpoint.getName());
    }
}