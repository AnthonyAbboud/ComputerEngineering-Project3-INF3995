package com.example.mounia.client.Fragments.Widgets;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mounia.client.Fragments.WidgetsToIgnore.FragmentDebugOptions;
import com.example.mounia.client.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTabContainer extends FragmentWidget {

    public TabLayout getTabLayout() { return tabLayout; }

    public ViewPager getViewPager() { return viewPager; }

    public CustomPagerAdapter getCustomPagerAdapter() { return customPagerAdapter; }

    private TabLayout tabLayout = null;
    private ViewPager viewPager = null;
    private CustomPagerAdapter customPagerAdapter = null;

    public FragmentTabContainer() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_tab_container, container, false);
    }

    @SuppressLint("NewApi")
    @Override
    public void onStart() {
        super.onStart();

        tabLayout = new TabLayout(getActivity());
        tabLayout.setId(View.generateViewId());
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        TabLayout.LayoutParams layoutParams = new TabLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        tabLayout.setLayoutParams(layoutParams);
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.addView(tabLayout);

        //customPagerAdapter = new CustomPagerAdapter(getActivity().getSupportFragmentManager());
        customPagerAdapter = new CustomPagerAdapter(getChildFragmentManager());
        viewPager = new ViewPager(getActivity());
        viewPager.setAdapter(customPagerAdapter);
        viewPager.setId(View.generateViewId());
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        viewPager.setLayoutParams(layoutParams2);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        viewGroup.addView(viewPager);

        if (fragmentsEnfants != null) {
            for (int i = 0; i < fragmentsEnfants.size(); i++) {
                FragmentTab fragmentTab = (FragmentTab) fragmentsEnfants.get(i);

                // Pour skipper les tabs de debug options et autres a ignorer...
                if (!fragmentTab.fragmentsEnfants.isEmpty())
                    preparerConteneurFragmentEnfant(fragmentTab, i);
            }
        }
    }

    // https://stackoverflow.com/questions/33246307/how-do-i-get-the-view-of-a-tab-in-a-tablayout
    protected ViewGroup getTabView(int index) {
        return (ViewGroup) ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(index);
    }

    // Preparer un conteneur pour chaque fragment dyn. de facon aussi dyn.
    @SuppressLint("NewApi")
    public void preparerConteneurFragmentEnfant(FragmentTab fragmentTabEnfant, int index) {

        TabLayout.Tab newTab = tabLayout.newTab();
        newTab.setText(fragmentTabEnfant.attributs.get("name"));
        tabLayout.addTab(newTab);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class CustomPagerAdapter extends FragmentStatePagerAdapter {// FragmentPagerAdapter { //FragmentStatePagerAdapter {
        public CustomPagerAdapter(FragmentManager fm) { super(fm); }

        @SuppressLint("NewApi")
        @Override
        public Fragment getItem(int position) {
            return fragmentsEnfants.get(position);
        }

        @Override
        public int getCount() {
            return tabLayout.getTabCount();
        }
    }
}
