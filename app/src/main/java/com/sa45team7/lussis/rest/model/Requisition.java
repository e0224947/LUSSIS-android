package com.sa45team7.lussis.rest.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by nhatton on 1/20/18.
 */

public class Requisition implements Comparable<Requisition> {

    @SerializedName("RequisitionId")
    private int requisitionId;

    @SerializedName("RequisitionEmpNum")
    private int requisitionEmpNum;

    @SerializedName("RequisitionEmp")
    private Employee requisitionEmp;

    @SerializedName("RequisitionDate")
    private Date requisitionDate;

    @SerializedName("RequestRemarks")
    private String requestRemarks;

    @SerializedName("ApprovalEmpNum")
    private int approvalEmpNum;

    @SerializedName("ApprovalEmp")
    private Employee approvalEmp;

    @SerializedName("ApprovalRemarks")
    private String approvalRemarks;

    @SerializedName("Status")
    private String status;

    @SerializedName("RequisitionDetails")
    private List<RequisitionDetail> requisitionDetails;

    public int getRequisitionId() {
        return requisitionId;
    }

    public void setRequisitionId(int requisitionId) {
        this.requisitionId = requisitionId;
    }

    public int getRequisitionEmpNum() {
        return requisitionEmpNum;
    }

    public void setRequisitionEmpNum(int requisitionEmpNum) {
        this.requisitionEmpNum = requisitionEmpNum;
    }

    public Employee getRequisitionEmp() {
        return requisitionEmp;
    }

    public void setRequisitionEmp(Employee requisitionEmp) {
        this.requisitionEmp = requisitionEmp;
    }

    public Date getRequisitionDate() {
        return requisitionDate;
    }

    public void setRequisitionDate(Date requisitionDate) {
        this.requisitionDate = requisitionDate;
    }

    public String getRequestRemarks() {
        return requestRemarks;
    }

    public void setRequestRemarks(String requestRemarks) {
        this.requestRemarks = requestRemarks;
    }

    public int getApprovalEmpNum() {
        return approvalEmpNum;
    }

    public void setApprovalEmpNum(int approvalEmpNum) {
        this.approvalEmpNum = approvalEmpNum;
    }

    public Employee getApprovalEmp() {
        return approvalEmp;
    }

    public void setApprovalEmp(Employee approvalEmp) {
        this.approvalEmp = approvalEmp;
    }

    public String getApprovalRemarks() {
        return approvalRemarks;
    }

    public void setApprovalRemarks(String approvalRemarks) {
        this.approvalRemarks = approvalRemarks;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<RequisitionDetail> getRequisitionDetails() {
        return requisitionDetails;
    }

    public void setRequisitionDetails(List<RequisitionDetail> requisitionDetails) {
        this.requisitionDetails = requisitionDetails;
    }

    @Override
    public int compareTo(@NonNull Requisition o) {
        return o.requisitionId - requisitionId;
    }
}
