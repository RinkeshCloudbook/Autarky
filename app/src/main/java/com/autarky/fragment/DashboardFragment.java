package com.autarky.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.autarky.R;
import com.autarky.utils.BaseFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends BaseFragment {


    ViewPager vpDashboard;
    TabLayout tlDashboard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tlDashboard = view.findViewById(R.id.tlDashboard);
        vpDashboard = view.findViewById(R.id.vpDashboard);
        setupViewPager(vpDashboard);
        tlDashboard.setupWithViewPager(vpDashboard);
        setupTabIcons();
        return view;
    }

    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabOne.setText("Devices");
        tabOne.setTextColor(ContextCompat.getColor(mContext, R.color.color_1D74BA));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_device_active, 0, 0);
        tlDashboard.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Events");
        tabTwo.setTextColor(ContextCompat.getColor(mContext, R.color.color_929398));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_events, 0, 0);
        tlDashboard.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabThree.setText("Profile");
        tabThree.setTextColor(ContextCompat.getColor(mContext, R.color.color_929398));
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile, 0, 0);
        tlDashboard.getTabAt(2).setCustomView(tabThree);

        TextView tabFourth = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.custom_tab, null);
        tabFourth.setText("Settings");
        tabFourth.setTextColor(ContextCompat.getColor(mContext, R.color.color_929398));
        tabFourth.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_setting, 0, 0);
        tlDashboard.getTabAt(3).setCustomView(tabFourth);
        tlDashboard.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_device, 0, 0);
                tabOne.setTextColor(ContextCompat.getColor(mContext, R.color.color_929398));
                tlDashboard.getTabAt(0).setCustomView(tabOne);
                tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_events, 0, 0);
                tabTwo.setTextColor(ContextCompat.getColor(mContext, R.color.color_929398));
                tlDashboard.getTabAt(1).setCustomView(tabTwo);
                tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile, 0, 0);
                tabThree.setTextColor(ContextCompat.getColor(mContext, R.color.color_929398));
                tlDashboard.getTabAt(2).setCustomView(tabThree);
                tabFourth.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_setting, 0, 0);
                tabFourth.setTextColor(ContextCompat.getColor(mContext, R.color.color_929398));
                tlDashboard.getTabAt(3).setCustomView(tabFourth);

                switch (tab.getPosition()) {
                    case 0:
                        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_device_active, 0, 0);
                        tabOne.setTextColor(ContextCompat.getColor(mContext, R.color.color_1D74BA));
                        tlDashboard.getTabAt(0).setCustomView(tabOne);
                        break;
                    case 1:
                        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_events_active, 0, 0);
                        tabTwo.setTextColor(ContextCompat.getColor(mContext, R.color.color_1D74BA));
                        tlDashboard.getTabAt(1).setCustomView(tabTwo);
                        break;
                    case 2:
                        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_profile_active, 0, 0);
                        tabThree.setTextColor(ContextCompat.getColor(mContext, R.color.color_1D74BA));
                        tlDashboard.getTabAt(2).setCustomView(tabThree);
                        break;
                    case 3:
                        tabFourth.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_setting_active, 0, 0);
                        tabFourth.setTextColor(ContextCompat.getColor(mContext, R.color.color_1D74BA));
                        tlDashboard.getTabAt(3).setCustomView(tabFourth);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new DeviceListFragment(), "Devices");
        adapter.addFrag(new EventListFragment(), "Events");
        adapter.addFrag(new ProfileFragment(), "Profile");
        adapter.addFrag(new SettingsFragment(), "Settings");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}