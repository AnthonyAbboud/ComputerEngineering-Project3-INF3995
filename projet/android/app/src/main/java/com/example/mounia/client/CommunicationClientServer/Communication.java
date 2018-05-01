package com.example.mounia.client.CommunicationClientServer;


import android.util.Log;

import com.example.mounia.client.Observateurs.Observable;
import com.example.mounia.client.Observateurs.Observateur;
import com.example.mounia.client.Utilitaires.Configurations;
import com.example.mounia.client.Utilitaires.Preferences;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by Luc on 2018-03-28.
 * classe en charge de la majorité de la communication avec le serveur
 * i.e. envoi et reception des messages CAN, connection et deconnection
 */

public class Communication implements Observable, Runnable {

    // sommes nous connectés au serveur ?
    public static boolean connected = false;

    // a-t-on fini de communiquer avec le serveur ?
    public static boolean done = true;

    // pour la communication via UDP
    private InetAddress serverAddr;
    private DatagramSocket socket;

    // patron singleton, l'instance et un thread sont créés dans AffichageActivity (fonction onCreate)
    public static Communication inst;

    public Communication() {
        super();

        String ip = Preferences.get("adresseIP");
        Log.d("Communication", "adresse ip : " + ip + " CANport : " + String.valueOf(Configurations.CANport));
        try {
            serverAddr = InetAddress.getByName(ip);
            socket = new DatagramSocket();
            socket.setSoTimeout(Configurations.CANtimeout);
        }  catch(Exception e){
            Log.e("Communication", "Socket error " + e.getMessage());
        }
    }


    // envoie un paquet vers le serveur
    public void send(byte[] buffer)  {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddr, Configurations.CANport);
            socket.send(packet);
        }
        catch(Exception e){
            Log.e("Communication", "error send " + e.getMessage());
        }
    }

    // tente de recevoir un paquet, renvoie faux si raté
    // le paquet est mis dans le buffer fourni
    public boolean receive (byte[] buffer) {
        try {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddr, Configurations.CANport);
            socket.receive(packet);
            return true;
        }
        catch(SocketTimeoutException e){
            Log.i("Communication", "missed packet " + e.getMessage());
        }
        catch(Exception e){
            Log.e("Communication", "error receive " + e.getMessage());
        }
        return false;
    }

    // le numéro de séquence du dernier paquet reçu
    public static long sequence = 0;

    // le contenu du dernier paquet reçu, concurrent pour éviter des problèmes de threading
    // Java fournit des concurrentMap, mais pas de ConcurrentList/Array, on utilise donc une adaptation un peu bancale
    // en soit c'est la fonction avertir qui devrait renvoyer une liste
    public static ConcurrentMap<Integer,CANMessage> data = new ConcurrentHashMap<>(); //fuck java


    // implementation du patron observable
    List<Observateur> observateurs = new ArrayList();
    @Override
    public void ajouter(Observateur observateur){
        observateurs.add(observateur);
    }
    @Override
    public  void enlever(Observateur observateur){
        observateurs.remove(observateur);
    }
    @Override
    public void avertir(){
        for (Observateur obs : observateurs){
            obs.mettreAJour();
        }
    }

    // boule d'envoi et reception principale
    @Override
    public void run() {

            CANEnums.init();
            //long sequence = 0;
            byte[] packet = new byte[Configurations.PACKET_SIZE * 32];

            done = false;
            while(!done){

                byte[] sendPacket = (Long.toString(sequence) + "\n" + Integer.toString(Configurations.PACKET_SIZE)).getBytes();
                send(sendPacket);

                if(receive(packet)) {

                    DecodeCAN.Result received = DecodeCAN.decodeEntirePacket(packet);

                    data.clear();
                    for(int i =0; i< received.data.length; i++)
                        data.put(i,received.data[i]);
                        //data.add(received.data[i]);


                    sequence = received.sequenceNumber;
                    //Log.i("Communication", String.valueOf(sequence));

                    if (received.ok()) {
                        if (received.data.length != 0) {
                            avertir();
                            Log.i("Communication", "sequence : " + Long.toString(received.sequenceNumber) + " size : " + String.valueOf(received.data.length));
                        }
                        //else System.out.print("I");
                    } else {
                        Log.e("Communication", "received : " + Long.toString(received.sequenceNumber) + " " + String.valueOf(received.ok()) + " " + received.status /*+ " -> " + new String(packet, "UTF-8")*/);
                    }
                }
            }
    }
    //public static  Response laReponse = null;



    public static boolean login(String username, String password){
        String ip = Preferences.get(Preferences.IP);
        Configurations.serverHttpUrl = "http://" + ip + ":" + String.valueOf(Configurations.connectPort);
        String urlPost = Configurations.serverHttpUrl + "/users/login";


        Log.i("Connection", urlPost);

        //creer le client pour les requettes Rest
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setConnectTimeout(Configurations.connectTimeout, TimeUnit.MILLISECONDS);


        //creation of the request body
        MediaType jason = MediaType.parse("application/json");
        RequestBody body = new FormEncodingBuilder()
                .add("user", "{\"username\": \"" + username + "\", \"password\": \"" + password + "\"}")
                .build();

        //create a request to the server adress
        Request request = new Request.Builder().header("user", "user").url(urlPost).post(body).build();

        boolean result = false;
        final Semaphore done = new Semaphore(0);
        Callback call = new Callback() {

            public boolean successful = false;
            public boolean getSuccess(){ return successful; }

            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("Connection", "failed " + e.getMessage());
                done.release();
            }

            @Override
            public void onResponse(Response response) {
                successful = response.isSuccessful();
                //laReponse = response;
                if(!successful)
                    Log.i("Connection"," was rejected");
                done.release();
            }
        };


        okHttpClient.newCall(request).enqueue(call);


        try{
            done.acquire();
            result = (boolean) call.getClass().getMethod("getSuccess").invoke(call);
        } catch (Exception e){}

        connected = result;

        return result;
    }



    public static boolean logout(){
        if(connected) {
            String username = Configurations.username;
            String ip = Preferences.get(Preferences.IP);
            String urlPost = Configurations.serverHttpUrl + "/users/logout";


            Log.i("Deconnection", urlPost);

            //creer le client pour les requettes Rest
            OkHttpClient okHttpClient = new OkHttpClient();
            okHttpClient.setConnectTimeout(Configurations.connectTimeout, TimeUnit.MILLISECONDS);


            //creation of the request body
            MediaType jason = MediaType.parse("application/json");
            RequestBody body = new FormEncodingBuilder()
                    .add("user", "{\"username\": \"" + username + "\"}")
                    .build();

            //create a request to the server adress
            Request request = new Request.Builder().header("user", "user").url(urlPost).post(body).build();

            boolean result = false;
            final Semaphore done = new Semaphore(0);
            Callback call = new Callback() {

                public boolean successful = false;

                public boolean getSuccess() {
                    return successful;
                }

                @Override
                public void onFailure(Request request, IOException e) {
                    Log.e("Deconnection", "failed " + e.getMessage());
                    done.release();
                }

                @Override
                public void onResponse(Response response) {
                    successful = response.isSuccessful();
                    if (!successful)
                        Log.i("Deconnection", " was rejected");
                    done.release();
                }
            };
            okHttpClient.newCall(request).enqueue(call);

            try {
                done.acquire();
                result = (boolean) call.getClass().getMethod("getSuccess").invoke(call);
            } catch (Exception e) {
                Log.i("Deconnection", "error retrieving result: " + e.getMessage());
            }

            Log.i("Deconnection", String.valueOf(result));

            return result;
        }
        else{
            connected =false;
            return true;
        }
    }

}
