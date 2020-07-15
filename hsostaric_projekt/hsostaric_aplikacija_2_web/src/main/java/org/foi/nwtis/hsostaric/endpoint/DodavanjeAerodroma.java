/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.foi.nwtis.hsostaric.ejb.sb.DohvacanjePodataka;

@ServerEndpoint("/dodavanjeAerodroma")
@LocalBean
public class DodavanjeAerodroma {

    @Inject
    DohvacanjePodataka upravljac;
    private static List<Session> korisnici = new ArrayList<>();

    @OnOpen
    public void openConnection(Session session) {
        korisnici.add(session);
        System.out.println("Otvorena veza na dodavanju aerodroma");

    }

    @OnMessage
    public void onMessage(String poruka) {
        System.out.println("Stigla poruka na server za dodavanje aerodroma : " + poruka);
        Session korisnik = null;
        String nazad = "";
        Boolean dodan = upravljac.dodajNoviAerodromUMoje(poruka);
        for (Session s : korisnici) {
            if (s.isOpen()) {
                try {
                    if(dodan==true){
                        nazad="OK;";
                    }else{
                        nazad="ERR;";
                    }
                    s.getBasicRemote().sendText(nazad);
                    korisnik = s;
                } catch (IOException ex) {
                    System.out.println("Greška: " + ex);
                }
            }
        }
        if (korisnik != null) {
            closedConnection(korisnik);
        }
    }

    @OnClose
    public void closedConnection(Session session) {
        korisnici.remove(session);
        System.out.println("Zatvorena veza.");
    }

    @OnError
    public void error(Session session, Throwable t) {
        korisnici.remove(session);
        System.out.println("Zatvorena veza zbog greške.");
    }
}
