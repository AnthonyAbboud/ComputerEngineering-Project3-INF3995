package com.example.mounia.client.Factory;

import com.example.mounia.client.Fragments.FragmentCan;
import com.example.mounia.client.Fragments.Widgets.FragmentDataDisplayer;
import com.example.mounia.client.Fragments.Widgets.FragmentDisplayLogWidget;
import com.example.mounia.client.Fragments.Widgets.FragmentDualHWidget;
import com.example.mounia.client.Fragments.Widgets.FragmentDualVWidget;
import com.example.mounia.client.Fragments.Widgets.FragmentFindMe;
import com.example.mounia.client.Fragments.Widgets.FragmentGrid;
import com.example.mounia.client.Fragments.Widgets.FragmentGridContainer;
import com.example.mounia.client.Fragments.Widgets.FragmentMap;
import com.example.mounia.client.Fragments.Widgets.FragmentModuleStatus;
import com.example.mounia.client.Fragments.Widgets.FragmentPlots;
import com.example.mounia.client.Fragments.Widgets.FragmentRocket;
import com.example.mounia.client.Fragments.Widgets.FragmentTab;
import com.example.mounia.client.Fragments.Widgets.FragmentTabContainer;
import com.example.mounia.client.Fragments.Widgets.FragmentWidget;
import com.example.mounia.client.Fragments.WidgetsToIgnore.FragmentButtonArray;
import com.example.mounia.client.Fragments.WidgetsToIgnore.FragmentCustomCanSender;
import com.example.mounia.client.Fragments.WidgetsToIgnore.FragmentDebugOptions;
import com.example.mounia.client.Fragments.WidgetsToIgnore.FragmentRadioStatus;

/**
 * Created by passenger on 3/29/2018.
 */

public class CreateurWidget {
    private CreateurWidget() {}

    public static final String ROCKET              = "rocket";
    public static final String GRID_CONTAINER      = "gridcontainer";
    public static final String GRID                = "grid";
    public static final String TAB_CONTAINER       = "tabcontainer";
    public static final String TAB                 = "tab";
    public static final String DUAL_V_WIDGET       = "dualvwidget";
    public static final String DUAL_H_WIDGET       = "dualhwidget";
    public static final String DEBUG_OPTIONS       = "debugoptions";
    public static final String BUTTON_ARRAY        = "buttonarray";
    public static final String MODULESTATUS        = "modulestatus";
    public static final String DISPLAY_LOG_WIDGET  = "displaylogwidget";
    public static final String PLOT                = "plot";
    public static final String MAP                 = "map";
    public static final String FIND_ME             = "findme";
    public static final String RADIO_STATUS        = "radiostatus";
    public static final String CUSTOM_CAN_SENDER   = "customcansender";
    public static final String DATA_DISPLAYER      = "datadisplayer";
    public static final String CAN                 = "can";


    private static FragmentTabContainer singletonTabContainer = null;
    public static FragmentTabContainer getSingletonTabContainer() {
        if (singletonTabContainer == null)
            singletonTabContainer = new FragmentTabContainer();
        return singletonTabContainer;
    }

    public static FragmentWidget newInstance(String tagXML) {
        String lowerTagXML = tagXML.toLowerCase();
        FragmentWidget fragment = null;
        switch (lowerTagXML) {
            case ROCKET:
                fragment = new FragmentRocket();
                break;
            case GRID_CONTAINER:
                fragment = new FragmentGridContainer();
                break;
            case GRID:
                fragment = new FragmentGrid();
                break;
            case TAB_CONTAINER:
                fragment = new FragmentTabContainer();
                //fragment = getSingletonTabContainer();
                break;
            case TAB:
                fragment = new FragmentTab();
                break;
            case RADIO_STATUS:
                fragment = new FragmentRadioStatus();
                break;
            case CUSTOM_CAN_SENDER:
                fragment = new FragmentCustomCanSender();
                break;
            case DEBUG_OPTIONS:
                fragment = new FragmentDebugOptions();
                break;
            case BUTTON_ARRAY:
                fragment = new FragmentButtonArray();
                break;
            case DUAL_H_WIDGET:
                fragment = new FragmentDualHWidget();
                break;
            case DUAL_V_WIDGET:
                fragment = new FragmentDualVWidget();
                break;
            case DATA_DISPLAYER:
                fragment = new FragmentDataDisplayer();
                break;
            case MODULESTATUS :
                fragment = new FragmentModuleStatus();
                break;
            case DISPLAY_LOG_WIDGET:
                fragment = new FragmentDisplayLogWidget();
                break;
            case MAP:
                fragment = new FragmentMap();
                break;
            case PLOT:
                fragment = new FragmentPlots();
                break;
            case FIND_ME:
                fragment = new FragmentFindMe();
                break;
            case CAN:
                fragment = new FragmentCan();
                break;
        }

        return fragment;
    }

    // https://developer.android.com/about/versions/android-4.2.html
    public static void etablirLiensParente(FragmentWidget enfant, FragmentWidget parent) {
        if (parent != null && enfant != null) {
            parent.obtenirFragmentsEnfants().add(enfant);
            enfant.assignerFragmentParent(parent);
        }
    }

    public static FragmentWidget newInstance(String tagXML, FragmentWidget parent) {
        FragmentWidget enfant = newInstance(tagXML);
        etablirLiensParente(enfant, parent);
        return enfant;
    }
}
