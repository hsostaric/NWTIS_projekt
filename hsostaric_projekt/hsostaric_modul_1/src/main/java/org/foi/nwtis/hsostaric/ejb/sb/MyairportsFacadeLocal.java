/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;

/**
 *
 * @author Hrvoje-PC
 */
@Local
public interface MyairportsFacadeLocal {

    void create(Myairports myairports);

    void edit(Myairports myairports);

    void remove(Myairports myairports);

    Myairports find(Object id);

    List<Myairports> findAll();

    List<Myairports> findRange(int[] range);

    int count();

    List<String> dajJedinstveneAerodrome();

    int brojAerodromaKojePratimo();

    Boolean pracenjeAerodroma(String icao);

    Myairports vratiZadnjiUnos();

    List<Myairports> vratiKorisnikoveAerodrome(String korisnickoIme);

    List<Myairports> vratiKorisnikovAerodrom(String korisnickoIme, String icao);
}
