/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.LocalBean;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;
import org.foi.nwtis.hsostaric.ejb.eb.Dnevnik;
import org.foi.nwtis.hsostaric.ejb.eb.Dnevnik_;
import org.foi.nwtis.hsostaric.ejb.eb.Korisnici;

import org.foi.nwtis.hsostaric.ejb.eb.Myairports;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
@LocalBean
public class DohvacanjePodataka {

    @EJB
    KorisniciFacadeLocal korisniciFacadeLocal;
    @EJB
    AirportsFacadeLocal airportsFacadeLocal;
    @EJB
    MyairportsFacadeLocal myairportsFacadeLocal;
    @EJB
    DnevnikFacadeLocal dnevnikFacadeLocal;

    public String dajZadnjeAzuriranje() {
        int broj = myairportsFacadeLocal.brojAerodromaKojePratimo();
        Date datum = vratiZadnjiPodatak();
        return "Broj aerodroma koji se prate: " + broj + ". Zadnje ažuriranje: " + pretvoriDatumUString(datum);
    }

    private Date vratiZadnjiPodatak() {
        Myairports aerodrom = myairportsFacadeLocal.vratiZadnjiUnos();
        return aerodrom.getStored();
    }

    private String pretvoriDatumUString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        return sdf.format(date);
    }

    public Boolean dodajNoviAerodromUMoje(String poruka) {
        long poc = System.currentTimeMillis();
        String polje[] = poruka.split(";");
        String username = polje[0];
        String ident = polje[1];
        System.out.println("Username: " + username);
        System.out.println("Ident: " + ident.trim());
        Airports aerodrom = airportsFacadeLocal.find(ident.trim());
        Korisnici odabraniKorisnik = korisniciFacadeLocal.find(username.trim());
        List<Myairports> aerodromiKorisnika = myairportsFacadeLocal.vratiKorisnikovAerodrom(username.trim(), ident.trim());
        if (aerodromiKorisnika.isEmpty()) {
            kreirajNoviZapisPracenja(aerodrom, odabraniKorisnik);
            long kraj = System.currentTimeMillis() - poc;
            InetAddress inetAddress;
            try {
                inetAddress = InetAddress.getLocalHost();
                unesiUDnevnik(username, "/dodavanjeAerodroma", "dodavanje aerodroma", inetAddress.getHostAddress(), new Date(), (int) kraj);
            } catch (UnknownHostException ex) {
                System.out.println("Greska: " + ex);
                return false;
            }
            return true;
        } else {
            long kraj = System.currentTimeMillis() - poc;
            InetAddress inetAddress;
            System.out.println("Zapis postoji.");
            try {
                inetAddress = InetAddress.getLocalHost();
                unesiUDnevnik(username, "/dodavanjeAerodroma", "dodavanje aerodroma", inetAddress.getHostAddress(), new Date(), (int) kraj);
            } catch (UnknownHostException ex) {
                System.out.println("Greska: " + ex);
                return false;
            }
            return false;
        }
    }

    private void kreirajNoviZapisPracenja(Airports aerodrom, Korisnici odabraniKorisnik) {
        Myairports noviZapis = new Myairports();
        noviZapis.setIdent(aerodrom);
        noviZapis.setStored(new Date());
        noviZapis.setUsername(odabraniKorisnik);
        myairportsFacadeLocal.create(noviZapis);
        odabraniKorisnik.getMyairportsList()
                .add(noviZapis);
        korisniciFacadeLocal.edit(odabraniKorisnik);
        System.out.println("Zapis je dodan");
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
}
