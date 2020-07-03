/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zrna;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.ejb.eb.Dnevnik;
import org.foi.nwtis.hsostaric.ejb.sb.Upravljac;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "upravljanjeDnevnikom")
@ViewScoped
public class UpravljanjeDnevnikom implements Serializable {

    @Inject
    ServletContext servletContext;
    BP_Konfiguracija bpk;
    @Inject
    Upravljac upravljac;
    @Getter
    @Setter
    private int brojStranica;
    @Inject
    PrijavaKorisnika prijavljenKorisnik;
    @Getter
    @Setter
    private List<Dnevnik> zapisiKorisnika = new ArrayList<>();

    public UpravljanjeDnevnikom() {
    }

    @PostConstruct
    public void init() {
        zapisiKorisnika = new ArrayList<>();
        zapisiKorisnika = upravljac.vratiKorisnikovezapise(
                prijavljenKorisnik.getKorisnickoIme());
        bpk = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
        brojStranica = Integer.parseInt(bpk
                .getKonfig()
                .dajPostavku("brojPoStranici"));
    }

    public String formatirajDatum(Date datumPreuzimanja) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        return sdf.
                format(datumPreuzimanja);
    }
    public String osvjeziDnevnik(){
    zapisiKorisnika = upravljac.vratiKorisnikovezapise(
                prijavljenKorisnik.getKorisnickoIme());
    return "";
    }

}
