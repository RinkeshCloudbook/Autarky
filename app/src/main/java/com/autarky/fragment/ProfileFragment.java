package com.autarky.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autarky.R;
import com.autarky.utils.BaseFragment;


public class ProfileFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        TextView tvToolbarTitle = view.findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.profile));

        view.findViewById(R.id.ivSearchTitle).setVisibility(View.GONE);
        view.findViewById(R.id.ivNotificationTitle).setVisibility(View.GONE);
        return view;
    }
}