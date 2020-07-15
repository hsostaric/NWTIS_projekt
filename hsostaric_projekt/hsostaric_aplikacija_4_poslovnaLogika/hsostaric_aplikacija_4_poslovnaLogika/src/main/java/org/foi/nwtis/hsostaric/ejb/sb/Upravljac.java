/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.ejb.Stateless;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;
import org.foi.nwtis.hsostaric.ejb.eb.Korisnici;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
@LocalBean
public class Upravljac {

    @EJB
    KorisniciFacadeLocal korisniciFacadeLocal;

    @EJB
    AirportsFacadeLocal airportsFacadeLocal;

    @EJB
    MyairportsFacadeLocal myairportsFacadeLocal;

    public Boolean autentificirajKorisnika(String username, String password) {
        Korisnici korisnici = korisniciFacadeLocal.find(username);
        if (korisnici != null) {
            if (korisnici.getLozinka()
                    .equals(password)) {
                return true;
            }
        }
        return false;
    }

    public List<Myairports> MojiAerodromi(String username) {
        List<Myairports> lista = new ArrayList<>();
        lista = myairportsFacadeLocal.vratiKorisnikoveAerodrome(username);
        return lista;
    }

    public List<Airports> dajFiltriranePodatke(String naziv) {
        List<Airports> listaAerdoroma = new ArrayList<>();
        listaAerdoroma = airportsFacadeLocal.findAirportbyName(naziv);
        return listaAerdoroma;
    }

    
}
