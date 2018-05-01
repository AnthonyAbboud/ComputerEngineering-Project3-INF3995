package com.example.mounia.tp3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentURL extends Fragment {

    public FragmentURL() {
    }
/*
    public void AfficherUrl(View view) {
        TextView textView = (TextView) getView().findViewById(R.id.link);
        textView.setVisibility(View.VISIBLE);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_url, container, false);
        return view;
    }



}
