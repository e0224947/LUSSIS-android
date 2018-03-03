package com.sa45team7.lussis.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by nhatton on 1/22/18.
 * Model class for Delegate
 */

public class Delegate {

    @SerializedName("DelegateId")
    private int delegateId;

    @SerializedName("Employee")
    private Employee employee;

    @SerializedName("StartDate")
    private Date startDate;

    @SerializedName("EndDate")
    private Date endDate;

    public int getDelegateId() {
        return delegateId;
    }

    public void setDelegateId(int delegateId) {
        this.delegateId = delegateId;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
