package com.sa45team7.lussis.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.rest.model.Disbursement;
import com.sa45team7.lussis.utils.DateConvertUtil;

import java.util.List;

/**
 * Created by nhatton on 1/23/18.
 * Adapter used for displaying list of upcoming disbursement
 */

public class DisbursementAdapter extends RecyclerView.Adapter<DisbursementAdapter.DisbursementHolder> {

    private final List<Disbursement> mValues;
    private final OnDisbursementListInteractionListener mListener;

    public DisbursementAdapter(List<Disbursement> list, OnDisbursementListInteractionListener listener) {
        mValues = list;
        mListener = listener;
    }

    @Override
    public DisbursementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_disbursement, parent, false);
        return new DisbursementHolder(view);
    }

    @Override
    public void onBindViewHolder(final DisbursementHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDeptText.setText(holder.mItem.getDepartmentName());
        holder.mPlaceText.setText(String.format("At: %s", holder.mItem.getCollectionPoint()));

        String date = DateConvertUtil.convertForDetail(holder.mItem.getCollectionDate());
        holder.mDateText.setText(date);
        holder.mTimeText.setText(holder.mItem.getCollectionTime());

        holder.mGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null)
                    mListener.onSelectDisbursement(holder.getAdapterPosition(), holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class DisbursementHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mDeptText;
        final TextView mPlaceText;
        final TextView mDateText;
        final TextView mTimeText;
        final Button mGenerateButton;
        Disbursement mItem;

        DisbursementHolder(View view) {
            super(view);
            mView = view;
            mDeptText = view.findViewById(R.id.dis_dept_text);
            mPlaceText = view.findViewById(R.id.dis_place_text);
            mDateText = view.findViewById(R.id.dis_date_text);
            mTimeText = view.findViewById(R.id.dis_time_text);
            mGenerateButton = view.findViewById(R.id.generate_button);
        }
    }

    public interface OnDisbursementListInteractionListener {
        void onSelectDisbursement(int position, Disbursement item);
    }
}
