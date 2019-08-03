package com.example.birdeyeassignment.network;

import com.example.birdeyeassignment.checkin.model.Checkin_Request;
import com.example.birdeyeassignment.checkin.model.Checkin_Response;
import com.example.birdeyeassignment.customers.model.All_Customers_Response;
import com.example.birdeyeassignment.delete_customer.Delete_Customer_Response;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface WebAPIInterface {

    @GET(WebAPIs.GET_ALL_CUSTOMERS)
    @Headers({"Content-Type: application/json", "Accept:application/json"})
    Call<List<All_Customers_Response>> getCustomers();


    @POST(WebAPIs.CHECKIN_CUSTOMER)
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<Checkin_Response> checkin_customer(@Body Checkin_Request model);


    @DELETE(WebAPIs.DELETE_CUSTOMER)
    @Headers({"Content-Type: application/json", "Accept: application/json"})
    Call<Delete_Customer_Response> deleteCustomer(@Path(value = "number", encoded = true) int number);


}
