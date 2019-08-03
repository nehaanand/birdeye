package com.example.birdeyeassignment.checkin.model;

import java.util.ArrayList;

public class Checkin_Request
{
    private String phone;

    private String name;

    private String smsEnabled;

    private String emailId;

    private ArrayList<Employees> employees;

    public String getPhone ()
    {
        return phone;
    }

    public void setPhone (String phone)
    {
        this.phone = phone;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getSmsEnabled ()
    {
        return smsEnabled;
    }

    public void setSmsEnabled (String smsEnabled)
    {
        this.smsEnabled = smsEnabled;
    }

    public String getEmailId ()
    {
        return emailId;
    }

    public void setEmailId (String emailId)
    {
        this.emailId = emailId;
    }

    public ArrayList<Employees> getEmployees ()
    {
        return employees;
    }

    public void setEmployees (ArrayList<Employees> employees)

    {
        this.employees = employees;
    }

}