package com.sa45team7.lussis.ui.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sa45team7.lussis.R;

import java.util.ArrayList;

/**
 * Created by nhatton on 1/25/18.
 * Adapter used for showing panel icons in Home screen
 */

public class HomeAdapter extends ArrayAdapter<MenuItem> {

    private Context mContext;
    private int mResourceId;
    private ArrayList<MenuItem> mValues;


    public HomeAdapter(@NonNull Context context, int resourceId, @NonNull ArrayList<MenuItem> list) {
        super(context, resourceId, list);
        mContext = context;
        mResourceId = resourceId;
        mValues = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(mResourceId, parent, false);

            holder = new ViewHolder();
            holder.mIcon = convertView.findViewById(R.id.action_image);
            holder.mText = convertView.findViewById(R.id.action_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        MenuItem item = getItem(position);
        if(item == null){
            convertView.setVisibility(View.INVISIBLE);
        } else {
            holder.mIcon.setBackground(item.getIcon());
            holder.mText.setText(item.getTitle());
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView mIcon;
        TextView mText;
    }
}
