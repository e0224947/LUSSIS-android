package com.sa45team7.lussis.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.rest.model.RequisitionDetail;

import java.util.List;

/**
 * Created by nhatton on 1/22/18.
 * Adapter used for showing details in a requisition
 */

public class ReqDetailAdapter extends RecyclerView.Adapter<ReqDetailAdapter.ReqDetailHolder> {

    private final List<RequisitionDetail> mValues;

    public ReqDetailAdapter(List<RequisitionDetail> list) {
        mValues = list;
    }

    @Override
    public ReqDetailHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_detail_req, parent, false);
        return new ReqDetailHolder(view);
    }

    @Override
    public void onBindViewHolder(ReqDetailHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.descriptionText.setText(holder.mItem.getDescription());
        holder.qtyText.setText(String.valueOf(holder.mItem.getQuantity()));
        holder.uomText.setText(holder.mItem.getUnitOfMeasure());
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ReqDetailHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView descriptionText;
        final TextView qtyText;
        final TextView uomText;
        RequisitionDetail mItem;

        ReqDetailHolder(View view) {
            super(view);
            mView = view;
            descriptionText = view.findViewById(R.id.description_text);
            qtyText = view.findViewById(R.id.qty_text);
            uomText = view.findViewById(R.id.uom_text);
        }
    }
}
