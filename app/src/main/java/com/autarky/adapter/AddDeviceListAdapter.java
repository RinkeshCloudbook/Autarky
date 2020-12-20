package com.autarky.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.autarky.R;
import com.autarky.utils.OnClickDevice;

import java.util.List;

public class AddDeviceListAdapter extends RecyclerView.Adapter<AddDeviceListAdapter.ItemViewHolder> {


    private List<String> dashboardModelList;
    private Context mContext;

    private OnClickDevice mOnClickDevice;

    public AddDeviceListAdapter(List<String> dashboardModels, Context context, OnClickDevice onClickDevice) {

        this.mOnClickDevice = onClickDevice;
        this.dashboardModelList = dashboardModels;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new ItemViewHolder(LayoutInflater.from
                (parent.getContext()).inflate(R.layout.add_devices_layout, parent, false));
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        if (mContext == null) {
            return;
        }

        holder.device_name.setText(dashboardModelList.get(position));

        holder.device_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnClickDevice != null) {
                    mOnClickDevice.onClickDevice(dashboardModelList.get(position));
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return dashboardModelList.size();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView device_name;

        ItemViewHolder(View itemView) {
            super(itemView);
            device_name = itemView.findViewById(R.id.device_name);
        }
    }

}