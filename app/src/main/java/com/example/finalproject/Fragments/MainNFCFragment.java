package com.example.finalproject.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.finalproject.Activities.ConnectionsActivity;
import com.example.finalproject.Activities.NearbyShareActivity;
import com.example.finalproject.Activities.UserProfileActivity;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.Interfaces.RVInterface;
import com.example.finalproject.R;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.nearby.connection.*;


public class MainNFCFragment extends Fragment implements RVInterface {

    private SwitchCompat sDiscover, sAdvertise;


    private static final String TAG = "NearbyShare";
    private static final String SERVICE_ID = "com.example.finalandroidproject.nearbyshare";
    private ConnectionsClient connectionsClient;
    private Context context;
    private Utilities utilities;
    private List<String> connectedEndpoints = new ArrayList<>();
    private boolean isDiscoveryActive = false, isAdvertiseActive = false;
    private List<String> discoveredEndpoints = new ArrayList<>();
    private RecyclerView recyclerView;
    private Button btShowDevices;


    public MainNFCFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = requireContext();
        utilities = new Utilities(context);

        connectionsClient = Nearby.getConnectionsClient(context);
        connectedEndpoints = new ArrayList<>();

        requestAllPermissions();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_nfc, container, false);

        startActivity(new Intent(getActivity(), NearbyShareActivity.class));

        sDiscover = rootView.findViewById(R.id.sDiscover);
        sAdvertise = rootView.findViewById(R.id.sAdvertise);

        discoveredEndpoints = new ArrayList<>();
        recyclerView = rootView.findViewById(R.id.recyclerView);
        btShowDevices = rootView.findViewById(R.id.btShowDevices);
        btShowDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDiscoveredEndpointsDialog();
            }
        });

        sDiscover.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    subscribe();
                } else {
                    unsubscribe();
                }
            }
        });
        sAdvertise.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    publish();
                } else {
                    unpublish();
                }
            }
        });

        requestAllPermissions();

        return rootView;
    }



    // Nearby Share -------------------------------------------------------------------------------
    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(@NonNull String endPointId, @NonNull Payload payload) {
            String receivedMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
            Log.d(TAG, "Received message: " + receivedMessage);
        }

        @Override
        public void onPayloadTransferUpdate(@NonNull String endPointId, @NonNull PayloadTransferUpdate update) {
            // Update transfer progress here
        }
    };

    public void startAdvertising() {
        AdvertisingOptions advertisingOptions = new AdvertisingOptions.Builder().build();
        connectionsClient.startAdvertising(
                android.os.Build.MODEL, SERVICE_ID, connectionLifecycleCallback, advertisingOptions
        ).addOnSuccessListener(
                aVoid ->{
                    Log.d(TAG, "Advertising started...");
                    isAdvertiseActive = true;
                }
        ).addOnFailureListener(
                e -> Log.e(TAG, "Failed to start advertising: " + e.getMessage())
        );
    }



    public void startDiscovery() {
        DiscoveryOptions discoveryOptions = new DiscoveryOptions.Builder().setStrategy(Strategy.P2P_STAR).build();
        connectionsClient.startDiscovery(
                SERVICE_ID, endpointDiscoveryCallback, discoveryOptions
        ).addOnSuccessListener(
                aVoid ->{
                    Log.d(TAG, "Discovery started...");
                    isDiscoveryActive = true;
                }
        ).addOnFailureListener(
                e -> Log.e(TAG, "Failed to start discovery: " + e.getMessage())
        );
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(@NonNull String endPointId, @NonNull ConnectionInfo info) {

            Log.d(TAG, "connection initiated");
            new AlertDialog.Builder(context)
                    .setTitle("Accept connection to " + info.getEndpointName())
                    .setMessage("Confirm the code matches on both devices: " + info.getAuthenticationDigits())
                    .setPositiveButton(
                            "Accept",
                            (DialogInterface dialog, int which) ->
                                    // The user confirmed, so we can accept the connection.
                                    Nearby.getConnectionsClient(context)
                                            .acceptConnection(endPointId, payloadCallback))
                    .setNegativeButton(
                            android.R.string.cancel,
                            (DialogInterface dialog, int which) ->
                                    // The user canceled, so we should reject the connection.
                                    Nearby.getConnectionsClient(context).rejectConnection(endPointId))
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        @Override
        public void onConnectionResult(@NonNull String endPointId, @NonNull ConnectionResolution result) {
            if (result.getStatus().isSuccess()) {
                Log.d(TAG, "Connected to: " + endPointId);
                // Add the endpoint ID to the list of connected endpoints
                connectedEndpoints.add(endPointId);
                sendData("Hello");
            } else {
                Log.e(TAG, "Failed to connect to: " + endPointId);
            }
        }

        @Override
        public void onDisconnected(@NonNull String endPointId) {
            Log.d(TAG, "Disconnected from: " + endPointId);
            // Remove the endpoint ID from the list of connected endpoints
            connectedEndpoints.remove(endPointId);
        }
    };

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(@NonNull String endPointId, @NonNull DiscoveredEndpointInfo discoveredEndpointInfo) {
            Log.d(TAG,"endpoint found, id: " + endPointId);

            connectionsClient.requestConnection(android.os.Build.MODEL, endPointId, connectionLifecycleCallback)
                    .addOnSuccessListener(
                            (Void unused) -> {
                                // We successfully requested a connection. Now both sides
                                // must accept before the connection is established.
                            })
                    .addOnFailureListener(
                            (Exception e) -> {
                                // Nearby Connections failed to request the connection.
                            });

            // Add the discovered endpoint to the list
            discoveredEndpoints.add(endPointId);

            // Update UI to display the list of discovered endpoints
            // For example, update a RecyclerView or ListView with the discovered endpoints
            updateDiscoveredEndpointsUI();
        }

        @Override
        public void onEndpointLost(@NonNull String endPointId) {
            Log.d(TAG, "Endpoint lost: " + endPointId);

            // Remove the lost endpoint from the list
            discoveredEndpoints.remove(endPointId);

            // Update UI to reflect the removal of the lost endpoint
            updateDiscoveredEndpointsUI();

        }
    };

    public void sendData(String message) {
        Payload payload = Payload.fromBytes(message.getBytes(StandardCharsets.UTF_8));
        connectionsClient.sendPayload(getConnectedEndpoints(), payload);
        Log.d(TAG,"data sent, msg: " + message);
    }

    private List<String> getConnectedEndpoints() {
        return connectedEndpoints;
    }

    private void publish() {
        if (isAdvertiseActive || isDiscoveryActive) {
            sAdvertise.setChecked(false);
            Toast.makeText(context,"You cant advertise and discover at the same time", Toast.LENGTH_LONG).show();
            return;
        }


        Log.d(TAG,"trying to publish");
        if(utilities.gotAllPermissions())
            startAdvertising();
        else
        {
            sAdvertise.setChecked(false);
            requestAllPermissions();
            Toast.makeText(context,"You must enable permissions", Toast.LENGTH_LONG).show();
        }

    }

    private void unpublish() {
        if (!isAdvertiseActive) {
            // Advertise is not active, no need to stop
            return;
        }

        connectionsClient.stopAdvertising();
        Log.d(TAG, "Advertising stopped...");
        isAdvertiseActive = false;
    }

    private void subscribe() {
        if (isDiscoveryActive || isAdvertiseActive) {
            Toast.makeText(context,"You cant advertise and discover at the same time", Toast.LENGTH_LONG).show();
            sDiscover.setChecked(false);
            return;
        }

        if(utilities.gotAllPermissions())
            startDiscovery();
        else
        {
            sDiscover.setChecked(false);
            requestAllPermissions();
            Toast.makeText(context,"You must enable permissions", Toast.LENGTH_LONG).show();
        }
    }

    private void unsubscribe() {

        if (!isDiscoveryActive) {
            // Discovery is not active, no need to stop
            return;
        }

        connectionsClient.stopDiscovery();
        Log.d(TAG, "Discovery stopped...");
        isDiscoveryActive = false;


    }

    // Method to display a dialog with discovered endpoints and allow user to select one
    private void showDiscoveredEndpointsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select an endpoint");
        builder.setItems(discoveredEndpoints.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User selected an endpoint, initiate connection request to the selected endpoint
                String selectedEndpoint = discoveredEndpoints.get(which);
                requestConnection(selectedEndpoint);
            }
        });
        builder.show();
    }

    // Method to initiate connection request to a selected endpoint
    private void requestConnection(String endpointId) {
        connectionsClient.requestConnection(
                android.os.Build.MODEL, endpointId, connectionLifecycleCallback
        );
    }
    // Permissions --------------------------------------------------------------------------------
    private void requestAllPermissions()
    {
        utilities.requestAllPermissionsAvailable(getActivity());
    }

    // UI RecyclerView ----------------------------------------------------------------------------
    private void updateDiscoveredEndpointsUI()
    {
        // TODO:
    }
    private void setupAdapterWithRecyclerView() {
        // Set up the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        //RVUserAdapter adapter = new RVUserAdapter(getContext(), connectedEndpoints, this);
        //recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClicked(int position) {
        Intent intent = new Intent(getContext(), UserProfileActivity.class);
        intent.putExtra("username", connectedEndpoints.get(position));
        startActivity(intent);
    }
}