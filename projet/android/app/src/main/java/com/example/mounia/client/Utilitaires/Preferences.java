package com.example.mounia.client.Utilitaires;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/*
*   classe preferences
*   stocke les configurations persistentes entre deux sesions
 */
public class Preferences {


    // l'objet utilisé pour implémenter, agit comme un dictionnaire (map)
    public static SharedPreferences preferences = null;

    // strings utilisées pour accéder aux champs
    public static final String IP = "adresseIP";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String CAN_PORT = "CANPort";
    public static final String REMEMBER_ME = "rememberMe";
    public static final String REMEMBER_PASSWORD = "rememberPassword";


    public static void init(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void  set(String name, String content){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, content);
        editor.commit();
    }

    public static String  get(String name){
        return preferences.getString(name,null);
    }
}
