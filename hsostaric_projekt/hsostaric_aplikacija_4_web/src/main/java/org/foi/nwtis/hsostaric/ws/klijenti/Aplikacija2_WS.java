/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ws.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.hsostaric.server.Aerodrom;
import org.foi.nwtis.hsostaric.server.AvionLeti;
import org.foi.nwtis.hsostaric.server.Projekt;
import org.foi.nwtis.hsostaric.server.Projekt_Service;
import org.foi.nwtis.hsostaric.server.UnknownHostException_Exception;

/**
 *
 * @author Hrvoje-PC
 */
public class Aplikacija2_WS {

    private final String korisnickoIme;
    private final String lozinka;

    public Aplikacija2_WS(String korisnickoIme, String lozinka) {
        this.korisnickoIme = korisnickoIme;
        this.lozinka = lozinka;
    }

    public List<Aerodrom> dajKorisnikoveAerodrome() {
        List<Aerodrom> mojiAerodromi = new ArrayList<>();
        try {
            Projekt_Service service = new Projekt_Service();
            Projekt port = service.getProjektPort();
            mojiAerodromi = port.dohvatiVlastiteAerodrome(korisnickoIme, lozinka);
        } catch (UnknownHostException_Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return mojiAerodromi;
    }

    public List<AvionLeti> vratiLetoveAerodroma(String icao, long od, long _do) {
        List<AvionLeti> lista = new ArrayList<>();
        try {
            Projekt_Service service = new org.foi.nwtis.hsostaric.server.Projekt_Service();
            Projekt port = service.getProjektPort();
            lista = port.dohvatiPopisSvihLetovaAerodroma(korisnickoIme, lozinka, icao, od, _do);
        } catch (UnknownHostException_Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return lista;
    }

    public List<AvionLeti> vratiLetoveAviona(String icao24, long od, long _do) {
        List<AvionLeti> lista = new ArrayList<>();
        try {
            Projekt_Service service = new org.foi.nwtis.hsostaric.server.Projekt_Service();
            Projekt port = service.getProjektPort();
            lista = port.dohvatiSveLetoveAviona(korisnickoIme, lozinka, icao24, od, _do);
        } catch (UnknownHostException_Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return lista;
    }

    public List<Aerodrom> vratiAerodromeURasponu(String icao, int od, int kraj) {
        List<Aerodrom> lista = new ArrayList<>();
        try {
            Projekt_Service service = new org.foi.nwtis.hsostaric.server.Projekt_Service();
            Projekt port = service.getProjektPort();
            lista = port.vratiVlastiteAerodromeURasponu(korisnickoIme, lozinka, icao, od, kraj);
        } catch (UnknownHostException_Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return lista;
    }

    public int vratiUdaljenostIzmedjuAerodroma(String icao1, String icao2) {
        int rezultat = 0;
        try {
            Projekt_Service service = new Projekt_Service();
            Projekt port = service.getProjektPort();
            rezultat = port.izracunajUdaljenostAerodroma(korisnickoIme, lozinka, icao1, icao2);
        } catch (UnknownHostException_Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return rezultat;
    }
}
