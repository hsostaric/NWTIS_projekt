/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;
import org.foi.nwtis.hsostaric.ejb.eb.Myairportslog;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
public class MyairportsFacade extends AbstractFacade<Myairports> implements MyairportsFacadeLocal {

    @PersistenceContext(unitName = "org.foi.nwtis.hsostaric_hsostaric_modul_1_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MyairportsFacade() {
        super(Myairports.class);
    }

    @Override
    public List<String> dajJedinstveneAerodrome() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> createQuery = cb.createQuery(String.class);
        Root<Myairports> identifikatori = createQuery.from(Myairports.class);
        createQuery.select(identifikatori.get("ident")
                .get("ident")).distinct(true);
        return em.createQuery(createQuery)
                .getResultList();
    }

    @Override
    public int brojAerodromaKojePratimo() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> createQuery = cb.createQuery(String.class);
        Root<Myairports> identifikatori = createQuery.from(Myairports.class);
        createQuery.select(identifikatori.get("ident")
                .get("ident")).distinct(true);
        return em.createQuery(createQuery)
                .getResultList().size();
    }

    @Override
    public Boolean pracenjeAerodroma(String icao) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Myairports> createQuery = cb.createQuery(Myairports.class);
        Root<Myairports> zapis = createQuery.from(Myairports.class);
        Expression<String> aero = zapis.get("ident").get("ident");
        createQuery.where(cb.equal(aero, icao));
        return em.createQuery(createQuery)
                .getResultList().isEmpty();
    }

    @Override
    public Myairports vratiZadnjiUnos() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Myairports> createQuery = cb.createQuery(Myairports.class);
        Root<Myairports> zapis = createQuery.from(Myairports.class);
        Expression<Date> datum = zapis.get("stored");
        createQuery.orderBy(cb.desc(datum));
        return em.createQuery(createQuery)
                .getResultList().get(0);
    }

    @Override
    public List<Myairports> vratiKorisnikoveAerodrome(String korisnickoIme) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Myairports> createQuery = cb.createQuery(Myairports.class);
        Root<Myairports> zapis = createQuery.from(Myairports.class);
        Expression<String> username = zapis.get("username").get("korIme");
        createQuery.where(cb.equal(username, korisnickoIme));
        return em.createQuery(createQuery)
                .getResultList();

    }

    @Override
    public List<Myairports> vratiKorisnikovAerodrom(String korisnickoIme, String icao) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Myairports> createQuery = cb.createQuery(Myairports.class);
        Root<Myairports> zapis = createQuery.from(Myairports.class);
        Expression<String> username = zapis.get("username").get("korIme");
          Expression<String> ident = zapis.get("ident").get("ident");
        createQuery.where(cb.and(cb.equal(username, korisnickoIme), cb.equal(ident, icao)));
        return em.createQuery(createQuery)
                .getResultList();
    }
}
