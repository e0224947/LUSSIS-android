package com.sa45team7.lussis.rest.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by nhatton on 1/22/18.
 */

public class Disbursement {

    @SerializedName("DisbursementId")
    private int disbursementId;

    @SerializedName("CollectionPointId")
    private int collectionPointId;

    @SerializedName("CollectionPoint")
    private String collectionPoint;

    @SerializedName("CollectionDate")
    private Date collectionDate;

    @SerializedName("CollectionTime")
    private String collectionTime;

    @SerializedName("DepartmentName")
    private String departmentName;

    @SerializedName("DisbursementDetails")
    private List<RequisitionDetail> disbursementDetails;

    public int getDisbursementId() {
        return disbursementId;
    }

    public void setDisbursementId(int disbursementId) {
        this.disbursementId = disbursementId;
    }

    public int getCollectionPointId() {
        return collectionPointId;
    }

    public void setCollectionPointId(int collectionPointId) {
        this.collectionPointId = collectionPointId;
    }

    public String getCollectionPoint() {
        return collectionPoint;
    }

    public void setCollectionPoint(String collectionPoint) {
        this.collectionPoint = collectionPoint;
    }

    public Date getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(Date collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(String collectionTime) {
        this.collectionTime = collectionTime;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public List<RequisitionDetail> getDisbursementDetails() {
        return disbursementDetails;
    }

    public void setDisbursementDetails(List<RequisitionDetail> disbursementDetails) {
        this.disbursementDetails = disbursementDetails;
    }
}
