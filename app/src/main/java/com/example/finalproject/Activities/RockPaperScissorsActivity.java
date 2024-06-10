package com.example.finalproject.Activities;

import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.R;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;

public class RockPaperScissorsActivity extends ConnectionsActivity {

    // -------------- Connecting logic --------------
    private String username;
    private String roomCode;
    SwitchCompat advertisingButton, discoveringButton;

    // -------------- Turn based logic --------------
    private static final String CHOICE_1 = "Rock";
    private static final String CHOICE_2 = "Paper";
    private static final String CHOICE_3 = "Scissors";
    private boolean isMyTurn = false;
    private String myChoice;
    private String opponentChoice;
    private TextView tvScoreSelf, tvScoreOpp, tvLog, tvNameSelf, tvNameOpp;
    LinearLayout gameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rock_paper_scissors);

        // -------------- Connecting logic --------------
        username = getApplicationContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_username", "");
        roomCode = getApplicationContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_room", "");

        Utilities utils = new Utilities(getApplicationContext());
        if (!utils.isLocationEnabled())
            Toast.makeText(getApplicationContext(), "Make sure your location is turned on",
                    Toast.LENGTH_LONG).show();

        advertisingButton = findViewById(R.id.switch_advertising);
        advertisingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    startAdvertising();
                else {
                    stopAdvertising();
                    disconnectFromAllEndpoints();
                }
            }
        });

        discoveringButton = findViewById(R.id.switch_discovering);
        discoveringButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    startDiscovering();
                else {
                    stopDiscovering();
                    disconnectFromAllEndpoints();
                }
            }
        });

        // -------------- Turn based logic --------------
        tvScoreSelf = findViewById(R.id.tvScoreSelf);
        tvScoreOpp = findViewById(R.id.tvScoreOpp);
        tvNameSelf = findViewById(R.id.tvNameSelf);
        tvNameSelf.setText(username + ":");
        tvNameOpp = findViewById(R.id.tvNameOpp);
        tvLog = findViewById(R.id.tvLog);
        gameLayout = findViewById(R.id.gameLayout);
        gameLayout.setVisibility(View.GONE);

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

        Button btEndGame = findViewById(R.id.btEndGame);
        btEndGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGame();
            }
        });
    }

    // -------------- Connecting logic --------------

    /**
     * Returns the name of the current user.
     * @return The username of the current user.
     */
    @Override
    protected String getName() {
        return username;
    }

    /**
     * Returns the service ID used for the connection.
     * @return The service ID which is a combination of the room code and "rock_paper_scissors".
     */
    @Override
    protected String getServiceId() {
        return roomCode + ".rock_paper_scissors";
    }

    /**
     * Returns the strategy used for the connection.
     * @return The P2P_STAR strategy for the connection.
     */
    @Override
    protected Strategy getStrategy() {
        return Strategy.P2P_STAR;
    }

    /**
     * Callback for when advertising starts successfully.
     */
    @Override
    protected void onAdvertisingStarted() {
        disconnectFromAllEndpoints();
        Log.d(TAG, "Advertising started successfully.");
    }

    /**
     * Callback for when advertising fails to start.
     */
    @Override
    protected void onAdvertisingFailed() {
        Log.d(TAG, "Advertising failed to start.");
        Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_LONG).show();
    }

    /**
     * Callback for when discovery starts successfully.
     */
    @Override
    protected void onDiscoveryStarted() {
        disconnectFromAllEndpoints();
        Log.d(TAG, "Discovery started successfully.");
    }

    /**
     * Callback for when discovery fails to start.
     */
    @Override
    protected void onDiscoveryFailed() {
        Log.d(TAG, "Discovery failed to start.");
        Toast.makeText(getApplicationContext(), "Something went wrong. Try again", Toast.LENGTH_LONG).show();
    }

    /**
     * Callback for when an endpoint is discovered.
     * @param endpoint The discovered endpoint.
     */
    @Override
    protected void onEndpointDiscovered(Endpoint endpoint) {
        Log.d(TAG, "Discovered endpoint: " + endpoint.getName());
        if (getDiscoveredEndpoints().size() == 1)
            connectToEndpoint(endpoint);
    }

    /**
     * Callback for when a connection is initiated with an endpoint.
     * @param endpoint The endpoint with which the connection is initiated.
     * @param connectionInfo Information about the connection.
     */
    @Override
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        Log.d(TAG, "Connection initiated with endpoint: " + endpoint.getName());
        if (!username.equals(endpoint.getName()) && getConnectedEndpoints().size() < 1)
            acceptConnection(endpoint);
    }

    /**
     * Callback for when an endpoint is connected.
     * @param endpoint The connected endpoint.
     */
    @Override
    protected void onEndpointConnected(Endpoint endpoint) {
        Log.d(TAG, "Connected to endpoint: " + endpoint.getName());
        startGame(endpoint);
    }

    /**
     * Callback for when an endpoint is disconnected.
     * @param endpoint The disconnected endpoint.
     */
    @Override
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Log.d(TAG, "Disconnected from endpoint: " + endpoint.getName());
        logText("Disconnected");
    }

    /**
     * Callback for when a payload is received from an endpoint.
     * @param endpoint The endpoint from which the payload is received.
     * @param payload The received payload.
     */
    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {
        String receivedChoice = new String(payload.asBytes(), StandardCharsets.UTF_8);
        logD("Received payload from endpoint: " + endpoint.getName() + " choice: " + receivedChoice);
        logText(endpoint.getName() + " played a move. ");
        opponentChoice = receivedChoice;
        isMyTurn = true;
        determineWinner();
    }

    /**
     * Callback for when a connection fails with an endpoint.
     * @param endpoint The endpoint with which the connection failed.
     */
    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        Log.d(TAG, "Connection failed with endpoint: " + endpoint.getName());
    }

    // -------------- Turn based logic --------------

    /**
     * Handles making a choice for the current turn.
     * @param choice The chosen move (Rock, Paper, or Scissors).
     */
    private void makeChoice(String choice) {
        if (!isMyTurn) {
            Toast.makeText(this, "Wait for your turn", Toast.LENGTH_SHORT).show();
            return;
        }
        myChoice = choice;
        logText("Choice made: " + choice);
        sendPayloadToOpponent(choice);
        isMyTurn = false;
    }

    /**
     * Sends the chosen move to the opponent.
     * @param choice The chosen move.
     */
    private void sendPayloadToOpponent(String choice) {
        Payload payload = Payload.fromBytes(choice.getBytes(StandardCharsets.UTF_8));
        send(payload);
        isMyTurn = false;
        determineWinner();
    }

    /**
     * Determines the winner of the current round based on the choices made by both players.
     */
    private void determineWinner() {
        if (myChoice != null && opponentChoice != null) {
            String result;
            if (myChoice.equals(opponentChoice)) {
                result = "It's a tie!";
            } else if ((myChoice.equals(CHOICE_1) && opponentChoice.equals(CHOICE_3)) ||
                    (myChoice.equals(CHOICE_2) && opponentChoice.equals(CHOICE_1)) ||
                    (myChoice.equals(CHOICE_3) && opponentChoice.equals(CHOICE_2))) {
                result = "You win!";
                int score = Integer.parseInt(tvScoreSelf.getText().toString());
                score++;
                tvScoreSelf.setText("" + score);
            } else {
                result = "You lose!";
                int score = Integer.parseInt(tvScoreOpp.getText().toString());
                score++;
                tvScoreOpp.setText("" + score);
            }
            logText(result);

            myChoice = null;
            opponentChoice = null;

            if (isMyTurn) {
                Toast.makeText(this, "Your turn!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Opponent's turn!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Logs text to the log TextView.
     * @param text The text to log.
     */
    private void logText(String text) {
        tvLog.setText(tvLog.getText() + text + "\n");
    }

    /**
     * Starts the game by setting up the initial state and hiding unnecessary UI elements.
     * @param opponent The connected opponent endpoint.
     */
    private void startGame(Endpoint opponent) {
        tvNameOpp.setText(":" + opponent.getName());
        isMyTurn = isAdvertising();
        logText("Game started. Opponent: " + opponent.getName());
        discoveringButton.setVisibility(View.GONE);
        advertisingButton.setVisibility(View.GONE);
        gameLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Ends the game by stopping advertising/discovering and disconnecting from all endpoints.
     */
    private void endGame() {
        if (isAdvertising()) stopAdvertising();
        if (isDiscovering()) stopDiscovering();
        disconnectFromAllEndpoints();
        finish();
    }

    /**
     * Ensures all connections are properly closed when the activity is destroyed.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isAdvertising()) stopAdvertising();
        if (isDiscovering()) stopDiscovering();
        disconnectFromAllEndpoints();
    }
}
