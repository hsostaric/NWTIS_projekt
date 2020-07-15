/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.filteri;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.foi.nwtis.hsostaric.ejb.eb.Dnevnik;
import org.foi.nwtis.hsostaric.ejb.eb.Korisnici;
import org.foi.nwtis.hsostaric.ejb.sb.DnevnikFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.hsostaric.web.zrna.PrijavaKorisnika;

/**
 * Klasa koja služi za kontrolu pristupa da neprijavljeni korisnici nemaju pravo
 * pristupa stranicama koje im nisu namjenjene i obrnuto za prijavljene
 * korisnike.
 *
 * @author Hrvoje-PC
 */
@WebFilter(filterName = "KontrolaPristupa", urlPatterns = {"/*"}, dispatcherTypes = {DispatcherType.REQUEST})
public class KontrolaPristupa implements Filter {

    private static final boolean debug = true;

    private FilterConfig filterConfig = null;

    @Inject
    PrijavaKorisnika prijavaKorisnika;
    @EJB
    KorisniciFacadeLocal korisniciFacadeLocal;
    @EJB
    DnevnikFacadeLocal dnevnikFacadeLocal;

    public KontrolaPristupa() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {

    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String url = req.getRequestURI();
        if (prijavaKorisnika.getPrijavljen() == false
                || prijavaKorisnika == null) {
            if (url.contains("pogled3.xhtml")
                    || url.contains("pogled4.xhtml")
                    || url.contains("pogled5.xhtml")
                    || url.contains("odjavaKorisnika.xhtml")) {
                resp.sendRedirect(req.getContextPath()
                        + "/pogled1.xhtml");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            filterPrijavljenogKorisnika(url, resp, req, chain, request, response);
        }
    }

    /**
     * Metoda za kontrolu pristupa prijavljenih korisnika.
     */
    private void filterPrijavljenogKorisnika(String url, HttpServletResponse resp,
            HttpServletRequest req, FilterChain chain, ServletRequest request, ServletResponse response)
            throws ServletException, IOException {
        if (url.contains("pogled1.xhtml")) {
            resp.sendRedirect(req.getContextPath() + "/index.xhtml");
        } else if (url.contains("odjavaKorisnika.xhtml")) {
            ocistiParametre(req);
            resp.sendRedirect(req.getContextPath() + "/pogled1.xhtml");
        } else {
            String adresa = req.getRequestURL().toString();
            if (adresa.contains("pogled1")
                    || adresa.contains("pogled3")
                    || adresa.contains("index")
                    || adresa.contains("pogled4")
                    || adresa.contains("pogled5")) {
                unosUDnevnik(chain, request, response, req, adresa);
            }
            else{
            chain.doFilter(request, response);
            }
        }
    }

    private void unosUDnevnik(FilterChain chain, ServletRequest request, ServletResponse response, 
            HttpServletRequest req, String adresa) throws IOException, UnknownHostException, ServletException {
        long pocetak = System.currentTimeMillis();
        chain.doFilter(request, response);
        long kraj = System.currentTimeMillis() - pocetak;
        String ip = req.getRemoteAddr();
        if (ip.equalsIgnoreCase("0:0:0:0:0:0:0:1")) {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ipAddress = inetAddress.getHostAddress();
            ip = ipAddress;
        }
        Korisnici korisnik = korisniciFacadeLocal.find(prijavaKorisnika.getKorisnickoIme());
        Dnevnik dnevnik = new Dnevnik();
        dnevnik.setIpAdresa(ip);
        dnevnik.setRadnja("Filter dnevnika");
        dnevnik.setUsername(korisnik);
        dnevnik.setTrajanjeObrade((int) kraj);
        dnevnik.setUrlAdresa(adresa);
        Date praviDatum=formatirajDatum();
        dnevnik.setVrijemePrijema(new java.sql.Date(praviDatum.getTime()));
        dnevnikFacadeLocal.create(dnevnik);
        korisnik.getDnevnikList().add(dnevnik);
        dnevnikFacadeLocal.edit(dnevnik);
    }

    private Date formatirajDatum() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        String datum = sdf.format(new Date());
        Date praviDatum;
        try {
            praviDatum = sdf.parse(datum);
            return praviDatum;
        } catch (ParseException ex) {
            System.out.println("Greska kod parsanja: " + ex);
        }
        return null;
    }

    private void ocistiParametre(HttpServletRequest req) {

        prijavaKorisnika.setKorisnickoIme("");
        prijavaKorisnika.setLozinka("");
        prijavaKorisnika.setPrijavljen(false);
    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            if (debug) {
                log("KontrolaPristupa:Initializing filter");
            }
        }
    }

    /**
     * Return a String representation of this object.
     */
    @Override
    public String toString() {
        if (filterConfig == null) {
            return ("KontrolaPristupa()");
        }
        StringBuffer sb = new StringBuffer("KontrolaPristupa(");
        sb.append(filterConfig);
        sb.append(")");
        return (sb.toString());
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
