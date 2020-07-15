/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.Date;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.hsostaric.ejb.eb.Myairportslog;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
public class MyairportslogFacade extends AbstractFacade<Myairportslog> implements MyairportslogFacadeLocal {

    @PersistenceContext(unitName = "org.foi.nwtis.hsostaric_hsostaric_modul_1_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportslogFacade() {
        super(Myairportslog.class);
    }
    
    @Override
    public Boolean provjeripreuzetostDana(String ident, Date datum) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Myairportslog> createQuery = cb.createQuery(Myairportslog.class);
        Root<Myairportslog> zapis = createQuery.from(Myairportslog.class);
        Expression<Date> stored = zapis.get("myairportslogPK").get("flightdate");
        Expression<String> icao = zapis.get("airports").get("ident");
        createQuery.where(cb.and(cb.equal(stored,datum), cb.equal(icao, ident)));
        return em.createQuery(createQuery)
                .getResultList().isEmpty();
    }

    
}
