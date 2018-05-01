package com.example.mounia.client.Utilitaires;

import android.support.v7.app.AppCompatActivity;

import com.example.mounia.client.R;

/**
 * Created by Reph on 4/5/2018.
 *  Configurations
 *  classe conenant de nombreux paramètres qui affectent le comportement de l'application
 *  susceptibles d'être changés au cours du temps
 */
public class Configurations {

    /*
    // Comunication
    */
    // temps avant d'arrêter d'essayer de se connecter au serveur en ms
    public final static int connectTimeout = 3000;
    // le port de connection au serveur
    public final static int connectPort = 80;
    // utile pour ne pas avoir à se répéter
    public static String serverHttpUrl = "";

    //temps avant d'arrêter d'essayer de recevoir un paquet UDP du serveur
    public final static int CANtimeout = 10;
    // le port de reception des paquets UDP
    public static int CANport = 5000;
    // nombre de CANMessages maximum en un paquet
    public static final int PACKET_SIZE = 2048;

    /*
   // Themes et changement de theme
   //
   // demande de fermer et rouvrir les activités (voir AppBaseActivity, fonction onMenuItemClick)
   // celles-ci doivent appeler setTheme (fonction déclarée ci-dessous)
   */
    public static boolean themeOriginal = true;
    public static void changerTheme()
    {
        themeOriginal = !themeOriginal;
    }
    public static void setTheme(AppCompatActivity activity)
    {
        if(themeOriginal)
            activity.setTheme(R.style.AppTheme); //android.R.style.Theme_Light);
        else
            activity.setTheme(R.style.AppSecondaryTheme); //android.R.style.Theme_Black);
    }

    /*
    // Autre
    */


    private static String modeleFusee = null;
    //liste des pdf a lire
    private static String[] files = null;
    //le nom du pdf
    private static String file = null;
    private static String fuseeXML = null;
    private static String nomMap = null;
    public static String username = "";
    private static String fuseeName = null; //le nom de la fusee


    public static String getModeleFusee() {
        return modeleFusee;
    }

    public static void setModeleFusee(String modeleFusee) {
        Configurations.modeleFusee = modeleFusee;
    }

    public static String[] getFiles() {
        return files;
    }

    public static void setFiles(String[] files) {
        Configurations.files = files;
    }

    //pour avoir le nom du fichier pdf
    public static String getFile() {
        return file;
    }

    //changer le nom du pdf
    public static void setFile(String file) {
        Configurations.file = file;
    }

    public static String getFuseeXML() {
        return fuseeXML;
    }


    public static String getFuseeName() {
        return fuseeXML;
    }

    public static void setFuseeName(String fuseeName) {
        Configurations.fuseeName = fuseeName;
    }

    public static void setFuseeXML(String fuseeName) {
        Configurations.fuseeName = fuseeName;
    }


    public static void setNomMap(String nomMap) {
        Configurations.nomMap = nomMap;
    }

    public static String getNomMap() {
        return Configurations.nomMap;
    }


    public static void setPort(int port) {
        CANport = port;
    }

}
