package com.example.finalproject.Fragments;

import static android.media.MediaExtractor.MetricsConstants.MIME_TYPE;
import static android.nfc.NdefRecord.createMime;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.finalproject.Activities.SignUpActivity;
import com.example.finalproject.Interfaces.NFCMessageCallback;
import com.example.finalproject.R;

import java.nio.charset.Charset;

public class MainNFCFragment extends Fragment {

    boolean sendingNfc = false;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private ImageButton sendButton;
    public MainNFCFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext());
        if (nfcAdapter == null) {
            // NFC is not supported on this device. Handle this case appropriately.

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_nfc, container, false);

       sendButton = rootView.findViewById(R.id.ibtTransfer);
        sendButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        sendNFC();

                        // Change color on press
                        sendButton.setBackgroundResource(R.drawable.background_nfc_pressed);

                        sendingNfc = true;
                        return true;
                    case MotionEvent.ACTION_UP:

                        // Change color back on release
                        //sendButton.setBackgroundResource(R.drawable.background_nfc_idle);


                        return true;
                }
                return false;
            }
        });

        return rootView;
    }

    private void sendNFC()
    {
        //Check if NFC is available on device
        nfcAdapter = NfcAdapter.getDefaultAdapter(requireContext());
        if(nfcAdapter != null) {

        }
        else {
            Toast.makeText(requireContext(), "NFC not available on this device", Toast.LENGTH_SHORT).show();
        }


        //if(sendingNfc) return ; // Use this line to block multiple send actions

        setPendingIntent();

        // Enable foreground dispatch to send NFC message
        nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, null, null);

        // Set the NDEF message callback
        if (nfcAdapter != null) {

        } else {
            Log.e("NFC", "NFC is not supported on this device");
        }
    }


    // NEW CODE - NEED TESTING
    public void handleNfcIntent(Intent intent) {
        Log.d("NFC", "received nfc");
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {

            Log.d("NFC", "intent has right action");


            String senderUsername = intent.getStringExtra("sender");

            Toast.makeText(requireContext(),"msg: " + senderUsername, Toast.LENGTH_LONG).show();
        }
    }

    private void setPendingIntent()
    {
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            flags |= PendingIntent.FLAG_MUTABLE; // or use FLAG_IMMUTABLE if it's appropriate
        }

        Intent intent = new Intent(requireContext(), getActivity().getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                .setAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
                .putExtra("sender", "hello world!");
        pendingIntent = PendingIntent.getActivity(requireContext(), 0,
                intent, flags);
    }

    /*@Override
    public NdefMessage createNdefMessage(NfcEvent event) {
        String text = ("Beam me up, Android!\n\n" +
                "Beam Time: " + System.currentTimeMillis());
        NdefMessage msg = new NdefMessage(
                new NdefRecord[] { createMime(
                        "application/vnd.com.example.android.beam", text.getBytes())
                        *//**
                         * The Android Application Record (AAR) is commented out. When a device
                         * receives a push with an AAR in it, the application specified in the AAR
                         * is guaranteed to run. The AAR overrides the tag dispatch system.
                         * You can add it back in to guarantee that this
                         * activity starts when receiving a beamed message. For now, this code
                         * uses the tag dispatch system.
                        *//*
                        ,NdefRecord.createApplicationRecord("com.example.android.beam")
                });
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Check to see that the Activity started due to an Android Beam
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            processIntent(getIntent());
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    *//**
     * Parses the NDEF Message from the intent and prints to the TextView
     *//*
    void processIntent(Intent intent) {
        textView = (TextView) findViewById(R.id.textView);
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(
                NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        textView.setText(new String(msg.getRecords()[0].getPayload()));
    }*/

}