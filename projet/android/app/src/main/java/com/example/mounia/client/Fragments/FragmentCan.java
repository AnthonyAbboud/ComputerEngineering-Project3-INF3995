package com.example.mounia.client.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mounia.client.CommunicationClientServer.CANMessage;
import com.example.mounia.client.CommunicationClientServer.CustomUpdate;
import com.example.mounia.client.CommunicationClientServer.ModuleType;
import com.example.mounia.client.Fragments.Widgets.FragmentWidget;
import com.example.mounia.client.R;
import com.example.mounia.client.Utilitaires.Chronometre;

import java.text.DecimalFormat;

/**
 * Created by mounianordine on 18-03-08.
 */

public class FragmentCan extends FragmentWidget {

    /*
        5. Le code doit être robuste aux inconsistances
            (a) CANSID inexistant
            (b) Mauvais type de données fourni dans DATATYPE1 et DATATYPE2
        6. Ne pas afficher les données corrompues
     */

    // Une constante, au lieu d'ecrire le texte a la main.
    public static String ATTR_KEY_ID = "id";

    // Obligatoire. Représente l’étiquette et sera affiché dans le UI près de la donnée correspondante
    public static String ATTR_KEY_NAME = "name";

    // Facultatif. Détermine si DATA1 ou DATA2 est affiché. Un symbole d’unité de
    // mesure peut être ajouté et sera concaténé à l’affichage de la donnée.
    // Par défaut, DATA1 est affiché
    public static String ATTR_KEY_DISPLAY = "display";

    // Facultatif. Détermine le seuil inférieur d’acceptabilité de la donnée. Si le seuil
    // est dépassé, la donnée doit être affichée en rouge. Autrement, la donnée doit
    // être affichée en vert. Si aucune donnée n’a encore été reçue, la couleur du thème
    // doit être adoptée.
    public static String ATTR_KEY_MIN_ACCEPTABLE = "minacceptable";

    // Facultatif. Détermine le seuil supérieur d’acceptabilité de la donnée. Si le seuil
    // est dépassé, la donnée doit être affichée en rouge. Autrement, la donnée doit
    // être affichée en vert. Si aucune donnée n’a encore été reçue, la couleur du thème
    // doit être adoptée.
    public static String ATTR_KEY_MAX_ACCEPTABLE = "maxacceptable";

    // Facultatif. Détermine le nombre de chiffres significatifs à afficher. Par défaut,
    // tous les chiffres significatifs sont affichés.
    public static String ATTR_KEY_CHIFFRES_SIGN = "chiffressign";

    // Facultatif. Si la donnée entrante provient de la source spécifiée, le champ est
    // mis à jour. Sinon, aucune action n’est prise. Par défaut, le champ est mis à jour.
    public static String ATTR_KEY_SPECIFIC_SOURCE = "specificsource";

    // Facultatif. Si la donnée entrante provient d’une source avec le numéro de série
    // spécifié, le champ est mis à jour. Sinon, aucune action n’est prise. Par défaut,
    // le champ est mis à jour.
    public static String ATTR_KEY_SERIAL_NB = "serialnb";

    // Facultatif. Appelle la fonction spécifiée par la valeur du champ. Cette fonction
    // retourne l’affichage sur mesure. Les implémentations des fonctions doivent respecter
    // exactement celles en Python pour la version PC déjà implémentée par
    // Oronos (CustomUpdate.py).
    public static String ATTR_KEY_CUSTOM_UPDATE = "customupdate";
    public static String ATTR_KEY_CUSTOM_UPDATE_PARAM = "customupdateparam";
    public static String ATTR_KEY_CUSTOM_ACCEPTABLE = "customacceptable";

    // Facultatif. Restreint le rafraîchissement des données pour des raisons de performance.
    // Par défaut, toutes les données sont affichées.
    public static String ATTR_KEY_UPDATE_EACH = "updateeach";

    // Un chronometre pour l'attribut update each
    private Chronometre chronometre = new Chronometre();

    public void relancerChronometre() {
        chronometre.lancer();
    }

