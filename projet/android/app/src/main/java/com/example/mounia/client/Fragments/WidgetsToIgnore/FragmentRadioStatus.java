package com.example.mounia.client.Fragments.WidgetsToIgnore;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mounia.client.Fragments.Widgets.FragmentWidget;
import com.example.mounia.client.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentRadioStatus extends FragmentWidget {


    public FragmentRadioStatus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_radio_status, container, false);
    }

}
