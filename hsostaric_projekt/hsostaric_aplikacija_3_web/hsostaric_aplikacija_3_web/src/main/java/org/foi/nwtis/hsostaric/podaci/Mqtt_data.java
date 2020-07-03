/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.podaci;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Hrvoje-PC
 */
public class Mqtt_data {
    @Getter
    @Setter
    private String korisnik;
    @Getter
    @Setter
    private String aerodrom;
    @Getter
    @Setter
    private String avion;
    @Getter
    @Setter
    private String oznaka;
    @Getter
    @Setter
    private String poruka;
    @Getter
    @Setter
    private String vrijeme;

    public Mqtt_data() {
    }

    public Mqtt_data(String korisnik, String aerodrom, String avion, String oznaka, String poruka, String vrijeme) {
        this.korisnik = korisnik;
        this.aerodrom = aerodrom;
        this.avion = avion;
        this.oznaka = oznaka;
        this.poruka = poruka;
        this.vrijeme = vrijeme;
    }
    
    
}
