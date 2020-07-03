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
import org.foi.nwtis.hsostaric.ejb.eb.Dnevnik;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
public class DnevnikFacade extends AbstractFacade<Dnevnik> implements DnevnikFacadeLocal {

    @PersistenceContext(unitName = "org.foi.nwtis.hsostaric_hsostaric_modul_1_ejb_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DnevnikFacade() {
        super(Dnevnik.class);
    }

    @Override
    public List<Dnevnik> vratiKorisnikoveZapise(String username) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Dnevnik> createQuery = cb.createQuery(Dnevnik.class);
        Root<Dnevnik> zapis = createQuery.from(Dnevnik.class);
        Expression<String> korisnicko = zapis.get("username").get("korIme");
        createQuery.where(cb.equal(korisnicko, username));
        return em.createQuery(createQuery)
                .getResultList();
    }
    
}
