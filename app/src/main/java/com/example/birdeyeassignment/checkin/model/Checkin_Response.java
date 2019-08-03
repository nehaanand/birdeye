package com.example.birdeyeassignment.checkin.model;


public class Checkin_Response
{

    private String customerId;


    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString()
    {
        return "customerId = "+customerId;
    }
}

