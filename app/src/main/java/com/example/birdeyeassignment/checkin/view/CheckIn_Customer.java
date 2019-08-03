package com.example.birdeyeassignment.checkin.view;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.birdeyeassignment.R;
import com.example.birdeyeassignment.checkin.model.Checkin_Request;
import com.example.birdeyeassignment.checkin.model.Checkin_Response;
import com.example.birdeyeassignment.checkin.model.Employees;
import com.example.birdeyeassignment.customers.model.All_Customers_Response;
import com.example.birdeyeassignment.network.ReUseComponets;
import com.example.birdeyeassignment.network.listeners.Checkin_DataListener;
import com.example.birdeyeassignment.utils.Network_Call;
import com.example.birdeyeassignment.utils.Sqlite_Database;
import java.util.ArrayList;
import java.util.List;

public class CheckIn_Customer extends AppCompatActivity {


    EditText et_name, et_emailid, et_phone;
    Button btn_save;
    Sqlite_Database db;
    Checkin_Request request;
    Employees emp1;
    ProgressDialog pdilog;
    static String regex = "\\d{3}-\\d{3}-\\d{4}^";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in__customer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("CheckIn");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        db = new Sqlite_Database(CheckIn_Customer.this);
        et_name = (EditText) findViewById(R.id.et_name);
        et_emailid = (EditText) findViewById(R.id.et_emailid);
        et_phone = (EditText) findViewById(R.id.et_phone);
        btn_save = (Button) findViewById(R.id.btn_save);
        request = new Checkin_Request();



        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (et_name.getText().toString().equals("")) {
                    et_name.setError("Field can't be empty");
                } else if (et_emailid.getText().toString().equals("")) {
                    et_emailid.setError("Field can't be empty");
                } else if (et_phone.getText().toString().equals("")) {
                    et_phone.setError("Field can't be empty");
                } else if (et_phone.getText().toString().matches(regex)) {
                    et_phone.setError("Enter valid Phone Number");
                } else {
                    emp1 = new Employees();
                    emp1.setEmailId(et_emailid.getText().toString());
                    ArrayList<Employees> arraylist = new ArrayList<>();
                    arraylist.add(emp1);
                    request.setEmployees(arraylist);

                    request.setEmailId(et_emailid.getText().toString());
                    request.setName(et_name.getText().toString());
                    request.setPhone(et_phone.getText().toString());
                    request.setSmsEnabled("1");
                    pdilog = new ProgressDialog(CheckIn_Customer.this, R.style.CustomProgress);
                    pdilog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                    pdilog.getWindow().setGravity(Gravity.CENTER);
                    pdilog.show();
                    if(!Network_Call.isNetworkAvailable(CheckIn_Customer.this)){
                        Toast.makeText(CheckIn_Customer.this, "Check your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        serverCallforCheckIn(request);
                    }

                }
            }
        });
    }

    public void serverCallforCheckIn(Checkin_Request map) {

        ReUseComponets servercall = new ReUseComponets(new Checkin_DataListener() {

            @Override
            public void showCheckInResult(int code, Checkin_Response result) {
                pdilog.dismiss();
                if (code == 200) {
                    Toast.makeText(CheckIn_Customer.this, "Checked IN Succesfully", Toast.LENGTH_LONG).show();
                    List<All_Customers_Response> list = new ArrayList<>();
                    All_Customers_Response res = new All_Customers_Response();
                    res.setNumber(result.getCustomerId());
                    res.setFirstName(et_name.getText().toString());
                    res.setMiddleName("");
                    res.setLastName("");
                    res.setEmailId(et_emailid.getText().toString());
                    res.setPhone(et_phone.getText().toString());

                    res.setEmailId(et_emailid.getText().toString());
                    list.add(res);
                    db.insert_Customers(list);
                    finish();

                } else if (code == 400) {
                    Toast.makeText(CheckIn_Customer.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CheckIn_Customer.this, "Unexpected Error Occured", Toast.LENGTH_SHORT).show();

                }


            }

            @Override
            public void showError(Throwable error) {

            }
        });
        servercall.asynCallForCheckIn(map);

    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


}
