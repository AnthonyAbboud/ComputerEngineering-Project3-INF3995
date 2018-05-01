package com.example.mounia.client.Fragments.Widgets;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.mounia.client.R;

public class FragmentDualHWidget extends FragmentWidget {

    public FragmentDualHWidget() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_dual_hwidget, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        for (int i = 0; i < fragmentsEnfants.size(); i++)
            preparerConteneurFragmentEnfant();

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++)
            transaction.add(fragmentContainerIDs.get(i), obtenirFragmentsEnfants().get(i));
        transaction.commit();
    }

    // Preparer un conteneur pour chaque fragment dyn. de facon aussi dyn.
    @SuppressLint("NewApi")
    public void preparerConteneurFragmentEnfant() {

//        ViewGroup viewGroup = (ViewGroup) getView();
//        ScrollView scrollView = new ScrollView(getContext());
//        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//        scrollView.setLayoutParams(layoutParams1);
//        viewGroup.addView(scrollView);
//
//
//        LinearLayout linearLayout = new LinearLayout(getActivity());
//        linearLayout.setOrientation(LinearLayout.VERTICAL);
//        linearLayout.setId(View.generateViewId());
//        fragmentContainerIDs.add(linearLayout.getId());
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                0,
//                LinearLayout.LayoutParams.MATCH_PARENT,
//                1);
//        linearLayout.setLayoutParams(layoutParams);
//        viewGroup.addView(linearLayout);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setId(View.generateViewId());
        fragmentContainerIDs.add(linearLayout.getId());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1);
        linearLayout.setLayoutParams(layoutParams);
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.addView(linearLayout);
    }
}
