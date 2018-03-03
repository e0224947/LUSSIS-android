package com.sa45team7.lussis.ui.detailsscreen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sa45team7.lussis.R;
import com.sa45team7.lussis.rest.model.Requisition;
import com.sa45team7.lussis.ui.adapters.ReqDetailAdapter;
import com.sa45team7.lussis.utils.DateConvertUtil;

public class MyReqDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_req_detail);
        String data = getIntent().getStringExtra("requisition");
        Requisition requisition = new Gson().fromJson(data, Requisition.class);

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

        TextView approvalNameText = findViewById(R.id.approval_emp_text);
        if(requisition.getApprovalEmp()!=null){
            approvalNameText.setText(requisition.getApprovalEmp().getFullName());
        }

        TextView statusText = findViewById(R.id.status_text);
        statusText.setText(requisition.getStatus());
        switch (requisition.getStatus()) {
            case "pending":
                statusText.setTextColor(getResources().getColor(R.color.colorYellow));
                break;
            case "approved":
                statusText.setTextColor(getResources().getColor(R.color.colorGreen));
                break;
            case "rejected":
                statusText.setTextColor(getResources().getColor(R.color.colorRed));
                break;
            default:
                statusText.setTextColor(getResources().getColor(R.color.colorBlack));
                break;
        }

        TextView reasonText = findViewById(R.id.reason_text);
        reasonText.setText(requisition.getApprovalRemarks());
    }
}
