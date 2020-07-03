/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.dretve;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.foi.nwtis.hsostaric.ejb.sb.DohvacanjePodataka;
import org.foi.nwtis.hsostaric.ejb.sb.NadzorAerodromova;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Hrvoje-PC
 */
public class SlanjePodataka extends Thread{
    DohvacanjePodataka dohvacanjePodataka;
    
    private BP_Konfiguracija bpk;
    Boolean radi=true;
    private int interval=0;
    public SlanjePodataka(BP_Konfiguracija bpk, DohvacanjePodataka dohvacanjePodataka) {
        this.bpk = bpk;
        this.dohvacanjePodataka=dohvacanjePodataka;
    }

    @Override
    public void interrupt() {
        radi=false;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        while (radi==true) {  
            long poc=System.currentTimeMillis();
            NadzorAerodromova.sendToAllusers(dohvacanjePodataka
                    .dajZadnjeAzuriranje());
            long trajanje= System.currentTimeMillis()-poc;
            try {
                if(interval-trajanje < 0){
                    sleep(0);
                }else{
                    sleep(interval - trajanje);
                }
               
            } catch (InterruptedException ex) {
                radi=false;
                System.out.println("Greska: "+ex);
            }
        }
    }

    @Override
    public synchronized void start() {
        interval = Integer.parseInt(bpk.
                getKonfig()
                .dajPostavku("dretva.slanjePodataka"));
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
