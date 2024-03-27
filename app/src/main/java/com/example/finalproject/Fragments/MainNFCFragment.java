package com.example.finalproject.Fragments;

import static android.media.MediaExtractor.MetricsConstants.MIME_TYPE;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.finalproject.R;

import java.nio.charset.Charset;

public class MainNFCFragment extends Fragment {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
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
        pendingIntent = PendingIntent.getActivity(requireContext(), 0,
                new Intent(requireContext(), getActivity().getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main_nfc, container, false);

        Button sendButton = rootView.findViewById(R.id.btTransfer);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an NDEF message with "hello world" payload
                NdefRecord record = NdefRecord.createMime(MIME_TYPE, "hello world".getBytes(Charset.forName("UTF-8")));
                NdefMessage message = new NdefMessage(new NdefRecord[]{record});

                // Enable foreground dispatch to send NFC message
                nfcAdapter.enableForegroundDispatch(getActivity(), pendingIntent, null, null);
                // Send the NDEF message
                if (NfcAdapter.getDefaultAdapter(requireContext()).isNdefPushEnabled()) {
                    NfcAdapter.getDefaultAdapter(requireContext()).setNdefPushMessage(message, getActivity());
                }
            }
        });

        return rootView;
    }


    public void handleNfcIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            // Handle NFC data received here
        }
    }
}