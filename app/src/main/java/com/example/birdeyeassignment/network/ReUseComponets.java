package com.example.birdeyeassignment.network;

import android.util.Log;


import com.example.birdeyeassignment.checkin.model.Checkin_Request;
import com.example.birdeyeassignment.checkin.model.Checkin_Response;
import com.example.birdeyeassignment.customers.model.All_Customers_Response;
import com.example.birdeyeassignment.network.listeners.Checkin_DataListener;
import com.example.birdeyeassignment.network.listeners.Customers_DataListener;
import com.example.birdeyeassignment.network.listeners.Delete_DataListener;
import com.example.birdeyeassignment.delete_customer.Delete_Customer_Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ReUseComponets {
    private WebAPIInterface webAPIInterface;
    private Customers_DataListener loginDataListener;

    private Checkin_DataListener checkInDataListner;
    private Delete_DataListener deleteDataListner;

    public void intiRetrofitService() {
        webAPIInterface = ServiceFactory.createRetrofitService(WebAPIInterface.class);
    }


    public ReUseComponets(Customers_DataListener userListrners) {
        this.loginDataListener = userListrners;
        intiRetrofitService();
    }

    public ReUseComponets(Checkin_DataListener userListrners) {
        this.checkInDataListner = userListrners;
        intiRetrofitService();
    }

    public ReUseComponets(Delete_DataListener userListrners) {
        this.deleteDataListner = userListrners;
        intiRetrofitService();
    }

    public void asynCallLoginData() {
        Call<List<All_Customers_Response>> call = webAPIInterface.getCustomers();
        call.enqueue(new Callback<List<All_Customers_Response>>() {
            @Override
            public void onResponse(Call<List<All_Customers_Response>> call, Response<List<All_Customers_Response>> response) {
                loginDataListener.showLoginResult(response.code(), response.body());
                Log.d("Sucess", "DataModel");
            }

            @Override
            public void onFailure(Call<List<All_Customers_Response>> call, Throwable t) {
                loginDataListener.showError(t);

                Log.d("Sucess", "Error");
            }
        });
    }

    public void asynCallForCheckIn(Checkin_Request model) {

        Call<Checkin_Response> call = webAPIInterface.checkin_customer(model);
        call.enqueue(new Callback<Checkin_Response>() {
            @Override
            public void onResponse(Call<Checkin_Response> call, Response<Checkin_Response> response) {
                checkInDataListner.showCheckInResult(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<Checkin_Response> call, Throwable t) {
                checkInDataListner.showError(t);
            }
        });

    }

    public void asynCallDeleteCustomer(int id) {
        Call<Delete_Customer_Response> call = webAPIInterface.deleteCustomer(id);
        call.enqueue(new Callback<Delete_Customer_Response>() {
            @Override
            public void onResponse(Call<Delete_Customer_Response> call, Response<Delete_Customer_Response> response) {
                deleteDataListner.showDeleteResult(response.code(), response.body());
            }

            @Override
            public void onFailure(Call<Delete_Customer_Response> call, Throwable t) {
                deleteDataListner.showError(t);

                Log.d("Sucess", "Error");
            }
        });
    }



}