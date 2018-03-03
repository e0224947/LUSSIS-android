package com.sa45team7.lussis.ui.detailsscreen;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sa45team7.lussis.R;
import com.sa45team7.lussis.helpers.UserManager;
import com.sa45team7.lussis.rest.LUSSISClient;
import com.sa45team7.lussis.rest.model.LUSSISResponse;
import com.sa45team7.lussis.rest.model.Requisition;
import com.sa45team7.lussis.ui.adapters.ReqDetailAdapter;
import com.sa45team7.lussis.utils.DateConvertUtil;
import com.sa45team7.lussis.utils.ErrorUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class PendingReqDetailActivity extends AppCompatActivity {

    private EditText reasonText;

    private Requisition requisition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_req_detail);
        String data = getIntent().getStringExtra("requisition");
        requisition = new Gson().fromJson(data, Requisition.class);

        TextView reqNameText = findViewById(R.id.req_emp_text);
        reqNameText.setText(requisition.getRequisitionEmp().getFullName());

        TextView reqDateText = findViewById(R.id.req_date_text);
        String date = DateConvertUtil.convertForDetail(requisition.getRequisitionDate());
        reqDateText.setText(date);

        TextView remarkText = findViewById(R.id.remark_text);
        remarkText.setText(requisition.getRequestRemarks());

        RecyclerView listView = findViewById(R.id.stationery_list_view);
        ReqDetailAdapter adapter = new ReqDetailAdapter(requisition.getRequisitionDetails());
        listView.setAdapter(adapter);

        reasonText = findViewById(R.id.reason_text);

        Button rejectButton = findViewById(R.id.reject_button);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processRequisition("rejected");
            }
        });

        Button approveButton = findViewById(R.id.approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processRequisition("approved");
            }
        });

        boolean isMyReq = getIntent().getBooleanExtra("my_req", false);
        rejectButton.setVisibility(isMyReq ? GONE : VISIBLE);
        approveButton.setVisibility(isMyReq ? GONE : VISIBLE);
    }

    private void processRequisition(String status) {
        requisition.setRequisitionEmpNum(requisition.getRequisitionEmp().getEmpNum());
        requisition.setApprovalRemarks(reasonText.getText().toString());
        requisition.setApprovalEmpNum(UserManager.getInstance().getCurrentEmployee().getEmpNum());
        requisition.setApprovalEmp(UserManager.getInstance().getCurrentEmployee());
        requisition.setStatus(status);

        Call<LUSSISResponse> call = LUSSISClient.getApiService().processRequisition(requisition);
        call.enqueue(new Callback<LUSSISResponse>() {
            @Override
            public void onResponse(@NonNull Call<LUSSISResponse> call, @NonNull Response<LUSSISResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(PendingReqDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String error = ErrorUtil.parseError(response).getMessage();
                    Toast.makeText(PendingReqDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LUSSISResponse> call, @NonNull Throwable t) {
                Toast.makeText(PendingReqDetailActivity.this,
                        "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
