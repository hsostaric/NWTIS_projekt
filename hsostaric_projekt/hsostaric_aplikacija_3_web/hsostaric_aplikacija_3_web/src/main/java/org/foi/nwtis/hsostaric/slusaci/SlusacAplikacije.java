/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.slusaci;

import java.io.File;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.foi.nwtis.hsostaric.ejb.sb.MqttPraviFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.NemaKonfiguracije;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.hsostaric.web.mqttslusac.MqttListener;
import org.foi.nwtis.hsostaric.zrna.PrijavaKorisnika;

/**
 * Klasa koja prestavlja slušač pokretanja aplikacije. Preuzima konfiguraciju
 * baze podatka i pokreće dretvu za preuzimanje podataka za avione.
 */
@WebListener
public class SlusacAplikacije implements ServletContextListener {

    private BP_Konfiguracija konf = null;
    private BP_Konfiguracija bpk;
    private MqttListener mqttListener;
    @EJB
    MqttPraviFacadeLocal mqttPraviFacadeLocal;
    @EJB
    MyairportsFacadeLocal myairportsFacadeLocal;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext sc = sce.getServletContext();
        String datoteke = sc.getInitParameter("konfiguracija");
        String putanja = sc.getRealPath("WEB-INF") + File.separator + datoteke;
        try {
            konf = new BP_Konfiguracija(putanja);
            sc.setAttribute("BP_Konfig", konf);
            mqttListener = new MqttListener(konf, mqttPraviFacadeLocal, myairportsFacadeLocal);
            mqttListener.start();
        } catch (NemaKonfiguracije | NeispravnaKonfiguracija ex) {
            System.out.println("Greška: " + ex);
        }
        System.out.println("Aplikacija je pokrenuta");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (mqttListener != null) {
            mqttListener.interrupt();
        }
        System.out.println("Aplikacija je zaustavljena.");
    }
}
