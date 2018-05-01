package com.example.mounia.client.Fragments.Widgets;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.mounia.client.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentGridContainer extends FragmentWidget {


    public FragmentGridContainer() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_grid_container, container, false);
    }

    @Override
    public void onStart() {

        super.onStart();

        for (FragmentWidget fragmentDyn : obtenirFragmentsEnfants()) {
            preparerConteneurFragmentEnfant();
        }

        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++) {
            //transaction.add(FactoryFragmentWidget.getFragmentContainerId(this), enfant);
            transaction.add(fragmentContainerIDs.get(i), obtenirFragmentsEnfants().get(i));
        }
        transaction.commit();
    }

    // Preparer un conteneur pour chaque fragment dyn. de facon aussi dyn.
    public void preparerConteneurFragmentEnfant() {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            linearLayout.setId(View.generateViewId());
            fragmentContainerIDs.add(linearLayout.getId());
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0);
        layoutParams.weight = 1;
        linearLayout.setLayoutParams(layoutParams);
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.addView(linearLayout);
    }

}
