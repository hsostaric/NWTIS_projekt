/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "prijavaKorisnika")
@SessionScoped
public class PrijavaKorisnika implements Serializable {

    @Getter
    @Setter
    private String korisnickoIme = "";
    @Getter
    @Setter
    private String lozinka = "";
    @Getter
    @Setter
    private Boolean prijavljen = false;

    public PrijavaKorisnika() {
    }

}
