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
import org.foi.nwtis.hsostaric.ejb.eb.Korisnici;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
public class KorisniciFacade extends AbstractFacade<Korisnici> implements KorisniciFacadeLocal {

    @PersistenceContext(unitName = "org.foi.nwtis.hsostaric_hsostaric_modul_1_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public KorisniciFacade() {
        super(Korisnici.class);
    }
    
    @Override
    public Boolean autentificirajKorisnika(String username, String password) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Korisnici> createQuery = cb.createQuery(Korisnici.class);
        Root<Korisnici> korisnici = createQuery.from(Korisnici.class);
        Expression<String> kor_ime = korisnici.get("korIme");
        Expression<String> lozinka = korisnici.get("lozinka");
        createQuery.where(cb.and(cb.equal(kor_ime,username), cb.equal(lozinka, password)));
        return em.createQuery(createQuery)
                .getResultList().isEmpty();
    }

    @Override
    public List<Korisnici> vratiFiltriraneKorisnike(String username) {
       CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Korisnici> createQuery = cb.createQuery(Korisnici.class);
        Root<Korisnici> korisnici = createQuery.from(Korisnici.class);
        Expression<String> kor_ime = korisnici.get("korIme");
        createQuery.where(cb.like(kor_ime,username));
        return em.createQuery(createQuery)
                .getResultList();
    }

    @Override
    public Boolean provjeriZauzetostMaila(String email) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Korisnici> createQuery = cb.createQuery(Korisnici.class);
        Root<Korisnici> korisnici = createQuery.from(Korisnici.class);
        Expression<String> e_mail = korisnici.get("emailAdresa");
        createQuery.where(cb.like(e_mail,email));
        return em.createQuery(createQuery)
                .getResultList().isEmpty();
    }
    
}
