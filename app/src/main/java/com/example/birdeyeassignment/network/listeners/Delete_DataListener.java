package com.example.birdeyeassignment.network.listeners;


import com.example.birdeyeassignment.delete_customer.Delete_Customer_Response;

public interface Delete_DataListener {
    void showDeleteResult(int code, Delete_Customer_Response result);
    void showError(Throwable error);
}