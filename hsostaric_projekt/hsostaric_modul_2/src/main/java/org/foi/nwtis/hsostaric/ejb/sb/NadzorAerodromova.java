/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.LocalBean;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Klasa koja je krajnja točka na poslužiteljevoj strani za WebSocket
 * komunikaciju.
 */
@ServerEndpoint("/slanjeAerodroma")
@LocalBean
public class NadzorAerodromova {
    @Inject
    DohvacanjePodataka dohvacanjePodataka;

    private static List<Session> korisnici = new ArrayList<>();

    public NadzorAerodromova() {
    }

    /**
     * Metoda koja registrira spajanje korisnika na vezu i dodaje ga u kolekciju
     * aktivnih korisnika.
     *
     * @param session sjednica korisnika
     */
    @OnOpen
    public void openConnection(Session session) {
        korisnici.add(session);
        try {
            session.getBasicRemote().sendText(dohvacanjePodataka.dajZadnjeAzuriranje());
        } catch (IOException ex) {
            System.out.println("Greska kod otvaranja konekcije sa slanjem aerodroma."+ex);
        }
        System.out.println("Otvorena veza");

    }

    /**
     * Metoda koja se poziva nakon zatvaranja sjednice.
     *
     * @param session sjednica za zatvaranje
     */
    @OnClose
    public void closedConnection(Session session) {
        korisnici.remove(session);
        System.out.println("Zatvorena veza.");
    }
/**
 *Metoda koja zatvara vezu prema korisniku ukoliko je došlo do greške.
     * @param session sjednica korisnika
     * @param t greška
 */
    @OnError
    public void error(Session session, Throwable t) {
        korisnici.remove(session);
        System.out.println("Zatvorena veza zbog greške.");
    }
    
    public static void sendToAllusers(String message){
         for (Session s : korisnici) {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText(message);
                } catch (IOException ex) {
                    System.out.println("Greška: " + ex);
                }
            }
        }
    }

}
