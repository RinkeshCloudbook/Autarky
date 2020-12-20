package com.autarky.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.autarky.R;
import com.autarky.adapter.DeviceListAdapter;
import com.autarky.constant.ApplicationConstants;
import com.autarky.utils.BaseFragment;

import java.util.ArrayList;


public class DeviceListFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        RecyclerView rvListView = view.findViewById(R.id.rvListView);
        TextView tvToolbarTitle = view.findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.devices));


        if (rvListView.getLayoutManager() == null) {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity()
                    , LinearLayoutManager.VERTICAL, false);
            rvListView.setLayoutManager(layoutManager);
            rvListView.setItemAnimator(new DefaultItemAnimator());
        }

        rvListView.setAdapter(new DeviceListAdapter(new ArrayList<>(), mContext));


        view.findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new AddDeviceFragment();
                mContext.replaceFragment(fragment, true, ApplicationConstants.AddDevice);

            }
        });

//        callRefreshToken();

        return view;
    }
}