package com.example.mounia.client.CommunicationClientServer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mounianordine on 18-04-08.
 */

public class ModuleType {


    public final int ADM = 0;
    public final int ADIRM = 3;
    public final int ADLM = 4;
    public final int APUM = 6;
    public final int NUC = 7;
    public final int GS = 7;
    public final int MCD = 15;
    public final int AGRUM = 16;
    public final int ADRMSAT = 17;
    public final int ATM_MASTER = 18;
    public final int ATM_SLAVE = 19;
    public final int UNKNOWN_MODULE = 0x1E;
    public final int ALL_MODULES = 0x1F;


    public static Map<Integer,String> map ;

    private ModuleType(){

        map = new HashMap<>();
        map.put(ADM,"ADM");
        map.put(ADIRM, "ADIRM");
        map.put(ADLM, "ADLM");
        map.put(APUM, "APUM");
        map.put(NUC, "NUC");
        map.put(GS, "GS");
        map.put(MCD, "MCD");
        map.put(AGRUM, "AGRUM");
        map.put(ADRMSAT, "ADRMSAT");
        map.put(ATM_MASTER, "ATM_MASTER");
        map.put(ATM_SLAVE, "ATM_SLAVE");
        map.put(UNKNOWN_MODULE, "UNKNOWN_MODULE");
        map.put(ALL_MODULES, "ALL_MODULES");
    }

    private static ModuleType instance = null;

    public static ModuleType instance()
    {
        if(instance == null)
            instance = new ModuleType();
        return instance;
    }

    public String toString (int index)
    {
        return map.get(index);
    }

//    public static void main (String[] args)  {
//
//        ModuleType moduleType = new ModuleType();
//
//        System.out.println(map.get(0x1F));
//
//    }

}
