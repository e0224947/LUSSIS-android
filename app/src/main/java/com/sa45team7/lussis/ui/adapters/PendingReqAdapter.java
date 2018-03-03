package com.sa45team7.lussis.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.rest.model.Requisition;
import com.sa45team7.lussis.utils.DateConvertUtil;

import java.util.Collections;
import java.util.List;

/**
 * Created by nhatton on 1/23/18.
 * Adapter used for showing pending requisitions needed to be approved
 */

public class PendingReqAdapter extends RecyclerView.Adapter<PendingReqAdapter.PendingReqHolder> {

    private final List<Requisition> mValues;
    private final OnPendingReqListInteractionListener mListener;

    public PendingReqAdapter(List<Requisition> items, OnPendingReqListInteractionListener listener) {
        mValues = items;
        Collections.sort(mValues);
        mListener = listener;
    }

    @Override
    public PendingReqHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_pending_req, parent, false);
        return new PendingReqHolder(view);
    }

    @Override
    public void onBindViewHolder(final PendingReqHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mRequestNameView.setText("By " + holder.mItem.getRequisitionEmp().getFullName());

        String displayDate = DateConvertUtil.convertForRequisitions(holder.mItem.getRequisitionDate());
        holder.mRequestDateView.setText(displayDate);
        holder.mRequestRemarkView.setText(holder.mItem.getRequestRemarks());

        holder.mApproveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onProcessRequisition(holder.getAdapterPosition(), "approved", holder.mItem);
                }
            }
        });

        holder.mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onProcessRequisition(holder.getAdapterPosition(), "rejected", holder.mItem);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onSelectRequisition(holder.getAdapterPosition(), holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public void removeItem(int position) {
        mValues.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mValues.size());
    }

    class PendingReqHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mRequestNameView;
        final TextView mRequestDateView;
        final TextView mRequestRemarkView;
        final Button mApproveButton;
        final Button mRejectButton;
        Requisition mItem;

        PendingReqHolder(View view) {
            super(view);
            mView = view;
            mRequestNameView = view.findViewById(R.id.request_name_text);
            mRequestDateView = view.findViewById(R.id.request_date_text);
            mRequestRemarkView = view.findViewById(R.id.request_remark_text);
            mApproveButton = view.findViewById(R.id.approve_button);
            mRejectButton = view.findViewById(R.id.reject_button);
        }
    }

    public interface OnPendingReqListInteractionListener {
        void onSelectRequisition(int position, Requisition item);

        void onProcessRequisition(int position, String status, Requisition item);
    }
}
