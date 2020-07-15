/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.rest.klijenti.Aplikacija_3RS;
import org.foi.nwtis.hsostaric.server.Aerodrom;
import org.foi.nwtis.hsostaric.ws.klijenti.Aplikacija2_WS;
import org.foi.nwtis.podaci.OdgovorAerodrom;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "pracenjeAerodroma")
@ViewScoped
public class PracenjeAerodroma implements Serializable {

    @Inject
    PrijavaKorisnika prijavaKorisnika;
    private Aplikacija2_WS aplikacija2_WS;
    String username;
    String password;
    @Getter
    @Setter
    private String odabraniAerodrom = "";
    @Getter
    @Setter
    private String pocetnaUdaljenost = "";
    @Getter
    @Setter
    private String krajnjaUdaljenost = "";
    @Getter
    @Setter
    private String odgovorServera = "";
    @Getter
    @Setter
    private List<Aerodrom> listaAerodroma = new ArrayList<>();
    private Aplikacija_3RS aplikacija_3RS;
    @Getter
    @Setter
    private List<Aerodrom> listaAerodromaIzRaspona = new ArrayList<>();
    @Getter
    @Setter
    private String vidljivostBloka = "";

    public PracenjeAerodroma() {

    }

    @PostConstruct
    public void init() {
        username = prijavaKorisnika.getKorisnickoIme();
        password = prijavaKorisnika.getLozinka();
        dohvatiKorisnikoveAerodrome();
        sakrijBlok();
        aplikacija2_WS = new Aplikacija2_WS(username, password);
    }

    private void sakrijBlok() {
        vidljivostBloka = "skriven";
    }

    private void prikaziBlok() {
        vidljivostBloka = "vidljiv";

    }

    private void dohvatiKorisnikoveAerodrome() {
        aplikacija_3RS = new Aplikacija_3RS(username, password);
        OdgovorAerodrom odgovor = aplikacija_3RS.dajAerodomeKorisnika(OdgovorAerodrom.class);
        listaAerodroma = new ArrayList(Arrays.
                asList(odgovor
                        .getOdgovor()));
        if (listaAerodroma == null
                || listaAerodroma.isEmpty()) {
            listaAerodroma = new ArrayList();
        }
    }

    public String prikaziAerodromeIzRaspona() {
        if (odabraniAerodrom != null || !odabraniAerodrom.equals("")) {
            if ((pocetnaUdaljenost != null || !pocetnaUdaljenost.equals(""))
                    && (krajnjaUdaljenost != null || !krajnjaUdaljenost.equals(""))) {
                int pocetna = Integer.parseInt(pocetnaUdaljenost);
                int zavrsna = Integer.parseInt(krajnjaUdaljenost);
                prikaziBlok();
                listaAerodromaIzRaspona = aplikacija2_WS.vratiAerodromeURasponu(odabraniAerodrom, pocetna, zavrsna);
            }
        }
        return "";
    }

    public int udaljenostAerodroma(String icaoReda) {
        int udaljenost = 0;
        udaljenost = aplikacija2_WS.vratiUdaljenostIzmedjuAerodroma(odabraniAerodrom, icaoReda);
        return udaljenost;

    }

    public String prestaniPratitAerodrom() {
        OdgovorAerodrom odgovorAerodrom = aplikacija_3RS.prestaniPratitAerodrom(OdgovorAerodrom.class, odabraniAerodrom);
        odgovorServera = odgovorAerodrom.getStatus();
        OdgovorAerodrom odgovor = aplikacija_3RS.dajAerodomeKorisnika(OdgovorAerodrom.class);
        listaAerodroma = new ArrayList(Arrays.
                asList(odgovor
                        .getOdgovor()));
        if (listaAerodroma == null
                || listaAerodroma.isEmpty()) {
            listaAerodroma = new ArrayList();
        }
        return "";
    }

    public String obrisiLetoveAerodroma() {
        OdgovorAerodrom odgovorAerodrom = aplikacija_3RS.obrisiLetoveAerodroma(OdgovorAerodrom.class, odabraniAerodrom);
        odgovorServera = odgovorAerodrom.getStatus();
          OdgovorAerodrom odgovor = aplikacija_3RS.dajAerodomeKorisnika(OdgovorAerodrom.class);
        listaAerodroma = new ArrayList(Arrays.
                asList(odgovor
                        .getOdgovor()));
        if (listaAerodroma == null
                || listaAerodroma.isEmpty()) {
            listaAerodroma = new ArrayList();
        }
        return "";

    }
}
