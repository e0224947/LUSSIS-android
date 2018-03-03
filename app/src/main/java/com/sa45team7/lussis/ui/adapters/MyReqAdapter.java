package com.sa45team7.lussis.ui.adapters;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sa45team7.lussis.R;
import com.sa45team7.lussis.rest.model.Requisition;
import com.sa45team7.lussis.ui.detailsscreen.MyReqDetailActivity;
import com.sa45team7.lussis.utils.DateConvertUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by nhatton on 1/23/18.
 * Adapter used for showing requisitions made by current user
 */

public class MyReqAdapter extends RecyclerView.Adapter<MyReqAdapter.MyReqHolder> {

    private final List<Requisition> mValues;

    public MyReqAdapter(List<Requisition> list) {
        mValues = list;
        Collections.sort(mValues);
    }

    @Override
    public MyReqHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_my_req, parent, false);
        return new MyReqHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyReqHolder holder, int position) {
        holder.mItem = mValues.get(position);

        String date = DateConvertUtil.convertForRequisitions(holder.mItem.getRequisitionDate());
        holder.mReqDateText.setText(date);

        String remark = holder.mItem.getRequestRemarks() == null ? "" : holder.mItem.getRequestRemarks();
        holder.mReqRemarkText.setText(remark);

        Resources res = holder.mView.getContext().getResources();
        String status = holder.mItem.getStatus();
        holder.mReqStatusText.setText(status);

        switch (status) {
            case "pending":
                holder.mReqStatusText.setTextColor(res.getColor(R.color.colorYellow));
                break;
            case "approved":
                holder.mReqStatusText.setTextColor(res.getColor(R.color.colorGreen));
                break;
            case "rejected":
                holder.mReqStatusText.setTextColor(res.getColor(R.color.colorRed));
                break;
            default:
                holder.mReqStatusText.setTextColor(res.getColor(R.color.colorBlack));
                break;
        }

        String approvalRemark = holder.mItem.getApprovalRemarks() == null ? "" : holder.mItem.getApprovalRemarks();
        holder.mApprovalRemarkText.setText(approvalRemark);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MyReqDetailActivity.class);
                String data = new Gson().toJson(holder.mItem);
                intent.putExtra("requisition", data);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class MyReqHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mReqDateText;
        final TextView mReqRemarkText;
        final TextView mReqStatusText;
        final TextView mApprovalRemarkText;
        Requisition mItem;

        MyReqHolder(View view) {
            super(view);
            mView = view;
            mReqDateText = view.findViewById(R.id.req_date_text);
            mReqRemarkText = view.findViewById(R.id.req_remark_text);
            mReqStatusText = view.findViewById(R.id.req_status_text);
            mApprovalRemarkText = view.findViewById(R.id.approval_remark_text);

        }
    }
}
