package com.sa45team7.lussis.rest;

import com.sa45team7.lussis.rest.model.Adjustment;
import com.sa45team7.lussis.rest.model.Delegate;
import com.sa45team7.lussis.rest.model.Disbursement;
import com.sa45team7.lussis.rest.model.Employee;
import com.sa45team7.lussis.rest.model.LUSSISResponse;
import com.sa45team7.lussis.rest.model.Requisition;
import com.sa45team7.lussis.rest.model.RetrievalItem;
import com.sa45team7.lussis.rest.model.Stationery;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by nhatton on 1/17/18.
 * Every APIs stay here
 */

public interface LUSSISService {

    /**
     * Account API
     */
    @FormUrlEncoded
    @POST("auth/Login")
    Call<Employee> login(@Field("Email") String email,
                         @Field("Password") String password);

    @FormUrlEncoded
    @POST("auth/ForgotPassword")
    Call<LUSSISResponse> forgot(@Field("Email") String email);

    /**
     * Stationery API
     */
    @GET("Stationeries/")
    Call<List<Stationery>> getAllStationeries();

    @GET("Stationeries/{id}")
    Call<Stationery> getStationery(@Path("id") String itemNum);

    /**
     * Requisitions API
     */
    @GET("Requisitions/Pending/{dept}")
    Call<List<Requisition>> getPendingRequisitions(@Path("dept") String deptCode);

    @POST("Requisitions/Process")
    Call<LUSSISResponse> processRequisition(@Body Requisition requisition);

    @GET("Requisitions/MyReq/{empnum}")
    Call<List<Requisition>> getMyRequisitions(@Path("empnum") int empNum);

    @GET("Requisitions/Retrieval")
    Call<List<RetrievalItem>> getRetrievalList();

    @POST("Requisitions/Create")
    Call<LUSSISResponse> createNewRequisition(@Body Requisition requisition);

    /**
     * Delegate API
     */
    @GET("Delegate/Get/{dept}")
    Call<Delegate> getDelegate(@Path("dept") String deptCode);

    @GET("Delegate/Employee/{dept}")
    Call<List<Employee>> getEmployeeListForDelegate(@Path("dept") String deptCode);

    @POST("Delegate/Create/{empnum}")
    Call<Delegate> postDelegate(@Path("empnum") int empNum, @Body Delegate delegate);

    @POST("Delegate/Delete")
    Call<LUSSISResponse> deleteDelegate(@Body Delegate delegate);

    /**
     * Disbursement API
     */
    @GET("Disbursement/")
    Call<List<Disbursement>> getDisbursements();

    @GET("Disbursement/{id}")
    Call<Disbursement> getDisbursementById(@Path("id") int disbursementId);

    @GET("Disbursement/Upcoming/{dept}")
    Call<Disbursement> getUpcomingCollection(@Path("dept") String deptCode);

    @POST("Disbursement/Acknowledge/")
    Call<LUSSISResponse> acknowledge(@Query("id") int disbursementId, @Query("empnum") int empNum);

    /**
     * Stock Adjustment API
     */
    @POST("StockAdjustment/")
    Call<LUSSISResponse> stockAdjust(@Body Adjustment adjustment);
}
