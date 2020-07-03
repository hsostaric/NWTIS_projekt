/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.zrna;

import java.awt.BorderLayout;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.enterprise.context.Dependent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.ejb.eb.Airplanes;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;
import org.foi.nwtis.hsostaric.ejb.sb.Meteorolog;
import org.foi.nwtis.hsostaric.ejb.sb.Upravljac;
import org.foi.nwtis.hsostaric.endpoint.client.WebSocketClient;
import org.foi.nwtis.hsostaric.konfiguracije.Konfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "upravljanjeAerodromima")
@ViewScoped
public class UpravljanjeAerodromima implements Serializable {

    @Getter
    @Setter
    List<Myairports> mojiAerodromi = new ArrayList<>();
    @Inject
    Upravljac upravljac;
    @Inject
    PrijavaKorisnika prijavaKorisnika;
    @Inject
    ServletContext servletContext;
    private BP_Konfiguracija bpk;
    private Konfiguracija konfiguracija;
    @Getter
    @Setter
    int brojStranica = 0;
    @Getter
    @Setter
    String owmKlijent = "";
    @Getter
    @Setter
    String liqKlijent = "";
    @Inject
    Meteorolog meteorolog;
    @Getter
    @Setter
    private String longitudeServisa = "";
    @Getter
    @Setter
    private String latidudeServisa = "";
    @Getter
    @Setter
    private String temperatura = "";
    @Getter
    @Setter
    private String postotakVlage = "";
    Lokacija lokacijaServisa;
    @Getter
    @Setter
    private List<Airports> filtriraniAerodromi = new ArrayList<>();
    @Getter
    @Setter
    private String nazivZaFiltriranje = "";

    @Getter
    @Setter
    private String vidljivostBloka = "";
    @Getter
    @Setter
    private String odabraniAerodrom = "";

    @Getter
    @Setter
    private String odgovorServera = "";

    public UpravljanjeAerodromima() {
    }

    @PostConstruct
    public void init() {
        String username = "";
        username = prijavaKorisnika.getKorisnickoIme();
        mojiAerodromi = upravljac.MojiAerodromi(username);
        bpk = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
        konfiguracija = bpk.getKonfig();
        brojStranica = Integer.parseInt(konfiguracija.dajPostavku("brojPoStranici"));
        owmKlijent = konfiguracija.dajPostavku("OpenWeatherMap.apikey");
        liqKlijent = konfiguracija.dajPostavku("LocationIQ.token");
        meteorolog.setApiKeyOWM(owmKlijent);
        meteorolog.setApiLIQ(liqKlijent);
        sakrijVidljivost();
    }

    private void sakrijVidljivost() {
        vidljivostBloka = "skriven";
    }

    private void prikaziDrugiBlok() {
        vidljivostBloka = "vidljiv";
    }

    public String dajPodatkeSaServisa(Airports airport) {
        postotakVlage = meteorolog.dohvatiVlagu(airport);
        temperatura = meteorolog.dohvatiTemperaturu(airport);
        lokacijaServisa = meteorolog.dajLokacijuLoqatoinIQ(airport.getName());
        latidudeServisa = lokacijaServisa.getLatitude();
        longitudeServisa = lokacijaServisa.getLongitude();
        return "";
    }

    public String dajFiltriranePodatke() {
        if (nazivZaFiltriranje == null
                || nazivZaFiltriranje.equals("")) {
            nazivZaFiltriranje = "%";
        }
        filtriraniAerodromi = upravljac.dajFiltriranePodatke(nazivZaFiltriranje);
        if (filtriraniAerodromi.isEmpty()) {
            sakrijVidljivost();
        } else {
            prikaziDrugiBlok();
        }
        return "";
    }

    public String posaljiPorukuNaEndpoint() {
        try {
            final WebSocketClient webSocketClient = new WebSocketClient(
                    new URI("ws://localhost:8084/hsostaric_aplikacija_2_web/dodavanjeAerodroma"));
            webSocketClient.addMessageHandler(new WebSocketClient.MessageHandler() {
                @Override
                public void handleMessage(String message) {
                    odgovorServera = message;
                }
            });
            webSocketClient.sendMessage(prijavaKorisnika.getKorisnickoIme() + ";" + odabraniAerodrom);
            Thread.sleep(2000);
        } catch (URISyntaxException | InterruptedException ex) {
            System.out.println("Greska: " + ex);
        }
        mojiAerodromi = upravljac.MojiAerodromi(prijavaKorisnika.getKorisnickoIme());
        return "";
    }
}
