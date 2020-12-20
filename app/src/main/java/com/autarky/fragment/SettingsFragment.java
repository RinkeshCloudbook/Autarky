package com.autarky.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.autarky.R;
import com.autarky.utils.BaseFragment;

public class SettingsFragment extends BaseFragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        TextView tvToolbarTitle = view.findViewById(R.id.tvToolbarTitle);
        tvToolbarTitle.setText(getResources().getString(R.string.setting));
        view.findViewById(R.id.ivSearchTitle).setVisibility(View.GONE);
        view.findViewById(R.id.ivNotificationTitle).setVisibility(View.GONE);
        view.findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext, R.style.AlertDialogTheme)
                        .setIcon(R.drawable.ic_logout)
                        .setTitle(getResources().getString(R.string.logout))
                        .setMessage(getResources().getString(R.string.logout_message))
                        .setPositiveButton(getResources().getString(R.string.yes_text), (dialog, which) -> {

                            sessionManager.logoutUser();
                        })

                        .setNegativeButton(getResources().getString(R.string.no_text), null)
                        .show();

            }
        });

        return view;
    }
}