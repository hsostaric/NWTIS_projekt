/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Hrvoje-PC
 */
public class GlavnaDretva extends Thread {

    private BP_Konfiguracija bpk;
    public volatile static boolean radi = true;
    private ServerSocket serverSocket;
    private int port = 0;
    private int cekaci = 0;
    private ObradaZahtjeva zahtjevKlijenta;
    private Socket veza;
    public volatile static boolean pauza = false;

    public GlavnaDretva(BP_Konfiguracija bpk) {
        this.bpk = bpk;
    }

    @Override
    public void interrupt() {
        radi = false;
        super.interrupt();
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if(veza!=null){
                veza.close();
            }
        } catch (IOException ex) {
            System.out.println("Greska kod gasenja: " + ex);
        }
    }

    @Override
    public void run() {
        System.out.println("Server pokrenut.");
        try {
            serverSocket = new ServerSocket(port, cekaci);
            while (radi == true) {
                System.out.println("Cekam vezu.");
                veza = serverSocket.accept();
                if (radi == true) {
                    zahtjevKlijenta = new ObradaZahtjeva(veza, bpk);
                    zahtjevKlijenta.start();
                    zahtjevKlijenta.join();
                } else {
                    OutputStreamWriter streamWriter = new OutputStreamWriter(veza.getOutputStream(),
                            "UTF-8");
                    streamWriter.write("OK 10;");
                    streamWriter.flush();
                    veza.shutdownOutput();
                    veza.close();
                }
            }
        } catch (IOException ex) {
            System.out.println("Greška: " + ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(GlavnaDretva.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                serverSocket.close();
            } catch (IOException ex) {
                System.out.println("Greska: " + ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        port = Integer.parseInt(bpk
                .getKonfig()
                .dajPostavku("serverSocket.port"));
        cekaci = Integer.parseInt(bpk
                .getKonfig()
                .dajPostavku("serverSocket.cekaci"));
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

}
