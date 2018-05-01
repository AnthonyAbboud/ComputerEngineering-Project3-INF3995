package com.example.mounia.client.Fragments.Widgets;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.example.mounia.client.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRocket extends FragmentWidget {

    // Exemple : <Rocket name="Polaris" id="10">. Conteneur principal du XML.
    // L’attribut name contient le nom textuel de la fusée. L’attribut id contient l’identifiant
    // unique de la fusée. Le titre de l’applicatoion doit contenir ces informations.

    public FragmentRocket() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_rocket, container, false);
    }

    private Toolbar toolbar = null;
    public void setToolbar(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Assigner le nom et l'id de la rocket au titre de l'application >> ActivityAffichage
        // https://stackoverflow.com/questions/2198410/how-to-change-title-of-activity-in-android
        // https://stackoverflow.com/questions/3438276/how-to-change-the-text-on-the-action-bar/8852749
        if (this.attributs.containsKey("name") && this.attributs.containsKey("id"))
            this.toolbar.setTitle(this.attributs.get("name") + " " + this.attributs.get("id"));
            //getActivity().setTitle(this.attributs.get("name") + " " + this.attributs.get("id"));

        // Preparer un conteneur dynamique pour chaque fragment dynamique enfant
        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++)
            preparerConteneurFragmentEnfant();

        // Ajouter les fragments enfants dynamiquement
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        for (int i = 0; i < obtenirFragmentsEnfants().size(); i++)
            transaction.add(fragmentContainerIDs.get(i), obtenirFragmentsEnfants().get(i));
        transaction.commit();
    }

    // Preparer un conteneur pour chaque fragment dyn. enfant de facon dyn.
    @SuppressLint("NewApi")
    public void preparerConteneurFragmentEnfant() {

        ViewGroup viewGroup = (ViewGroup) getView();
        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        scrollView.setLayoutParams(layoutParams1);
        viewGroup.addView(scrollView);

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setId(View.generateViewId());
        fragmentContainerIDs.add(linearLayout.getId());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1);
        linearLayout.setLayoutParams(layoutParams);
        viewGroup.addView(linearLayout);
    }
}
