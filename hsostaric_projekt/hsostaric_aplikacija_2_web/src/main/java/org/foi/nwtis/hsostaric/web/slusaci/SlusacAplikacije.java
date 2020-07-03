/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.slusaci;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.hsostaric.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.Aplikacija;
import org.foi.nwtis.hsostaric.ejb.sb.DohvacanjePodataka;
import org.foi.nwtis.hsostaric.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.hsostaric.web.dretve.SlanjePodataka;

/**
 * Klasa koja prestavlja slušač pokretanja aplikacije. Preuzima konfiguraciju
 * baze podatka i pokreće dretvu za preuzimanje podataka za avione.
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    @Inject
    DohvacanjePodataka dohvacanjePodataka;
    private SlanjePodataka slanjePodataka;
    @Inject
    Aplikacija aplikacija;
    @EJB
    AirportsFacadeLocal airportsFacadeLocal;
    @EJB
    MyairportsFacadeLocal myairportsFacadeLocal;
    @EJB
    MyairportslogFacadeLocal myairportslogFacadeLocal;
    @EJB
    KorisniciFacadeLocal korisniciFacadeLocal;
    @EJB
    AirplanesFacadeLocal airplanesFacadeLocal;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            BP_Konfiguracija konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            aplikacija.pokreniDretvu(konf, airportsFacadeLocal, myairportsFacadeLocal, myairportslogFacadeLocal, korisniciFacadeLocal,
                    airplanesFacadeLocal);
            slanjePodataka = new SlanjePodataka(konf, dohvacanjePodataka);
            slanjePodataka.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            Logger.getLogger(SlusacAplikacije.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Aplikacija je pokrenuta u slusacu");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       
        if (slanjePodataka != null) {
            slanjePodataka.interrupt();
        }
        System.out.println("Aplikacija je zaustavljena.");
    }
}
