package com.example.mounia.client.Utilitaires;

import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by passenger on 2/22/2018.
 */

public class AfficheurTempsReel implements Runnable
{
    private static Socket socket;
    private static final int SERVER_PORT = 5010;
    private static final String SERVER_IP = "192.168.1.106";
    private static PrintWriter writer = null;

    public AfficheurTempsReel()
    {
        arretEstDemande = false;
    }

    private volatile boolean arretEstDemande;

    private synchronized boolean arretEstDemande()
    {
        return this.arretEstDemande;
    }

    public synchronized void demanderArret()
    {
        this.arretEstDemande = true;
    }

    public static void sendMessage(String message) throws IOException {
        if (writer == null) writer = new PrintWriter(socket.getOutputStream());
        writer.println(message.toString());
        writer.flush();
    }

    @Override
    public void run() {

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            sendMessage("Hi from Android!");
            socket.close();
        } catch (IOException e) {
            Log.e("Socket error", e.getMessage());
        }

        // Boucle principale
        while (!arretEstDemande())
        {
            // TODO : Lire ce que l'ecrivain envoie
            // ...

            // Faire dormir un peu le thread qui executera ce runnable
//            try {
//                TimeUnit.MILLISECONDS.sleep(33);
//            } catch (InterruptedException e) {
//                Log.e("Socket error", e.getMessage());
//            }
        }


    }
}
