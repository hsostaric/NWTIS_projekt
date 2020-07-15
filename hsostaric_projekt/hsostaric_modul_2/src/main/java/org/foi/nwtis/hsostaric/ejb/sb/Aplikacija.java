/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.hsostaric.web.dretve.PreuzimanjeLetovaAvionaAerodroma;
import org.foi.nwtis.hsostaric.web.dretve.SlanjePodataka;

/**
 *
 * @author Hrvoje-PC
 */
@Singleton
@Startup
public class Aplikacija {
    private PreuzimanjeLetovaAvionaAerodroma preuzimanjeLetovaAvionaAerodroma;
    private SlanjePodataka slanjePodataka; 
    
    public void pokreniDretvu(BP_Konfiguracija bpk, AirportsFacadeLocal airportsFacadeLocal, 
            MyairportsFacadeLocal myairportsFacadeLocal, MyairportslogFacadeLocal myairportslogFacadeLocal, KorisniciFacadeLocal korisniciFacadeLocal, AirplanesFacadeLocal airplanesFacadeLocal){
        preuzimanjeLetovaAvionaAerodroma = new PreuzimanjeLetovaAvionaAerodroma(bpk, airportsFacadeLocal, myairportsFacadeLocal,
                myairportslogFacadeLocal,korisniciFacadeLocal,airplanesFacadeLocal);
        preuzimanjeLetovaAvionaAerodroma.start();
    }
    @PreDestroy
    public void zaustaviDretvu(){
    if(preuzimanjeLetovaAvionaAerodroma!=null){
        preuzimanjeLetovaAvionaAerodroma.interrupt();
    }
    }
}
