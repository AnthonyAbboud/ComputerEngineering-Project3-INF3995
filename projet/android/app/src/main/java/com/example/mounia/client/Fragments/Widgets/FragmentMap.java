package com.example.mounia.client.Fragments.Widgets;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.mounia.client.CommunicationClientServer.CANEnums;
import com.example.mounia.client.CommunicationClientServer.CANMessage;
import com.example.mounia.client.CommunicationClientServer.Communication;
import com.example.mounia.client.Observateurs.Observateur;
import com.example.mounia.client.R;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.services.android.telemetry.permissions.PermissionsListener;

import java.util.List;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;


/**
 * Created by mounianordine on 18-03-09.
 */

/*
* Site officiel : https://www.mapbox.com
* Exemple de Map: https://github.com/mapbox/mapbox-android-demo/tree/master/MapboxAndroidWearDemo/src/main/java/com/mapbox/mapboxandroiddemo/examples

* */

// key AIzaSyA0U1j7S6oT9GwRB-WKkNeDtXftQKAliCs

public class FragmentMap extends FragmentWidget implements PermissionsListener, Observateur {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private MapView mapView;

    private static final String TAG = "OfflineMapFragment";
    private boolean isEndNotified;
    private ProgressBar progressBar;
    private OfflineManager offlineManager;
    private OfflineRegion offlineRegionDownloaded;
    private int regionSelected;
    // JSON encoding/decoding
    public static final String JSON_CHARSET = "UTF-8";
    public static final String JSON_FIELD_REGION_NAME = "FIELD_REGION_NAME";
    private MapboxMap map;
    public Marker rocketMarker;
    private Button downloadRegion;
    private Button listRegions;


    public FragmentMap() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Mapbox.getInstance(getActivity(), "pk.eyJ1IjoibW9ub3IiLCJhIjoiY2pleXZtajNtMDNxejJxb2ExZ2dlczB1ZiJ9.f-dBeUfaMbgZtH1GB_80tw");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.layout_map, container, false);


        mapView = (MapView) layout.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                map = mapboxMap;

                // Set map style
                mapboxMap.setStyleUrl(Style.MAPBOX_STREETS);

                // ajouter le markeur
                // TODO: get the position from the json
                addPositionServer("Spareport America", mapboxMap);
                // we download the region


            }
        });

        return layout;
    }

    public double rocketLongitude = -106.91425323486328;
    public double rocketLatitude = 32.9432487487793;
    public double rocketAltitude = 0.0;
    boolean first = true;

    @Override
    public void mettreAJour() {
        if (this.map != null) {
            for (int i = 0; i < Communication.data.size(); i++) {
                CANMessage msg = Communication.data.get(i);

                if (msg.msgID == CANEnums.CANSid.GPS1_LONGITUDE) {
                    Log.i("LONGITUDE: ", String.valueOf(-(double) msg.data1));
                    rocketLongitude = -(double) msg.data1;
                    if (first) {
                        addPositionRocket(new LatLng(rocketLatitude, rocketLongitude, rocketAltitude), this.map);
                        first = false;
                    } else {
                        updatePositionRocket(new LatLng(rocketLatitude, rocketLongitude, rocketAltitude));
                    }
                } else if (msg.msgID == CANEnums.CANSid.GPS1_LATITUDE) {
                    System.out.println("Latitude: " + String.valueOf((double) msg.data1));
                    rocketLatitude = (double) msg.data1;
                    if (first) {
                        addPositionRocket(new LatLng(rocketLatitude, rocketLongitude, rocketAltitude), this.map);
                        first = false;
                    } else {
                        updatePositionRocket(new LatLng(rocketLatitude, rocketLongitude, rocketAltitude));
                    }
                } else if (msg.msgID == CANEnums.CANSid.GPS1_ALT_MSL) {
                    System.out.println("Altitude: " + String.valueOf((double) msg.data1));
                    rocketAltitude = (double) msg.data1;
                    if (first) {
                        addPositionRocket(new LatLng(rocketLatitude, rocketLongitude, rocketAltitude), this.map);
                        first = false;
                    } else {
                        updatePositionRocket(new LatLng(rocketLatitude, rocketLongitude, rocketAltitude));
                    }
                }
            }
        }
    }

    // cette methode permet d'ajouter le markeur selon la position recu par le fichier jason de serveur
    public void addPositionServer(String positionServeur, MapboxMap myMap) {
        LatLng position;
        if (positionServeur.equals("Spareport America")) {
            position = new LatLng(32.9401475, -106.9193209, 152);
            myMap.addMarker(new MarkerOptions().position(position).title("Spareport America"));

            Toast.makeText(getActivity(), "Spare America", Toast.LENGTH_LONG);

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position) // Sets the center of the map to Chicago
                    .zoom(12)                            // Sets the zoom
                    .build();

            myMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else if (positionServeur.equals("Motel 6")) {
            position = new LatLng(32.3417429, -106.7628682, 53);
            myMap.addMarker(new MarkerOptions().position(position).title("Motel 6"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position) // Sets the center of the map to Chicago
                    .zoom(12)                            // Sets the zoom
                    .build();

            myMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else if (positionServeur.equals("Convention Center")) {
            position = new LatLng(32.2799304, -106.7468314, 67);
            myMap.addMarker(new MarkerOptions().position(position).title("Convention Center"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position) // Sets the center of the map to Chicago
                    .zoom(12)                            // Sets the zoom
                    .build();

            myMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else if (positionServeur.equals("St-Pie de Guire")) {
            position = new LatLng(46.0035479, -72.7311097, 422);
            myMap.addMarker(new MarkerOptions().position(position).title("St_Pie de Guide"));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(position) // Sets the center of the map to Chicago
                    .zoom(12)                            // Sets the zoom
                    .build();

            myMap.moveCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

    }

    // ajouter la position de la fusée
    public void addPositionRocket(LatLng positionFromServer, MapboxMap myMap) {
        rocketMarker = new Marker(new MarkerOptions().position(positionFromServer));
        rocketMarker.setPosition(positionFromServer);
        //  myMap.addMarker(new MarkerOptions().position(positionFromServer).title("St_Pie de Guide"));
    }

    // update la position de la fusee
    public void updatePositionRocket(LatLng positionFromServer) {
        rocketMarker.setPosition(positionFromServer);
    }

    public static FragmentMap newInstance(int sectionNumber) {
        FragmentMap fragment = new FragmentMap();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onStart() {

        super.onStart();
        mapView.onStart();

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


    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {

    }
}