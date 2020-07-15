/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ws.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.hsostaric.server.Aerodrom;
import org.foi.nwtis.hsostaric.server.Projekt;
import org.foi.nwtis.hsostaric.server.Projekt_Service;
import org.foi.nwtis.hsostaric.server.UnknownHostException_Exception;

/**
 *
 * @author Hrvoje-PC
 */
public class Aplikacija_3WS {

    public List<Aerodrom> dajKorisnikoveAerodrome(String korisnickoIme, String lozinka) {
        List<Aerodrom> aerodromi = new ArrayList<>();
        try {
            Projekt_Service service = new org.foi.nwtis.hsostaric.server.Projekt_Service();
            Projekt port = service.getProjektPort();
            aerodromi = port.dohvatiVlastiteAerodrome(korisnickoIme, lozinka);
        } catch (UnknownHostException_Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return aerodromi;
    }

    public List<Aerodrom> dajAerodromeNaziv(String korisnik, String lozinka, String naziv) {
        List<Aerodrom> lista = new ArrayList<>();

        try {
            Projekt_Service service = new Projekt_Service();
            Projekt port = service.getProjektPort();
            lista = port.dohvatiAerodromePoImenu(korisnik, lozinka, naziv);
        } catch (UnknownHostException_Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return lista;
    }

    public List<Aerodrom> vratiAerodromeDrzave(String korisnik, String lozinka, String drzava) {
        List<Aerodrom> lista = new ArrayList<>();
        try {
            Projekt_Service service = new org.foi.nwtis.hsostaric.server.Projekt_Service();
            Projekt port = service.getProjektPort();
            lista = port.dohvatiAerodromeIzDrzave(korisnik, lozinka, drzava);
        } catch (UnknownHostException_Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return lista;
    }

}
