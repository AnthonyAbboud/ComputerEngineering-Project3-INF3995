package com.example.mounia.client.Utilitaires;

import android.view.View;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by passenger on 4/12/2018.
 */

public class ViewConserver {

    // Juste pour pointer vers les vues dynamiques pour que le garbage collector ne
    // puisse les detruire

    public static ConcurrentHashMap<Integer, View> vuesDynamiques = new ConcurrentHashMap<>();

}
