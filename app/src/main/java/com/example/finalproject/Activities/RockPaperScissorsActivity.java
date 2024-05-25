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


        advertisingButton = findViewById(R.id.switch_advertising);
        advertisingButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
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
                if(isChecked)
                    startDiscovering();
                else{
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

    @Override
    protected String getName() {
        return username;
    }

    @Override
    protected String getServiceId() {
        return roomCode + ".rock_paper_scissors";
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
        if(getDiscoveredEndpoints().size() == 1)
            connectToEndpoint(endpoint);
    }

    @Override
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        Log.d(TAG, "Connection initiated with endpoint: " + endpoint.getName());
        if(!username.equals(endpoint.getName()) && getConnectedEndpoints().size() < 1)
            acceptConnection(endpoint);
    }

    @Override
    protected void onEndpointConnected(Endpoint endpoint) {
        Log.d(TAG, "Connected to endpoint: " + endpoint.getName());
        startGame(endpoint);
    }

    @Override
    protected void onEndpointDisconnected(Endpoint endpoint) {
        Log.d(TAG, "Disconnected from endpoint: " + endpoint.getName());
        logText("Disconnected");
    }

    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {

        //String receivedMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
        //Log.d(TAG, "Received payload from endpoint: " + endpoint.getName() + " msg: " + receivedMessage);
        //Toast.makeText(getApplicationContext(),"msg: " + receivedMessage,Toast.LENGTH_LONG).show();
        //Room.setCatcher(roomCode,username);

        String receivedChoice = new String(payload.asBytes(), StandardCharsets.UTF_8);
        logD("Received payload from endpoint: " + endpoint.getName() + " choice: " + receivedChoice);
        logText(endpoint.getName() + " played a move. ");
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
        logText("Choice made: " + choice);
        sendPayloadToOpponent(choice);
        isMyTurn = false;
    }

    private void sendPayloadToOpponent(String choice) {
        Payload payload = Payload.fromBytes(choice.getBytes(StandardCharsets.UTF_8));
        send(payload);
        isMyTurn = false;
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
            //Toast.makeText(this, result, Toast.LENGTH_LONG).show();

            // Reset choices for the next turn
            myChoice = null;
            opponentChoice = null;

            // Determine who goes first in the next turn (toggle isMyTurn)
            //isMyTurn = !isMyTurn;

            // Notify the player whose turn it is
            if (isMyTurn) {
                Toast.makeText(this, "Your turn!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Opponent's turn!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void logText(String text)
    {
        tvLog.setText(tvLog.getText() + text + "\n");
    }

    private void startGame(Endpoint opponent)
    {
        tvNameOpp.setText(":" + opponent.getName());
        isMyTurn = isAdvertising();
        logText("Game started. Opponent: " + opponent.getName());
        discoveringButton.setVisibility(View.GONE);
        advertisingButton.setVisibility(View.GONE);
        gameLayout.setVisibility(View.VISIBLE);
    }
    private void endGame()
    {
        if(isAdvertising()) stopAdvertising();
        if(isDiscovering()) stopDiscovering();
        disconnectFromAllEndpoints();
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(isAdvertising()) stopAdvertising();
        if(isDiscovering()) stopDiscovering();
        disconnectFromAllEndpoints();
    }
}