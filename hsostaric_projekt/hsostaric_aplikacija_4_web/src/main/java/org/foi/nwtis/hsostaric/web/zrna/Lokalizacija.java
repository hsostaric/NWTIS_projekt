/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.zrna;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "lokalizacija")
@SessionScoped
public class Lokalizacija implements Serializable {
    @Getter
    @Setter
    private String jezik = "hr";

    @Inject
    private FacesContext facesContext;

    public Lokalizacija() {
    }
    
     public Object odaberiJezik() {
        facesContext.getViewRoot()
                .setLocale(new Locale(jezik));
        return "";
    }
    
}
