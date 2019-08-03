package com.example.birdeyeassignment.job_scheduler;

import android.annotation.TargetApi;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.evernote.android.job.Job;
import com.example.birdeyeassignment.customers.model.All_Customers_Response;
import com.example.birdeyeassignment.network.ReUseComponets;
import com.example.birdeyeassignment.network.listeners.Customers_DataListener;
import com.example.birdeyeassignment.utils.Sqlite_Database;

import java.util.List;

public class ShowNotificationJob extends Job {

    static final String TAG = "job_tag";
    Sqlite_Database db;
    public static final int JOB_ID=1001;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {

        db = new Sqlite_Database(getContext());
        get_Customers();

        return Result.SUCCESS;
    }

    public void get_Customers() {
        ReUseComponets servercall = new ReUseComponets(new Customers_DataListener() {
            @Override
            public void showLoginResult(int code, List<All_Customers_Response> result) {
                if (!result.get(0).getEmailId().equals("")) {
                    db.delete_Customer("");
                    db.insert_Customers(result);
                }
                else
                {

                }
            }

            @Override
            public void showError(Throwable error) {
                Log.d("Error", "" + error);
            }


        });

        servercall.asynCallLoginData();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setJobScheduler(Context context){
        JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName serviceName = new ComponentName(context, JobService1.class);
        JobInfo jobInfo;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                    .setMinimumLatency(5*60*1000)
                    .build();
        }else{
            jobInfo = new JobInfo.Builder(JOB_ID, serviceName)
                    .setPeriodic(5*60*1000)
                    .build();
        }
        jobScheduler.schedule(jobInfo);
    }

}
