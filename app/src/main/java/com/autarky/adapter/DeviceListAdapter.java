package com.autarky.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autarky.R;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ItemViewHolder> {


    private List<String> dashboardModelList;
    private Activity mContext;

    public DeviceListAdapter(List<String> dashboardModels, Activity context) {

        this.dashboardModelList = dashboardModels;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ItemViewHolder(LayoutInflater.from
                (parent.getContext()).inflate(R.layout.devices_row_layout, parent, false));
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        if (mContext == null) {
            return;
        }


    }


    @Override
    public int getItemCount() {
        return 5;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {


        ItemViewHolder(View itemView) {
            super(itemView);

        }
    }

}