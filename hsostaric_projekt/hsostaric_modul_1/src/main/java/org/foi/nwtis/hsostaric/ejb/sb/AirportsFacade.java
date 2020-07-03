/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
public class AirportsFacade extends AbstractFacade<Airports> implements AirportsFacadeLocal {

    @PersistenceContext(unitName = "org.foi.nwtis.hsostaric_hsostaric_modul_1_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirportsFacade() {
        super(Airports.class);
    }
     @Override
    public List<Airports> findAirportbyName(String naziv) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airports> createQuery = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = createQuery.from(Airports.class);
        Expression<String> name = aerodromi.get("name");
        createQuery.where(cb.like(name, naziv));
        return em.createQuery(createQuery)
                .getResultList();
    }

    @Override
    public List<Airports> findAirportsfromCountry(String drzava) {
      CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airports> createQuery = cb.createQuery(Airports.class);
        Root<Airports> aerodromi = createQuery.from(Airports.class);
        Expression<String> country = aerodromi.get("isoCountry");
        createQuery.where(cb.equal(country, drzava));
        return em.createQuery(createQuery)
                .getResultList();
    }
    
}
