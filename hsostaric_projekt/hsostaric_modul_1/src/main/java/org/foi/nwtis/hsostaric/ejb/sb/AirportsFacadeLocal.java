/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;

/**
 *
 * @author Hrvoje-PC
 */
@Local
public interface AirportsFacadeLocal {

    void create(Airports airports);

    void edit(Airports airports);

    void remove(Airports airports);

    Airports find(Object id);

    List<Airports> findAll();

    List<Airports> findRange(int[] range);

    List<Airports> findAirportbyName(String naziv);

    List<Airports> findAirportsfromCountry(String drzava);

    int count();

}