    public boolean tempsDeMettreAJour() {
        String strUpdateEach = attributs.get(FragmentCan.ATTR_KEY_UPDATE_EACH);
        if (strUpdateEach != null) {
            int updateEach = Integer.parseInt(strUpdateEach);
            if (chronometre.secondesEcoulees() < updateEach) return false;
        }

        return true;
    }

    public boolean specificSourceDifferent(String canMessageSrcID) {
        String specificSource = attributs.get(ATTR_KEY_SPECIFIC_SOURCE);
        if (specificSource != null) {
            final String ALL_MOD = ModuleType.instance().toString(ModuleType.instance().ALL_MODULES);
            if (!specificSource.equals(canMessageSrcID) && !specificSource.equals(ALL_MOD)) return true;
        }

        return false;
    }

    public boolean serialNbDifferent(int canMessageSerialNb) {
        String strSerialNb = attributs.get(ATTR_KEY_SERIAL_NB);
        if (strSerialNb != null) {
            int serialNb = Integer.parseInt(strSerialNb);
            if (serialNb != canMessageSerialNb) return true;
        }

        return false;
    }

    public double recupererDonnee(CANMessage canMessage) {
        double donnee = 0.;
        String strDisplay = attributs.get(ATTR_KEY_DISPLAY);
        if (strDisplay != null) {
            try {
                donnee = (double) canMessage.data1;
            } catch (Exception e) {
                try {
                    donnee = (long) canMessage.data1;
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            if (strDisplay.contains("__DATA2__")) {
                try {
                    donnee = (double) canMessage.data2;
                } catch (Exception e3) {
                    try {
                        donnee = (long) canMessage.data2;
                    } catch (Exception e4) {
                        e4.printStackTrace();
                    }
                }
            }
        }

        return donnee;
    }

    public String recupererUnite(CANMessage canMessage) {
        String unite = "";
        String compare = "__DATA1__";
        String strDisplay = attributs.get(ATTR_KEY_DISPLAY);
        if (strDisplay != null && strDisplay.length() > compare.length())
            unite = strDisplay.substring(compare.length(), strDisplay.length());
        return unite;
    }

    public double recupererChiffresSignificatifs(double donnee) {
        String strChiffresSign = attributs.get(ATTR_KEY_CHIFFRES_SIGN);
        if (strChiffresSign != null) {
            int chiffresSign = Integer.parseInt(strChiffresSign);
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMaximumFractionDigits(chiffresSign);
            String strDonnee = decimalFormat.format(donnee);
            double result = 0.;
            try {
                result = Double.parseDouble(strDonnee);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        return donnee;
    }

    public String recupererCustomUpdate(CANMessage canMessage) {
        String functionName = attributs.get(ATTR_KEY_CUSTOM_UPDATE);
        if (functionName != null) {
            if (functionName.equals("oneWire") && attributs.get(ATTR_KEY_CUSTOM_UPDATE_PARAM) != null) {
                String addr = attributs.get(ATTR_KEY_CUSTOM_UPDATE_PARAM);
                if (addr.contains("0x")) addr = addr.replace("0x", "");
                return CustomUpdate.update(functionName, canMessage, addr);
            }

            return CustomUpdate.update(functionName, canMessage, null);
        }
        return null;
    }

    public int getBackGroundColor(double donnee) {
        String strMinAcceptable = attributs.get(ATTR_KEY_MIN_ACCEPTABLE);
        if (strMinAcceptable != null) {
            double minAcceptable = Double.parseDouble(strMinAcceptable);
            if (donnee < minAcceptable) return Color.RED;
        }

        String strMaxAcceptable = attributs.get(ATTR_KEY_MAX_ACCEPTABLE);
        if (strMaxAcceptable != null) {
            double maxAcceptable = Double.parseDouble(strMaxAcceptable);
            if (donnee > maxAcceptable) return Color.RED;
        }

        return Color.GREEN;
    }

    public FragmentCan() { chronometre.lancer(); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_can, container, false);
    }
}



