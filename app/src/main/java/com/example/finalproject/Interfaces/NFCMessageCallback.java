package com.example.finalproject.Interfaces;

public interface NFCMessageCallback {
    void onNFCSendSuccess();
    void onNFCReceiveSuccess(String message);
}
