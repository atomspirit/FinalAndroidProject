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

import com.example.finalproject.Domains.Utilities;
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

        Utilities utils = new Utilities(getApplicationContext());
        if(!utils.isLocationEnabled())
            Toast.makeText(getApplicationContext(),"Make sure your location is turned on",
                    Toast.LENGTH_LONG).show();

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
     * @return The service ID which is a combination of the room code and "connect_four".
     */
    @Override
    protected String getServiceId() {
        return roomCode + ".connect_four";
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
        Log.d(TAG, "Advertising started successfully.");
    }

    /**
     * Callback for when advertising fails to start.
     */
    @Override
    protected void onAdvertisingFailed() {
        Log.d(TAG, "Advertising failed to start.");
        Toast.makeText(getApplicationContext(),"something went wrong. Try again", Toast.LENGTH_LONG).show();
    }

    /**
     * Callback for when discovery starts successfully.
     */
    @Override
    protected void onDiscoveryStarted() {
        Log.d(TAG, "Discovery started successfully.");
    }

    /**
     * Callback for when discovery fails to start.
     */
    @Override
    protected void onDiscoveryFailed() {
        Log.d(TAG, "Discovery failed to start.");
        Toast.makeText(getApplicationContext(),"something went wrong. Try again", Toast.LENGTH_LONG).show();
    }

    /**
     * Callback for when an endpoint is discovered.
     * @param endpoint The discovered endpoint.
     */
    @Override
    protected void onEndpointDiscovered(Endpoint endpoint) {
        Log.d(TAG, "Endpoint discovered: " + endpoint.getName());
        if(getDiscoveredEndpoints().size() == 1)
            connectToEndpoint(endpoint);
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
     * Callback for when a connection is initiated with an endpoint.
     * @param endpoint The endpoint with which the connection is initiated.
     * @param connectionInfo Information about the connection.
     */
    @Override
    protected void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        if(!username.equals(endpoint.getName()))
            acceptConnection(endpoint);
    }

    /**
     * Callback for when a payload is received from an endpoint.
     * @param endpoint The endpoint from which the payload is received.
     * @param payload The received payload.
     */
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
        buttons[row][col].setImageResource(isXTurn ? R.drawable.white : R.drawable.black);
        buttons[row][col].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        buttons[row][col].setAdjustViewBounds(true);

        winningLogic(row,col,symbol);
        isMyTurn = true;
    }

    // -------------- Turn based logic --------------

    /**
     * Initializes the game grid with buttons and sets up click listeners for each button.
     */
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

    /**
     * Handles button clicks on the game grid.
     * @param row The row of the clicked button.
     * @param col The column of the clicked button.
     */
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
            buttons[emptyRow][col].setImageResource(isXTurn ? R.drawable.black : R.drawable.white);
            buttons[emptyRow][col].setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            buttons[emptyRow][col].setAdjustViewBounds(true);

            winningLogic(emptyRow,col,symbol);

            isXTurn = !isXTurn;
            isMyTurn = !isMyTurn;
            sendMoveToOpponent(emptyRow, col,symbol);
        }
    }

    /**
     * Returns the first empty row in the specified column.
     * @param col The column to check.
     * @return The first empty row, or -1 if the column is full.
     */
    private int getEmptyRow(int col) {
        for (int row = ROWS - 1; row >= 0; row--) {
            if (board[row][col] == null) {
                return row;
            }
        }
        return -1;
    }

    /**
     * Checks the winning conditions and updates the score and logs accordingly.
     * @param emptyRow The row of the last move.
     * @param col The column of the last move.
     * @param symbol The symbol of the last move.
     */
    private void winningLogic(int emptyRow, int col, String symbol)
    {
        if (checkWin(emptyRow, col)) {
            String winner = (symbol.equals("X") ? "Black" : "White");
            Toast.makeText(this, winner + " wins!", Toast.LENGTH_SHORT).show();
            logText(winner + " wins!");
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
    }

    /**
     * Checks if there is a winning sequence starting from the specified cell.
     * @param row The starting row.
     * @param col The starting column.
     * @return True if there is a winning sequence, false otherwise.
     */
    private boolean checkWin(int row, int col) {
        String player = board[row][col];
        // Check horizontal, vertical, and both diagonals
        return checkDirection(row, col, 1, 0, player) || // Horizontal
                checkDirection(row, col, 0, 1, player) || // Vertical
                checkDirection(row, col, 1, 1, player) || // Diagonal \
                checkDirection(row, col, 1, -1, player);  // Diagonal /
    }

    /**
     * Checks if there is a winning sequence in a specific direction.
     * @param row The starting row.
     * @param col The starting column.
     * @param dRow The row direction.
     * @param dCol The column direction.
     * @param player The player symbol to check for.
     * @return True if there is a winning sequence, false otherwise.
     */
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

    /**
     * Checks if the game board is full.
     * @return True if the board is full, false otherwise.
     */
    private boolean isBoardFull() {
        for (int col = 0; col < COLS; col++) {
            if (board[0][col] == null) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sends the move to the opponent.
     * @param row The row of the move.
     * @param col The column of the move.
     * @param symbol The symbol of the move.
     */
    private void sendMoveToOpponent(int row, int col, String symbol) {
        String move = row + "," + col + "," + symbol;
        Payload payload = Payload.fromBytes(move.getBytes(StandardCharsets.UTF_8));
        send(payload);
    }

    /**
     * Logs text to the log TextView.
     * @param text The text to log.
     */
    private void logText(String text) {
        tvLog.setText(tvLog.getText() + text + "\n");
    }

    /**
     * Resets the game board and optionally sends a reset payload to the opponent.
     * @param isInitiated True if the reset is initiated by the current player, false otherwise.
     */
    private void resetGame(boolean isInitiated) {
        initializeGrid();

        if (isInitiated){
            Payload payload = Payload.fromBytes(RESET_KEY_WORD.getBytes(StandardCharsets.UTF_8));
            send(payload);
        }
    }

    /**
     * Locks the game board by disabling all buttons.
     * @return True when the board is successfully locked.
     */
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

    /**
     * Starts the game by setting up the initial state and hiding unnecessary UI elements.
     * @param opponent The connected opponent endpoint.
     */
    private void startGame(Endpoint opponent) {
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
        if(isAdvertising()) stopAdvertising();
        if(isDiscovering()) stopDiscovering();
        disconnectFromAllEndpoints();
    }
}
