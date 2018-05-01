package com.example.mounia.tp3;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.plus.PlusOneButton;

/**
 * Created by mounianordine on 18-01-25.
 */

public class FragmentText extends Fragment {

    public FragmentText()
    {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        return view;
    }



}
