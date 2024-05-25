package com.example.finalproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalproject.R;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;

import java.nio.charset.StandardCharsets;

public class ConnectFourActivity extends ConnectionsActivity {

    // -------------- Connecting logic --------------
    private String username;
    private String roomCode;
    SwitchCompat advertisingButton, discoveringButton;

    // -------------- Turn based logic --------------
    private static final int ROWS = 6;
    private static final int COLS = 7;
    private String[][] board = new String[ROWS][COLS];
    private ImageButton[][] buttons = new ImageButton[ROWS][COLS];
    private boolean isXTurn = true;
    private boolean isMyTurn = false;
    private TextView tvScoreBlack,tvScoreWhite, tvLog;
    LinearLayout gameLayout;


    private static final String RESET_KEY_WORD = "reset";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_four);

        // -------------- Connecting logic --------------
        username = getApplicationContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_username", "");
        roomCode = getApplicationContext().getSharedPreferences("shared_pref",
                Context.MODE_PRIVATE).getString("current_room", "");

        advertisingButton = findViewById(R.id.switch_advertising);
        advertisingButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startAdvertising();
            } else {
                stopAdvertising();
                disconnectFromAllEndpoints();
            }
        });

        discoveringButton = findViewById(R.id.switch_discovering);
        discoveringButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startDiscovering();
            } else {
                stopDiscovering();
                disconnectFromAllEndpoints();
            }
        });
        findViewById(R.id.btEndGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endGame();
            }
        });
        findViewById(R.id.btResetGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetGame(true);
            }
        });

        // -------------- Turn based logic --------------
        tvScoreBlack = findViewById(R.id.tvScoreBlack);
        tvScoreWhite = findViewById(R.id.tvScoreWhite);
        tvLog = findViewById(R.id.tvLog);
        gameLayout = findViewById(R.id.gameLayout);
        gameLayout.setVisibility(View.GONE);

        initializeGrid();
    }

    // -------------- Connecting logic --------------
    @Override
    protected String getName() {
        return username;
    }

    @Override
    protected String getServiceId() {
        return roomCode + ".connect_four";
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
        Log.d(TAG, "Endpoint discovered: " + endpoint.getName());
        if(getConnectedEndpoints().size() < 1)
            connectToEndpoint(endpoint);
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
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        if(!username.equals(endpoint.getName()))
            acceptConnection(endpoint);
    }

    @Override
    protected void onReceive(Endpoint endpoint, Payload payload) {
        String move = new String(payload.asBytes(), StandardCharsets.UTF_8);

        if(move.equals(RESET_KEY_WORD)){
            resetGame(false);
            return;
        }

        Toast.makeText(getApplicationContext(),"Your turn..",Toast.LENGTH_SHORT).show();

        String[] parts = move.split(",");
        int row = Integer.parseInt(parts[0]);
        int col = Integer.parseInt(parts[1]);
        String symbol = parts[2];
        isXTurn = !symbol.equals("X");
        board[row][col] = symbol;
        buttons[row][col].setImageResource(isXTurn ? R.drawable.xo_o : R.drawable.xo_x);
        buttons[row][col].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        buttons[row][col].setAdjustViewBounds(true);
        if (checkWin(row, col)) {
            Toast.makeText(this, symbol + " wins!", Toast.LENGTH_SHORT).show();
            logText(symbol + " wins!");
            lockBoard();
            if(symbol.equals("X"))
            {
                int score = Integer.parseInt(tvScoreBlack.getText().toString());
                score++;
                tvScoreBlack.setText("" + score);
            }
            else{
                int score = Integer.parseInt(tvScoreWhite.getText().toString());
                score++;
                tvScoreWhite.setText("" + score);
            }
        } else if (isBoardFull()) {
            Toast.makeText(this, "It's a draw!", Toast.LENGTH_SHORT).show();
            logText("It's a draw!");
        }
        isMyTurn = true;
    }

    // -------------- Turn based logic --------------

    private void initializeGrid() {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int buttonId = getResources().getIdentifier("button" + row + col, "id", getPackageName());
                buttons[row][col] = findViewById(buttonId);
                final int finalRow = row;
                final int finalCol = col;
                buttons[row][col].setOnClickListener(v -> onGridButtonClick(finalRow, finalCol));
                buttons[row][col].setImageResource(R.drawable.xo_blank);
                buttons[row][col].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                buttons[row][col].setAdjustViewBounds(true);
                buttons[row][col].setEnabled(true);
                board[row][col] = null;
            }
        }
    }

    private void onGridButtonClick(int row, int col) {
        if (!isMyTurn) {
            Toast.makeText(this, "Wait for your turn", Toast.LENGTH_SHORT).show();
            return;
        }
        if (board[row][col] != null) {
            Toast.makeText(this, "Column is full", Toast.LENGTH_SHORT).show();
            return;
        }
        int emptyRow = getEmptyRow(col);
        if (emptyRow != -1) {
            String symbol = isXTurn ? "X" : "O";
            board[emptyRow][col] = symbol;
            buttons[emptyRow][col].setImageResource(isXTurn ? R.drawable.xo_x : R.drawable.xo_o);
            buttons[emptyRow][col].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            buttons[emptyRow][col].setAdjustViewBounds(true);
            if (checkWin(emptyRow, col)) {
                Toast.makeText(this, symbol + " wins!", Toast.LENGTH_SHORT).show();
                logText(symbol + " wins!");
                lockBoard();
                if(symbol.equals("X"))
                {
                    int score = Integer.parseInt(tvScoreBlack.getText().toString());
                    score++;
                    tvScoreBlack.setText("" + score);
                }
                else{
                    int score = Integer.parseInt(tvScoreWhite.getText().toString());
                    score++;
                    tvScoreWhite.setText("" + score);
                }
            } else if (isBoardFull()) {
                Toast.makeText(this, "It's a draw!", Toast.LENGTH_SHORT).show();
            }
            isXTurn = !isXTurn;
            isMyTurn = !isMyTurn;
            sendMoveToOpponent(emptyRow, col,symbol);

        }
    }

    private int getEmptyRow(int col) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == null) {
                return row;
            }
        }
        return -1;
    }

    private boolean checkWin(int row, int col) {
        String player = board[row][col];
        // Check horizontal, vertical, and both diagonals
        return checkDirection(row, col, 1, 0, player) || // Horizontal
                checkDirection(row, col, 0, 1, player) || // Vertical
                checkDirection(row, col, 1, 1, player) || // Diagonal \
                checkDirection(row, col, 1, -1, player);  // Diagonal /
    }

    private boolean checkDirection(int row, int col, int dRow, int dCol, String player) {
        int count = 0;
        for (int i = -3; i <= 3; i++) {
            int newRow = row + i * dRow;
            int newCol = col + i * dCol;
            if (newRow >= 0 && newRow < ROWS && newCol >= 0 && newCol < COLS && player.equals(board[newRow][newCol])) {
                count++;
                if (count == 4) {
                    return true;
                }
            } else {
                count = 0;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == null) {
                return false;
            }
        }
        return true;
    }

    private void sendMoveToOpponent(int row, int col, String symbol) {
        String move = row + "," + col + "," + symbol;
        Payload payload = Payload.fromBytes(move.getBytes(StandardCharsets.UTF_8));
        send(payload);
    }

    private void logText(String text) {
        tvLog.setText(tvLog.getText() + text + "\n");
    }

    private void resetGame(boolean isInitiated) {
        initializeGrid();

        if (isInitiated){
            Payload payload = Payload.fromBytes(RESET_KEY_WORD.getBytes(StandardCharsets.UTF_8));
            send(payload);
        }
    }
    private boolean lockBoard()
    {
        for(int i = 0; i < ROWS; i++)
        {
            for(int j = 0; j < COLS; j++)
            {
                buttons[i][j].setEnabled(false);
            }
        }
        return true;
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
