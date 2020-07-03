/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.zrna;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.server.Aerodrom;
import org.foi.nwtis.hsostaric.server.AvionLeti;
import org.foi.nwtis.hsostaric.ws.klijenti.Aplikacija2_WS;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "pregledAerodroma")
@ViewScoped
public class PregledAerodroma implements Serializable {

    @Inject
    PrijavaKorisnika prijavaKorisnika;
    @Getter
    @Setter
    String datumPocetka = "";
    @Getter
    @Setter
    String datumKraja = "";
    @Getter
    @Setter
    private List<Aerodrom> korisnikoviAerodromi = new ArrayList<>();
    @Getter
    @Setter
    private List<AvionLeti> letoviAerodroma = new ArrayList<>();
    private Aplikacija2_WS aplikacija2_WS;
    @Getter
    @Setter
    private String odabraniAerodrom = "";
    @Getter
    @Setter
    private long brojSekundiOd = 0;
    @Getter
    @Setter
    private long brojSekundiDo = 0;
    @Getter
    @Setter
    private List<AvionLeti> letoviAviona = new ArrayList<>();
    @Getter
    @Setter
    private String vidljivostDrugogBloka = "";
    @Getter
    @Setter
    private String vidljivostTrecegBloka = "";

    public PregledAerodroma() {
    }

    @PostConstruct
    public void init() {
        String username = prijavaKorisnika.getKorisnickoIme();
        String password = prijavaKorisnika.getLozinka();
        aplikacija2_WS = new Aplikacija2_WS(username, password);
        korisnikoviAerodromi = aplikacija2_WS.dajKorisnikoveAerodrome();
        sakrijDrugiBlok();
        sakrijTreciBlok();
    }

    private void prikaziDrugiBlok() {
        vidljivostDrugogBloka = "vidljiv";
    }

    private void sakrijDrugiBlok() {
        vidljivostDrugogBloka = "skriven";
    }

    private void prikaziTreciBlok() {
        vidljivostTrecegBloka = "vidljiv";
    }

    private void sakrijTreciBlok() {
        vidljivostTrecegBloka = "skriven";
    }

    public String dajLetoveAerodroma() {
        boolean ispravnostPrvog = false;
        boolean ispravnostDrugog = false;
        if (odabraniAerodrom != null
                || !odabraniAerodrom.equals("")) {
            if ((datumPocetka != null || !datumPocetka.equals(""))
                    && (datumKraja != null || !datumPocetka.equals(""))) {
                ispravnostPrvog = provjeriFormat(datumPocetka);
                ispravnostDrugog = provjeriFormat(datumKraja);
                if (ispravnostPrvog == true
                        && ispravnostDrugog == true) {
                    postaviVarijable();
                    if (brojSekundiOd > brojSekundiDo) {
                        long pom = 0;
                        pom = brojSekundiDo;
                        brojSekundiDo = brojSekundiOd;
                        brojSekundiOd = pom;
                    }
                    letoviAerodroma = aplikacija2_WS.vratiLetoveAerodroma(odabraniAerodrom, brojSekundiOd, brojSekundiDo);
                    prikaziDrugiBlok();
                    sakrijTreciBlok();
                } else {
                    sakrijDrugiBlok();
                    sakrijTreciBlok();
                }
            } else {
                sakrijDrugiBlok();
                sakrijTreciBlok();
            }
        } else {
            sakrijDrugiBlok();
            sakrijTreciBlok();
        }
        return "";
    }

    private boolean provjeriFormat(String dateStr) {
        try {
            Date date = new SimpleDateFormat("dd.MM.yyyy HH:mm")
                    .parse(dateStr);
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

    private void postaviVarijable() {
        try {
            Date od = new SimpleDateFormat("dd.MM.yyyy HH:mm")
                    .parse(datumPocetka);
            Date kraj = new SimpleDateFormat("dd.MM.yyyy HH:mm").
                    parse(datumKraja);
            brojSekundiOd = od.getTime() / 1000;
            brojSekundiDo = kraj.getTime() / 1000;
        } catch (ParseException ex) {
            System.out.println("Greska: " + ex);
        }
    }

    public String pretvoriSekundeUdatum(int sekunde) {
        Date date = new Date(sekunde * 1000L);
        SimpleDateFormat datumPredlozak = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return datumPredlozak
                .format(date);
    }

    public String dohvatiLetoveAviona(String icao24) {
        boolean ispravnostPrvog = false;
        boolean ispravnostDrugog = false;
        if (icao24 != null
                || !icao24.equals("")) {
            if ((datumPocetka != null || !datumPocetka.equals(""))
                    && (datumKraja != null || !datumPocetka.equals(""))) {
                ispravnostPrvog = provjeriFormat(datumPocetka);
                ispravnostDrugog = provjeriFormat(datumKraja);
                if (ispravnostPrvog == true
                        && ispravnostDrugog == true) {
                    postaviVarijable();
                    if (brojSekundiOd > brojSekundiDo) {
                        long pom = 0;
                        pom = brojSekundiDo;
                        brojSekundiDo = brojSekundiOd;
                        brojSekundiOd = pom;
                    }
                    prikaziTreciBlok();
                    letoviAviona = aplikacija2_WS.vratiLetoveAviona(icao24, brojSekundiOd, brojSekundiDo);
                }else{
                sakrijTreciBlok();
                }
            }else{
            sakrijTreciBlok();
            }
        }else{
        sakrijTreciBlok();
        }
        return "";
    }

}
