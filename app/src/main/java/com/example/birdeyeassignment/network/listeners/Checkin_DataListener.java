package com.example.birdeyeassignment.network.listeners;


import com.example.birdeyeassignment.checkin.model.Checkin_Response;

public interface Checkin_DataListener {
    void showCheckInResult(int code, Checkin_Response result);
    void showError(Throwable error);
}