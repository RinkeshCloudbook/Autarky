package com.autarky.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.autarky.R;
import com.autarky.utils.BaseFragment;

public class EventListFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device_list, container, false);

        RecyclerView rvListView = view.findViewById(R.id.rvListView);
        TextView tvToolbarTitle = view.findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.events));

        view.findViewById(R.id.floatingActionButton).setVisibility(View.GONE);
        view.findViewById(R.id.ivSearchTitle).setVisibility(View.GONE);
        view.findViewById(R.id.ivNotificationTitle).setVisibility(View.GONE);
        return view;
    }
}