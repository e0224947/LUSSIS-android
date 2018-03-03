package com.sa45team7.lussis.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.sa45team7.lussis.R;
import com.sa45team7.lussis.rest.model.Stationery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by nhatton on 1/26/18.
 * Adapter used for showing stationeries
 */

public class StationeryAdapter extends RecyclerView.Adapter<StationeryAdapter.StationeryHolder>
        implements Filterable {

    private List<Stationery> mValues;
    private List<Stationery> mOriginValues;
    private CategoryFilter categoryFilter;
    private OnStationeryListInteractionListener mListener;

    public StationeryAdapter(List<Stationery> list, OnStationeryListInteractionListener listener) {
        mValues = list;
        Collections.sort(mValues);
        mOriginValues = mValues;

        mListener = listener;
    }

    @Override
    public StationeryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_stationery, parent, false);
        return new StationeryHolder(view);
    }

    @Override
    public void onBindViewHolder(final StationeryHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mDescriptionText.setText(holder.mItem.getDescription());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) mListener.onSelectStationery(holder.mItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    @Override
    public Filter getFilter() {
        if (categoryFilter == null) categoryFilter = new CategoryFilter();
        return categoryFilter;
    }

    public void filterByDescription(String query) {
        query = query.toLowerCase().trim();

        if (!query.isEmpty()) {
            ArrayList<Stationery> result = new ArrayList<>();

            for (Stationery item : mOriginValues) {
                if (item.getDescription().toLowerCase().trim().contains(query))
                    result.add(item);
            }

            mValues = result;
            notifyDataSetChanged();
        } else {
            mValues = mOriginValues;
            notifyDataSetChanged();
        }

    }

    class StationeryHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mDescriptionText;
        Stationery mItem;

        StationeryHolder(View view) {
            super(view);
            mView = view;
            mDescriptionText = view.findViewById(R.id.description_text);
        }
    }

    /**
     * Filter class used for filtering stationery based on category
     */
    private class CategoryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence category) {
            FilterResults results = new FilterResults();

            if (category.equals("All")) {
                results.count = mOriginValues.size();
                results.values = mOriginValues;
            } else {
                ArrayList<Stationery> filteredList = new ArrayList<>();
                for (Stationery item : mOriginValues) {
                    if (item.getCategory().equals(category)) {
                        filteredList.add(item);
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mValues = (ArrayList<Stationery>) results.values;
            notifyDataSetChanged();
        }
    }

    public interface OnStationeryListInteractionListener {
        void onSelectStationery(Stationery item);
    }
}
