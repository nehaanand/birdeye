package com.example.birdeyeassignment.network.listeners;

import com.example.birdeyeassignment.customers.model.All_Customers_Response;

import java.util.List;


public interface Customers_DataListener {
    void showLoginResult(int code, List<All_Customers_Response> result);
    void showError(Throwable error);
}
