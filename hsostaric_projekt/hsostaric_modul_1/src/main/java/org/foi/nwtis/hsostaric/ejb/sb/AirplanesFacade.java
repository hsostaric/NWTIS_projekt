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
import org.foi.nwtis.hsostaric.ejb.eb.Airplanes;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
public class AirplanesFacade extends AbstractFacade<Airplanes> implements AirplanesFacadeLocal {

    @PersistenceContext(unitName = "org.foi.nwtis.hsostaric_hsostaric_modul_1_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AirplanesFacade() {
        super(Airplanes.class);
    }

    @Override
    public List<Airplanes> findLetoviAviona(String icao24, long od, long kraj) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airplanes> createQuery = cb.createQuery(Airplanes.class);
        Root<Airplanes> aerodromi = createQuery.from(Airplanes.class);
        Expression<String> icao = aerodromi.get("icao24");
        Expression<Integer> pocetak = aerodromi.get("firstseen");
        Expression<Integer> zavrsetak = aerodromi.get("lastseen");
        createQuery.where(cb.and(cb.equal(icao, icao24), cb.ge(pocetak, od)), cb.le(zavrsetak, kraj));
        return em.createQuery(createQuery)
                .getResultList();
    }
    
    @Override
    public List<Airplanes> dajLetoveAerodroma(String ident, long od, long kraj){
    CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airplanes> createQuery = cb.createQuery(Airplanes.class);
        Root<Airplanes> aerodromi = createQuery.from(Airplanes.class);
        Expression<String> icao = aerodromi.get("estdepartureairport").get("ident");
        Expression<Integer> pocetak = aerodromi.get("firstseen");
        Expression<Integer> zavrsetak = aerodromi.get("lastseen");
        createQuery.where(cb.and(cb.equal(icao, ident), cb.ge(pocetak, od)), cb.le(zavrsetak, kraj));
        return em.createQuery(createQuery)
                .getResultList();
    }

    @Override
    public List<Airplanes> dajLetoveAerodroma(String icao) {
         CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Airplanes> createQuery = cb.createQuery(Airplanes.class);
        Root<Airplanes> aerodromi = createQuery.from(Airplanes.class);
        Expression<String> ident = aerodromi.get("estdepartureairport").get("ident");
        createQuery.where(cb.equal(ident, icao));
        return em.createQuery(createQuery)
                .getResultList();
    }
}
