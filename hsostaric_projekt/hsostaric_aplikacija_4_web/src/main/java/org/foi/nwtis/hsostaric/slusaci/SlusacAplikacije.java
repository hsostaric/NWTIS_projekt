/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.slusaci;

import java.io.File;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;

/**
 * Klasa koja prestavlja slušač pokretanja aplikacije. Preuzima konfiguraciju
 * baze podatka i pokreće dretvu za preuzimanje podataka za avione.
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    private BP_Konfiguracija konf; 

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.out.println("Greška: " + ex);
        }
        System.out.println("Aplikacija je pokrenuta");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
       
        System.out.println("Aplikacija je zaustavljena.");
    }
}
