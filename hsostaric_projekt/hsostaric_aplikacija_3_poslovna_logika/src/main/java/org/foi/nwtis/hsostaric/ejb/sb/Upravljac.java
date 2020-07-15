/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import org.foi.nwtis.hsostaric.ejb.eb.Airplanes;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;
import org.foi.nwtis.hsostaric.ejb.eb.Dnevnik;
import org.foi.nwtis.hsostaric.ejb.eb.Korisnici;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
@Local
public class Upravljac {

    @EJB
    KorisniciFacadeLocal korisniciFacadeLocal;
    @EJB
    MyairportsFacadeLocal myairportsFacadeLocal;
    @EJB
    AirportsFacadeLocal airportsFacadeLocal;
    @EJB
    AirplanesFacadeLocal airplanesFacadeLocal;

    @EJB
    DnevnikFacadeLocal dnevnikFacadeLocal;

    public Boolean autentificirajKorisnika(String username, String password) {
        Korisnici korisnici = korisniciFacadeLocal.find(username);
        if (korisnici != null) {
            if (korisnici.getLozinka()
                    .equals(password)) {
                return true;
            }
        }
        return false;
    }

    public Airports dohvatiMojAerodrom(String username, String icao) {
        List<Myairports> listaMojihAerodroma = new ArrayList<>();
        Airports airports = null;
        listaMojihAerodroma = myairportsFacadeLocal.vratiKorisnikovAerodrom(username, icao);
        if (!listaMojihAerodroma.isEmpty()) {
            for (Myairports myairports : listaMojihAerodroma) {
                airports = myairports.getIdent();
                break;
            }
        }
        return airports;
    }

    public List<Myairports> vratiZapisIzMojihAerodroma(String username, String icao) {
        List<Myairports> myairports = new ArrayList<>();
        myairports = myairportsFacadeLocal.vratiKorisnikovAerodrom(username, icao);
        return myairports;
    }

    public Airports pronadjiAerodromPremaIdu(String ident) {
        return airportsFacadeLocal.find(ident);
    }

    public void prestaniPratitiAerodrom(Myairports myairports) {
        Korisnici korisnici = myairports.getUsername();
        List<Myairports> lista = korisnici.getMyairportsList();
        lista.remove(myairports);
        korisnici.setMyairportsList(lista);
        korisniciFacadeLocal.edit(korisnici);
        myairportsFacadeLocal.remove(myairports);
    }

    public void obrisiLetoveAviona(List<Airplanes> listaLetova) {
        if (!listaLetova.isEmpty()) {
            for (Airplanes avioni : listaLetova) {
                airplanesFacadeLocal.remove(avioni);
            }
          
        }
    }

    public void unesiUDnevnik(String korisnickoIme, String url_adresa, String radnja, String ip_adresa, Date vrijemePrijema, int tranjanjeZahtjeva) {
        Dnevnik dnevnik;
        dnevnik = new Dnevnik();
        Korisnici korisnici = korisniciFacadeLocal.find(korisnickoIme);
        dnevnik.setIpAdresa(ip_adresa);
        dnevnik.setTrajanjeObrade(tranjanjeZahtjeva);
        dnevnik.setVrijemePrijema(vrijemePrijema);
        dnevnik.setUsername(korisnici);
        dnevnik.setUrlAdresa(url_adresa);
        dnevnik.setRadnja(radnja);
        dnevnikFacadeLocal.create(dnevnik);
        korisnici.getDnevnikList().add(dnevnik);
        korisniciFacadeLocal.edit(korisnici);
    }

    public List<Korisnici> dajSveKorisnikeIzBaze() {
        return korisniciFacadeLocal.findAll();
    }

    public List<Korisnici> dajFiltriraneKorisnike(String username) {
        List<Korisnici> lista = new ArrayList<>();
        lista = korisniciFacadeLocal.vratiFiltriraneKorisnike(username);
        return lista;
    }

    public List<Dnevnik> vratiKorisnikovezapise(String username) {
        List<Dnevnik> lista = new ArrayList<>();
        lista = dnevnikFacadeLocal.vratiKorisnikoveZapise(username);
        return lista;
    }

    public Boolean zauzetostEmaila(String email) {
        Boolean zauzeto = true;
        zauzeto = korisniciFacadeLocal.provjeriZauzetostMaila(email);
        return zauzeto;
    }

    public void dodajKorisnikaLokalno(String korisnickoIme, String lozinka, String email, String ime, String prezime) {
        List<Korisnici> listaKorisnika = new ArrayList<>();
        listaKorisnika = korisniciFacadeLocal.vratiFiltriraneKorisnike(korisnickoIme);
        if (listaKorisnika.isEmpty()) {
            Korisnici korisnici = new Korisnici();
            korisnici.setIme(ime);
            korisnici.setPrezime(prezime);
            korisnici.setKorIme(korisnickoIme);
            korisnici.setEmailAdresa(email);
            korisnici.setLozinka(lozinka);
            korisnici.setDatumKreiranja(new Date());
            korisnici.setDatumPromjene(new Date());
            korisniciFacadeLocal.create(korisnici);
        }

    }

    public List<String> vratiKorisnikoveAerodrome(String username) {
        List<String> lista = new ArrayList<>();
        Korisnici korisnici = korisniciFacadeLocal.find(username);
        if (!korisnici.getMyairportsList().isEmpty()) {
            for (Myairports myairports : korisnici
                    .getMyairportsList()) {
                lista.add(myairports
                        .getIdent().getIdent());
            }
        }
        return lista;
    }

    public List<Airplanes> dohvatiLetoveAerodroma(String icao) {
        List<Airplanes> lista = new ArrayList<>();
        lista = airplanesFacadeLocal.dajLetoveAerodroma(icao);
        return lista;
    }
}
