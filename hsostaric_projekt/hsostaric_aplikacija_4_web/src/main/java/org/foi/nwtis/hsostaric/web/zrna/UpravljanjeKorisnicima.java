/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.zrna;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;
import org.foi.nwtis.hsostaric.ejb.sb.Upravljac;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "upravljanjeKorisnicima")
@ViewScoped
public class UpravljanjeKorisnicima implements Serializable {

    @Inject
    PrijavaKorisnika prijavaKorisnika;
    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;
    @Inject
    Upravljac upravljac;
    

    public UpravljanjeKorisnicima() {
    }


    public String prijavaKorisnika() {
        if (upravljac.autentificirajKorisnika(username, password) == true) {
            postaviVarijable();
            return "uspjesnaPrijava";
        }
        return "";
    }

    private void postaviVarijable() {
        prijavaKorisnika.setKorisnickoIme(username);
        prijavaKorisnika.setLozinka(password);
        prijavaKorisnika.setPrijavljen(true);
    }

}
