package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.Domains.Utilities;
import com.example.finalproject.R;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;

public class TicTacToeActivity extends ConnectionsActivity {

    // -------------- Connecting logic --------------
    private String username;
    private String roomCode;
    SwitchCompat advertisingButton, discoveringButton;

    // -------------- Turn based logic --------------
    private boolean isMyTurn = false;
    private String[][] board = new String[3][3];
    private ImageButton[][] buttons = new ImageButton[3][3];
    private TextView tvScoreX,tvScoreO, tvLog;
    LinearLayout gameLayout;
    private boolean isXTurn = true; // Track whose turn it is


    private static final String RESET_KEY_WORD = "reset";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tic_tac_toe);

        // -------------- Connecting logic --------------
        username = getApplicationContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_username", "");
        roomCode = getApplicationContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_room", "");

        Utilities utils = new Utilities(getApplicationContext());
        if(!utils.isLocationEnabled())
        {
            Toast.makeText(getApplicationContext(),"Make sure your location is turned on", Toast.LENGTH_LONG).show();
            stopDiscovering();
        }

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
        tvScoreX = findViewById(R.id.tvScoreX);
        tvScoreO = findViewById(R.id.tvScoreO);
        tvLog = findViewById(R.id.tvLog);
        gameLayout = findViewById(R.id.gameLayout);
        gameLayout.setVisibility(View.GONE);

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                final int finalI = i;
                final int finalJ = j;
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isMyTurn && board[finalI][finalJ] == null) {
                            String symbol = isXTurn ? "X" : "O";
                            board[finalI][finalJ] = symbol;
                            buttons[finalI][finalJ].setImageResource(isXTurn ? R.drawable.xo_x : R.drawable.xo_o);
                            sendPayloadToOpponent(finalI, finalJ, symbol);
                            isMyTurn = false;
                            isXTurn = !isXTurn;
                            checkForWin();
                            return;
                        }
                        if (!isMyTurn)
                        {
                            Toast.makeText(getApplicationContext(), "Wait for your turn", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                buttons[i][j].setImageResource(R.drawable.xo_blank);
            }
        }
        findViewById(R.id.btEndGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGame();
            }
        });
        findViewById(R.id.btResetGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBoard(true);
            }
        });
    }

    @Override
    protected String getName() {
        return username;
    }

    @Override
    protected String getServiceId() {
        return roomCode + ".tic_tac_toe";
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
        Toast.makeText(getApplicationContext(),"something went wrong. Try again", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDiscoveryStarted() {
        Log.d(TAG, "Discovery started successfully.");
    }

    @Override
    protected void onDiscoveryFailed() {
        Log.d(TAG, "Discovery failed to start.");
        Toast.makeText(getApplicationContext(),"something went wrong. Try again", Toast.LENGTH_LONG).show();
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
        if(!username.equals(endpoint.getName()))
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
        String receivedMessage = new String(payload.asBytes(), StandardCharsets.UTF_8);
        Log.d(TAG, "Received payload from endpoint: " + endpoint.getName() + " msg: " + receivedMessage);

        if(receivedMessage.equals(RESET_KEY_WORD))
        {
            resetBoard(false);
            return;
        }

        String[] parts = receivedMessage.split(",");
        int i = Integer.parseInt(parts[0]);
        int j = Integer.parseInt(parts[1]);
        String symbol = parts[2];
        board[i][j] = symbol;
        buttons[i][j].setImageResource(symbol.equals("X") ? R.drawable.xo_x : R.drawable.xo_o);
        isMyTurn = true;
        isXTurn = !symbol.equals("X"); // Switch turn to the other player
        checkForWin();
    }

    @Override
    protected void onConnectionFailed(Endpoint endpoint) {
        Log.d(TAG, "Connection failed with endpoint: " + endpoint.getName());
    }

    private void sendPayloadToOpponent(int i, int j, String symbol) {
        String message = i + "," + j + "," + symbol;
        Payload payload = Payload.fromBytes(message.getBytes(StandardCharsets.UTF_8));
        send(payload);
    }

    private void checkForWin() {
        // Logic to check for win condition
        String winner = getWinner();
        if (winner != null) {
            logText(winner + " wins!");
            Toast.makeText(this, winner + " wins!", Toast.LENGTH_LONG).show();
            if(winner.equals("X"))
            {
                int score = Integer.parseInt(tvScoreX.getText().toString());
                score++;
                tvScoreX.setText("" + score);
            }
            else{
                int score = Integer.parseInt(tvScoreO.getText().toString());
                score++;
                tvScoreO.setText("" + score);
            }
            lockBoard();
        }
    }

    private String getWinner() {

        // Check rows, columns, and diagonals for a win condition
        for (int i = 0; i < 3; i++) {
            if (board[i][0] != null && board[i][0].equals(board[i][1]) && board[i][0].equals(board[i][2])) {
                return board[i][0];
            }
            if (board[0][i] != null && board[0][i].equals(board[1][i]) && board[0][i].equals(board[2][i])) {
                return board[0][i];
            }
        }
        if (board[0][0] != null && board[0][0].equals(board[1][1]) && board[0][0].equals(board[2][2])) {
            return board[0][0];
        }
        if (board[0][2] != null && board[0][2].equals(board[1][1]) && board[0][2].equals(board[2][0])) {
            return board[0][2];
        }

        return null;
    }
    private boolean lockBoard()
    {
        for(int i = 0; i < 3; i++)
        {
            for(int j = 0; j < 3; j++)
            {
                buttons[i][j].setEnabled(false);
            }
        }
        return true;
    }


    private void resetBoard(boolean isInitiated) {
        board = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setImageResource(R.drawable.xo_blank);
                buttons[i][j].setEnabled(true);
            }
        }
        isMyTurn = isAdvertising();
        isXTurn = true; // Reset to X's turn

        if (isInitiated){
            Payload payload = Payload.fromBytes(RESET_KEY_WORD.getBytes(StandardCharsets.UTF_8));
            send(payload);
        }
    }

    private void logText(String text) {
        tvLog.setText(tvLog.getText() + text + "\n");
    }

    private void startGame(Endpoint opponent) {
        isMyTurn = isAdvertising();
        logText("Game started. Opponent: " + opponent.getName());
        discoveringButton.setVisibility(View.GONE);
        advertisingButton.setVisibility(View.GONE);
        gameLayout.setVisibility(View.VISIBLE);
    }

    private void endGame() {
        if (isAdvertising()) stopAdvertising();
        if (isDiscovering()) stopDiscovering();
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
