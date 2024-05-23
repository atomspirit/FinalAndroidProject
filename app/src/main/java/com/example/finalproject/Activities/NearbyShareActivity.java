package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Domains.Room;
import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.R;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;

public class NearbyShareActivity extends ConnectionsActivity {

    // -------------- Connecting logic --------------
    private static final String TAG = "NearbyShare";
    private static final String SERVICE_ID = "com.example.finalproject.SERVICE_ID";
    private String username;
    private String roomCode;

    // -------------- Turn based logic --------------
    private static final String CHOICE_1 = "Rock";
    private static final String CHOICE_2 = "Paper";
    private static final String CHOICE_3 = "Scissors";
    private boolean isMyTurn = false;
    private String myChoice;
    private String opponentChoice;
    private TextView tvScore, tvLog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_share);

        // -------------- Connecting logic --------------
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
                disconnectFromAllEndpoints();
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
                disconnectFromAllEndpoints();
            }
        });

        // -------------- Turn based logic --------------
        tvScore = findViewById(R.id.tvScore);
        tvLog = findViewById(R.id.tvLog);

        Button choice1Button = findViewById(R.id.btRock);
        choice1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeChoice(CHOICE_1);
            }
        });

        Button choice2Button = findViewById(R.id.btPaper);
        choice2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeChoice(CHOICE_2);
            }
        });

        Button choice3Button = findViewById(R.id.btScissors);
        choice3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeChoice(CHOICE_3);
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
        /*String message = "greetings, human. im, " + username;
        Payload payload = Payload.fromBytes(message.getBytes(StandardCharsets.UTF_8));
        send(payload);*/
        isMyTurn = isAdvertising();
        Toast.makeText(getApplicationContext(),"Connected to endpoint: " + endpoint.getName()
                ,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Log.d(TAG, "Disconnected from endpoint: " + endpoint.getName());
    }

    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {

        //String receivedMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
        //Log.d(TAG, "Received payload from endpoint: " + endpoint.getName() + " msg: " + receivedMessage);
        //Toast.makeText(getApplicationContext(),"msg: " + receivedMessage,Toast.LENGTH_LONG).show();
        //Room.setCatcher(roomCode,username);

        String receivedChoice = new String(payload.asBytes(), StandardCharsets.UTF_8);
        logD("Received payload from endpoint: " + endpoint.getName() + " choice: " + receivedChoice);
        Toast.makeText(getApplicationContext(), "Opponent's choice: " + receivedChoice, Toast.LENGTH_LONG).show();
        opponentChoice = receivedChoice;
        isMyTurn = true;
        determineWinner();
    }

    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        Log.d(TAG, "Connection failed with endpoint: " + endpoint.getName());
    }

    // Turn based logic ---------------------------------------------------------------------------
    private void makeChoice(String choice) {
        if (!isMyTurn) {
            Toast.makeText(this, "Wait for your turn", Toast.LENGTH_SHORT).show();
            return;
        }
        myChoice = choice;
        sendPayloadToOpponent(choice);
        isMyTurn = false;
        Toast.makeText(this, "Choice made: " + choice, Toast.LENGTH_SHORT).show();
    }

    private void sendPayloadToOpponent(String choice) {
        Payload payload = Payload.fromBytes(choice.getBytes(StandardCharsets.UTF_8));
        send(payload);
        determineWinner();
    }



    private void determineWinner() {
        if (myChoice != null && opponentChoice != null) {
            // Logic to determine the winner based on myChoice and opponentChoice
            String result;
            if (myChoice.equals(opponentChoice)) {
                result = "It's a tie!";
            } else if ((myChoice.equals(CHOICE_1) && opponentChoice.equals(CHOICE_3)) ||
                    (myChoice.equals(CHOICE_2) && opponentChoice.equals(CHOICE_1)) ||
                    (myChoice.equals(CHOICE_3) && opponentChoice.equals(CHOICE_2))) {
                result = "You win!";
                int score = Integer.parseInt(tvScore.getText().toString());
                score++;
                tvScore.setText(score);
            } else {
                result = "You lose!";
            }
            tvLog.setText(result);
            Toast.makeText(this, result, Toast.LENGTH_LONG).show();

            // Reset choices for the next turn
            myChoice = null;
            opponentChoice = null;

            // Determine who goes first in the next turn (toggle isMyTurn)
            isMyTurn = !isMyTurn;

            // Notify the player whose turn it is
            if (isMyTurn) {
                Toast.makeText(this, "Your turn!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Opponent's turn!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}