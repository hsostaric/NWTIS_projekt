/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.konfiguracije.bp;

import java.util.Properties;
import org.foi.nwtis.hsostaric.konfiguracije.Konfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.KonfiguracijaApstraktna;
import org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.NemaKonfiguracije;

/**
 *
 * @author Hrvoje Šoštarić
 */
public class BP_Konfiguracija implements BP_Sucelje{
private String nazivDatoteke = "";
    private Konfiguracija konfig = null;
    
    
    public BP_Konfiguracija(String nazivDatoteke) throws NeispravnaKonfiguracija, NemaKonfiguracije {
        this.nazivDatoteke = nazivDatoteke;
        konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
    }
    
    @Override
    public String getAdminDatabase() {
        return konfig.dajPostavku("admin.database");
    }

    @Override
    public String getAdminPassword() {
        return konfig.dajPostavku("admin.password");
    }

    @Override
    public String getAdminUsername() {
        return konfig.dajPostavku("admin.username");
    }

    @Override
    public String getDriverDatabase() {
        String serverDatabase = getServerDatabase();
        return getDriverDatabase(serverDatabase);
    }

    @Override
    public String getDriverDatabase(String bp_url) {
        String[] p = bp_url.split(":");
        return konfig.dajPostavku("jdbc."+p[1]);
    }

    @Override
    public Properties getDriversDatabase() {
        Properties p = new Properties();
        for(Object o : konfig.dajSvePostavke().keySet()){
            String k = (String) o;
            if(k.startsWith("jdbc")){
                String v = konfig.dajPostavku(k);
                p.setProperty(k, v);
            }
        }
        return p;
    }

    @Override
    public String getServerDatabase() {
        return konfig.dajPostavku("server.database");
    }

    @Override
    public String getUserDatabase() {
        return konfig.dajPostavku("user.database");
    }

    @Override
    public String getUserPassword() {
        return konfig.dajPostavku("user.password"); 
    }

    @Override
    public String getUserUsername() {
        return konfig.dajPostavku("user.username");
    }

    public Konfiguracija getKonfig() {
        return konfig;
    }
    
}
