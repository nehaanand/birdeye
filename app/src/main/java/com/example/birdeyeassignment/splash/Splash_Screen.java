package com.example.birdeyeassignment.splash;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import com.example.birdeyeassignment.R;
import com.example.birdeyeassignment.checkin.view.CheckIn_Customer;
import com.example.birdeyeassignment.customers.model.All_Customers_Response;
import com.example.birdeyeassignment.customers.view.All_Customers;
import com.example.birdeyeassignment.network.ReUseComponets;
import com.example.birdeyeassignment.network.listeners.Customers_DataListener;
import com.example.birdeyeassignment.utils.Sqlite_Database;

import java.util.List;

public class Splash_Screen extends AppCompatActivity {
    Handler handler;
    Sqlite_Database db;
    ProgressDialog pdilog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        handler = new Handler();
        db = new Sqlite_Database(Splash_Screen.this);
        pdilog = new ProgressDialog(Splash_Screen.this, R.style.CustomProgress);
        pdilog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pdilog.getWindow().setGravity(Gravity.BOTTOM);
        pdilog.show();
        if (db.get_Customers_Count() == 0) {
            get_Customers();
        } else {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pdilog.dismiss();
                    Intent intent = new Intent(Splash_Screen.this, All_Customers.class);
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }

    }

    public void get_Customers() {
        ReUseComponets servercall = new ReUseComponets(new Customers_DataListener() {
            @Override
            public void showLoginResult(int code, List<All_Customers_Response> result) {
                pdilog.dismiss();
                db.insert_Customers(result);
                Intent intent = new Intent(Splash_Screen.this, All_Customers.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void showError(Throwable error) {
                Log.d("Error", "" + error);
            }


        });

        servercall.asynCallLoginData();
    }

}
