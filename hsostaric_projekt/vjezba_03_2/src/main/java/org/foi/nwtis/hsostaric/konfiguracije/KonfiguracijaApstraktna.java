/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.konfiguracije;

import java.util.Enumeration;
import java.util.Properties;

/**
 *
 * @author NWTiS_1
 */
public abstract class KonfiguracijaApstraktna implements Konfiguracija {

    protected String datoteka;
    protected Properties postavke;

    public KonfiguracijaApstraktna(String datoteka) {
        this.datoteka = datoteka;
        this.postavke= new Properties();
    }

    public String dajPostavku(String postavka) {
        return this.postavke.getProperty(postavka);
    }

    @Override
    public boolean spremiPostavku(String postavka, String vrijednost) {
        if (this.postavke.containsKey(postavka)) {
            return false;
        } else {
            this.postavke.setProperty(postavka, vrijednost);
            return true;
        }
    }

    public boolean azurirajPostavku(String postavka, String vrijednost) {
        if (!this.postavke.containsKey(postavka)) {
            return false;
        } else {
            this.postavke.setProperty(postavka, vrijednost);
            return true;
        }
    }

    @Override
    public boolean postojiPostavka(String postavka) {
        return this.postavke.containsKey(postavka);
    }

    @Override
    public boolean obrisiPostavku(String postavka) {
        if (!this.postavke.containsKey(postavka)) {
            return false;
        } else {
            this.postavke.remove(postavka);
            return true;
        }
    }

    @Override
    public Properties dajSvePostavke() {
        return this.postavke;
    }

    @Override
    public boolean obrisiSvePostavke() {
        if (this.postavke.isEmpty()) {
            return false;
        } else {
            this.postavke.clear();
            return true;
        }
    }

    @Override
    public void dodajKonfiguraciju(Properties postavke) {
        for (Enumeration e = postavke.keys(); e.hasMoreElements();) {
            String kljuc = (String) e.nextElement();
            String vrijednost = postavke.getProperty(kljuc);
            this.postavke.setProperty(kljuc, vrijednost);
        }
    }

    @Override
    public void kopirajKonfiguraciju(Properties postavke) {
        this.postavke = postavke;
    }

    public static Konfiguracija kreirajKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
       Konfiguracija konfiguracija = dajIspravnukonfiguraciju(datoteka);
        konfiguracija.spremiKonfiguraciju();
        return konfiguracija;
    }

    public static Konfiguracija preuzmiKonfiguraciju(String datoteka)
            throws NemaKonfiguracije, NeispravnaKonfiguracija {

       Konfiguracija konfiguracija = dajIspravnukonfiguraciju(datoteka);
        konfiguracija.ucitajKonfiguraciju();
        return konfiguracija;
        
    }
    
     private static Konfiguracija dajIspravnukonfiguraciju(String datoteka1) throws NeispravnaKonfiguracija {
        int p = datoteka1.lastIndexOf(".");
        if (p == -1) {
            throw new NeispravnaKonfiguracija("Nema ekstenzije!");
        }
        String ekstenzija = datoteka1.substring(p + 1).toLowerCase();
        switch (ekstenzija) {
            case "txt":
                return new KonfiguracijaTXT(datoteka1);
            case "xml":
                return new KonfiguracijaXML(datoteka1);
            case "bin":
                return new KonfiguracijaBIN(datoteka1);
            case "json":
                return new KonfiguracijaJSON(datoteka1);
            default:
                throw new NeispravnaKonfiguracija("Nema ekstenzije!");
        }
    }
}
