/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ws.klijenti;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.dkermek.ws.serveri.Aerodrom;
import org.foi.nwtis.dkermek.ws.serveri.AerodromiWS;
import org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;

/**
 * Klasa za pozivanje operacija SOAP servisa iz nastavničke aplikacije.
 *
 * @author Hrvoje-PC
 */
public class Aerodromi_WS {

    public Boolean autentificirajGrupu(String korisnickoIme, String korisnickaLozinka) {
        Boolean result = false;
        try {
            AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            result = port.autenticirajGrupu(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return result;
    }

    public Boolean registrirajGrupu(String korisnickoIme, String korisnickaLozinka) {
        Boolean result = false;
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            result = port.registrirajGrupu(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return result;
    }

    public Boolean odjaviGrupu(String korisnickoIme, String korisnickaLozinka) {
        Boolean result = false;
        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            result = port.deregistrirajGrupu(korisnickoIme, korisnickaLozinka);
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return result;
    }

    public StatusKorisnika dajStatusKorisnika(String korisnickoIme, String lozinka) {
        try {
            AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            StatusKorisnika result = port.dajStatusGrupe(korisnickoIme, lozinka);
            return result;
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return StatusKorisnika.NEPOSTOJI;
    }

    public Boolean aktivirajGrupu(String korisnickoIme, String lozinka) {
        Boolean result = false;
        try { // Call Web Service Operation
            AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            result = port.aktivirajGrupu(korisnickoIme, lozinka);
            System.out.println("Result = " + result);
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return result;
    }

    public Boolean blokirajGrupu(String username, String password) {
        Boolean result = false;
        try { // Call Web Service Operation
            AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            result = port.blokirajGrupu(username, password);
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return result;
    }

    public Boolean obrisiAerodromeGrupe(String korisnickoIme, String lozinka) {
        Boolean result = false;

        try {
            AerodromiWS_Service service = new AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            result = port.obrisiSveAerodromeGrupe(korisnickoIme, lozinka);
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return result;
    }

    public Boolean aerodromiGrupe(String korisnickoIme, String korisnickaLozinka) {
        Boolean postoji = false;
        List<Aerodrom> result = new ArrayList<Aerodrom>();
        try {
            AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            result = port.dajSveAerodromeGrupe(korisnickoIme, korisnickaLozinka);
            postoji = result.size() > 0;
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return postoji;
    }

    public Boolean dodajAerodromGrupi(String username, String password, String icao) {
        Boolean result = false;
        try { 
            AerodromiWS_Service service = new org.foi.nwtis.dkermek.ws.serveri.AerodromiWS_Service();
            AerodromiWS port = service.getAerodromiWSPort();
            result = port.dodajAerodromIcaoGrupi(username, password, icao);
        } catch (Exception ex) {
            System.out.println("Greska: " + ex);
        }
        return result;
    }

}
