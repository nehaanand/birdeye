package com.example.birdeyeassignment.job_scheduler;

import android.app.ProgressDialog;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.view.Gravity;

import com.example.birdeyeassignment.R;
import com.example.birdeyeassignment.customers.model.All_Customers_Response;
import com.example.birdeyeassignment.network.ReUseComponets;
import com.example.birdeyeassignment.network.listeners.Customers_DataListener;
import com.example.birdeyeassignment.utils.Sqlite_Database;

import java.util.List;

public class JobService1 extends JobService {
    Sqlite_Database db;
    JobParameters params;
    ProgressDialog pdilog;

    @Override
    public boolean onStartJob(JobParameters params) {
        db = new Sqlite_Database(JobService1.this);

        get_Customers();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobFinished(params, false);
        return false;
    }

    public void get_Customers() {
        ReUseComponets servercall = new ReUseComponets(new Customers_DataListener() {
            @Override
            public void showLoginResult(int code, List<All_Customers_Response> result) {
                if (!result.get(0).getEmailId().equals("")) {

                    db.delete_Customer("");
                    db.insert_Customers(result);
                } else {

                }
            }

            @Override
            public void showError(Throwable error) {
                Log.d("Error", "" + error);
            }


        });

        servercall.asynCallLoginData();
    }

}
