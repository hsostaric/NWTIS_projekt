/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.foi.nwtis.hsostaric.ejb.eb.Airplanes;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;
import org.foi.nwtis.hsostaric.ejb.eb.Korisnici;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;
import org.foi.nwtis.hsostaric.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.DohvacanjePodataka;
import org.foi.nwtis.hsostaric.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
@WebService(serviceName = "Projekt")
public class Projekt {
    
    @EJB
    KorisniciFacadeLocal korisniciFacadeLocal;
    @EJB
    AirportsFacadeLocal airportsFacadeLocal;
    @EJB
    AirplanesFacadeLocal airplanesFacadeLocal;
    @Inject
    DohvacanjePodataka dohvacanjePodataka;
    @EJB
    MyairportsFacadeLocal myairportsFacadeLocal;

    /**
     * This is a sample web service operation
     *
     * @param korisnickoIme
     * @param lozinka
     * @return
     */
    @WebMethod(operationName = "autentificirajKorisnika")
    public Boolean autentificirajKorisnika(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka) {
        Boolean prazno = false;
        prazno = korisniciFacadeLocal.autentificirajKorisnika(korisnickoIme, lozinka);
        return prazno;
    }
    
    @WebMethod(operationName = "dohvatiAerodromePoImenu")
    public List<Aerodrom> dohvatiAerodromePoImenu(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "naziv") String naziv) throws UnknownHostException {
        Boolean prazno = true;
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        List<Aerodrom> lista = new ArrayList<>();
        List<Airports> airports = new ArrayList<>();
        prazno = korisniciFacadeLocal.autentificirajKorisnika(korisnickoIme, lozinka);
        if (prazno == false) {
            long pocetak = System.currentTimeMillis();
            airports = airportsFacadeLocal.findAirportbyName(naziv);
            if (!airports.isEmpty()) {
                dodajAerodromeIzBazeUKolekciju(airports, lista);
            }
            long kraj = System.currentTimeMillis();
            long trajanje = kraj - pocetak;
            dohvacanjePodataka.unesiUDnevnik(korisnickoIme, inetAddress.getHostName(),
                    "dohvatiAerodromePoImenu", inetAddress.getHostAddress(), new Date(), (int) trajanje);
        }
        return lista;
    }
    
    @WebMethod(operationName = "dohvatiAerodromeIzDrzave")
    public List<Aerodrom> dohvatiAerodromeIzDrzave(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "drzava") String drzava) throws UnknownHostException {
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        Boolean prazno = true;
        List<Aerodrom> lista = new ArrayList<>();
        List<Airports> airports = new ArrayList<>();
        prazno = korisniciFacadeLocal.autentificirajKorisnika(korisnickoIme, lozinka);
        if (prazno == false) {
            long pocetak = System.currentTimeMillis();
            airports = airportsFacadeLocal.findAirportsfromCountry(drzava);
            if (!airports.isEmpty()) {
                dodajAerodromeIzBazeUKolekciju(airports, lista);
            }
            long kraj = System.currentTimeMillis();
            long trajanje = kraj - pocetak;
            dohvacanjePodataka.unesiUDnevnik(korisnickoIme, inetAddress.getHostName(),
                    "dohvatiAerodromeIzDrzave", inetAddress.getHostAddress(), new Date(), (int) trajanje);
        }
        return lista;
    }
    
    @WebMethod(operationName = "dohvatiVlastiteAerodrome")
    public List<Aerodrom> dohvatiVlastiteAerodrome(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka) throws UnknownHostException {
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        Boolean prazno = true;
        Korisnici korisnik = null;
        List<Aerodrom> lista = new ArrayList<>();
        List<Airports> airports = new ArrayList<>();
        List<Myairports> mojiAerodromi = new ArrayList<>();
        prazno = korisniciFacadeLocal.autentificirajKorisnika(korisnickoIme, lozinka);
        if (prazno == false) {
            long pocetak = System.currentTimeMillis();
            mojiAerodromi = myairportsFacadeLocal.vratiKorisnikoveAerodrome(korisnickoIme);
            if (!mojiAerodromi.isEmpty()) {
                for (Myairports myairports : mojiAerodromi) {
                    airports.add(myairports.getIdent());
                }
                dodajAerodromeIzBazeUKolekciju(airports, lista);
            }
            long kraj = System.currentTimeMillis();
            long trajanje = kraj - pocetak;
            dohvacanjePodataka.unesiUDnevnik(korisnickoIme, inetAddress.getHostName(),
                    "dohvatiVlastiteAerodrome", inetAddress.getHostAddress(), new Date(), (int) trajanje);
        }
        return lista;
    }
    
    @WebMethod(operationName = "dohvatiPopisSvihLetovaAerodroma")
    public List<AvionLeti> dohvatiPopisSvihLetovaAerodroma(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "ICAO") String ICAO,
            @WebParam(name = "od") long od, @WebParam(name = "do") long kraj) throws UnknownHostException {
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        Boolean prazno = true;
        List<AvionLeti> lista = new ArrayList<>();
        List<Airplanes> listaAviona = new ArrayList<>();
        prazno = korisniciFacadeLocal.autentificirajKorisnika(korisnickoIme, lozinka);
        Airports airports = null;
        if (prazno == false) {
            long pocetak = System.currentTimeMillis();
            listaAviona = airplanesFacadeLocal.dajLetoveAerodroma(ICAO, od, kraj);
            if (!listaAviona.isEmpty()) {
                for (Airplanes airplanes : listaAviona) {
                    pohraniLetoveUListu(airplanes, od, kraj, ICAO, lista);
                }
            }
            long kraj1 = System.currentTimeMillis();
            long trajanje = kraj1 - pocetak;
            dohvacanjePodataka.unesiUDnevnik(korisnickoIme, inetAddress.getHostName(),
                    "dohvatiPopisSvihLetovaAerodroma", inetAddress.getHostAddress(), new Date(), (int) trajanje);
        }
        return lista;
    }
    
    @WebMethod(operationName = "dohvatiSveLetoveAviona")
    public List<AvionLeti> dohvatiSveLetoveAviona(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "ICAO24") String ICAO24,
            @WebParam(name = "od") long od, @WebParam(name = "do") long kraj) throws UnknownHostException {
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        Boolean prazno = true;
        List<Airplanes> popisAviona = new ArrayList<>();
        List<AvionLeti> listaLetova = new ArrayList<>();
        prazno = korisniciFacadeLocal.autentificirajKorisnika(korisnickoIme, lozinka);
        if (prazno == false) {
            long pocetak = System.currentTimeMillis();
            popisAviona = airplanesFacadeLocal.findLetoviAviona(ICAO24, od, kraj);
            if (!popisAviona.isEmpty()) {
                for (Airplanes airplanes : popisAviona) {
                    pohraniLetoveUListu(airplanes, od, kraj, ICAO24, listaLetova);
                }
            }
            long kraj1 = System.currentTimeMillis();
            long trajanje = kraj1 - pocetak;
            dohvacanjePodataka.unesiUDnevnik(korisnickoIme, inetAddress.getHostName(),
                    "dohvatiSveLetoveAviona", inetAddress.getHostAddress(), new Date(), (int) trajanje);
        }
        return listaLetova;
    }
    
    @WebMethod(operationName = "izracunajUdaljenostAerodroma")
    public int izracunajUdaljenostAerodroma(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "icao_1") String icao_1, @WebParam(name = "icao_2") String icao_2)
            throws UnknownHostException {
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        Airports airports1;
        Airports airports2;
        Boolean prazno = true;
        int udaljenost = 0;
        Lokacija lokacija1;
        Lokacija lokacija2;
        prazno = korisniciFacadeLocal.autentificirajKorisnika(korisnickoIme, lozinka);
        if (prazno == false) {
            long pocetak = System.currentTimeMillis();
            airports1 = airportsFacadeLocal.find(icao_1);
            airports2 = airportsFacadeLocal.find(icao_2);
            if (airports1 != null && airports2 != null) {
                if (!airports1.getIdent().equals(airports2.getIdent())) {
                    lokacija1 = new Lokacija();
                    lokacija2 = new Lokacija();
                    postaviParametreLokacije(airports1, airports2, lokacija1, lokacija2);
                    udaljenost = izracunajUdaljenost(lokacija1, lokacija2);
                }
            }
            long kraj1 = System.currentTimeMillis();
            long trajanje = kraj1 - pocetak;
            dohvacanjePodataka.unesiUDnevnik(korisnickoIme, inetAddress.getHostName(),
                    "izracunajUdaljenostAerodroma", inetAddress.getHostAddress(), new Date(), (int) trajanje);
        }
        return udaljenost;
    }
    
    @WebMethod(operationName = "vratiVlastiteAerodromeURasponu")
    public List<Aerodrom> vratiVlastiteAerodromeURasponu(@WebParam(name = "korisnickoIme") String korisnickoIme,
            @WebParam(name = "lozinka") String lozinka, @WebParam(name = "icao") String icao,
            @WebParam(name = "donjaGranica") int donjaGranica, @WebParam(name = "gornjaGranica") int gornjaGranica) throws UnknownHostException {
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        List<Aerodrom> listaAerodroma = new ArrayList<>();
        List<Airports> listaMojihAerodroma = new ArrayList<>();
        List<Myairports> mojiAerodromi = new ArrayList<>();
        Airports airport = null;
        boolean prazno = true;
        long udaljenost = 0;
        prazno = autentificirajKorisnika(korisnickoIme, lozinka);
        if (prazno == false) {
            long pocetak = System.currentTimeMillis();
            airport = airportsFacadeLocal.find(icao);
            if (airport != null) {
                postaviAerodromeUnutarRaspona(listaMojihAerodroma, korisnickoIme, lozinka,
                        airport, donjaGranica, gornjaGranica, listaAerodroma);
            }
            long kraj1 = System.currentTimeMillis();
            long trajanje = kraj1 - pocetak;
            dohvacanjePodataka.unesiUDnevnik(korisnickoIme, inetAddress.getHostName(),
                    "vratiVlastiteAerodromeURasponu", inetAddress.getHostAddress(), new Date(), (int) trajanje);
        }
        return listaAerodroma;
    }
    
    private void postaviAerodromeUnutarRaspona(List<Airports> listaMojihAerodroma, String korisnickoIme,
            String lozinka, Airports airport, int donjaGranica, int gornjaGranica, List<Aerodrom> listaAerodroma) throws UnknownHostException {
        
        List<Myairports> mojiAerodromi;
        int udaljenost = 0;
        mojiAerodromi = myairportsFacadeLocal.vratiKorisnikoveAerodrome(korisnickoIme);
        if (!mojiAerodromi.isEmpty()) {
            mojiAerodromi.forEach((ma) -> {
                listaMojihAerodroma.add(ma.getIdent());
            });
            for (Airports a : listaMojihAerodroma) {
                if (!a.getIdent().equals(airport.getIdent())) {
                    udaljenost = izracunajUdaljenostAerodroma(korisnickoIme, lozinka, airport.getIdent(), a.getIdent());
                    if (udaljenost >= donjaGranica && udaljenost <= gornjaGranica) {
                        Aerodrom aerodrom = new Aerodrom();
                        urediAerodrom(aerodrom, a);
                        listaAerodroma.add(aerodrom);
                    }
                }
            }
        }
    }
    
    private void urediAerodrom(Aerodrom aerodrom, Airports a) {
        aerodrom.setNaziv(a.getName());
        aerodrom.setIcao(a.getIdent());
        aerodrom.setDrzava(a.getIsoCountry());
        Lokacija lokacija = new Lokacija();
        String koridnata = a.getCoordinates();
        String polje[] = koridnata
                .split(",");
        lokacija.setLatitude(polje[1]
                .trim());
        lokacija.setLongitude(polje[0]
                .trim());
        aerodrom.setLokacija(lokacija);
    }
    
    private void postaviParametreLokacije(Airports airports1, Airports airports2, Lokacija lokacija1, Lokacija lokacija2) {
        String lokacijaprva[] = airports1
                .getCoordinates()
                .split(",");
        String lokacijadruga[] = airports2
                .getCoordinates()
                .split(",");
        lokacija1.setLatitude(lokacijaprva[1]
                .trim());
        lokacija1.setLongitude(lokacijaprva[0]
                .trim());
        lokacija2.setLatitude(lokacijadruga[1]
                .trim());
        lokacija2.setLongitude(lokacijadruga[0]
                .trim());
    }
    
    private int izracunajUdaljenost(Lokacija polaziste, Lokacija odrediste) {
        double lat1 = Double.parseDouble(polaziste.getLatitude());
        double lat2 = Double.parseDouble(odrediste.getLatitude());
        double lon1 = Double.parseDouble(polaziste.getLongitude());
        double lon2 = Double.parseDouble(odrediste.getLongitude());
        double theta = lon1 - lon2;
        double dist = Math.sin(Math.toRadians(lat1))
                * Math.sin(Math.toRadians(lat2))
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2))
                * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return (int) dist;
    }
    
    private void pohraniLetoveUListu(Airplanes airplanes, long od, long kraj, String ICAO, List<AvionLeti> lista) {
        if (airplanes.getFirstseen() >= od && airplanes.getLastseen() <= kraj) {
            AvionLeti avionLeti = new AvionLeti();
            avionLeti.setArrivalAirportCandidatesCount(airplanes.getArrivalairportcandidatescount());
            avionLeti.setCallsign(airplanes.getCallsign());
            avionLeti.setDepartureAirportCandidatesCount(airplanes.getDepartureairportcandidatescount());
            avionLeti.setEstArrivalAirport(airplanes.getEstarrivalairport());
            avionLeti.setEstArrivalAirportHorizDistance(airplanes.getEstarrivalairporthorizdistance());
            avionLeti.setEstArrivalAirportVertDistance(airplanes.getEstarrivalairportvertdistance());
            avionLeti.setEstDepartureAirport(airplanes.getEstdepartureairport().getIdent());
            avionLeti.setEstDepartureAirportHorizDistance(airplanes.getEstdepartureairporthorizdistance());
            avionLeti.setEstDepartureAirportVertDistance(airplanes.getEstdepartureairportvertdistance());
            avionLeti.setFirstSeen(airplanes.getFirstseen());
            avionLeti.setIcao24(airplanes.getIcao24());
            avionLeti.setLastSeen(airplanes.getLastseen());
            lista.add(avionLeti);
        }
    }
    
    private void dodajAerodromeIzBazeUKolekciju(List<Airports> airports, List<Aerodrom> lista) {
        for (Airports airport : airports) {
            Aerodrom aerodrom = new Aerodrom();
            urediAerodrom(aerodrom, airport);
            lista.add(aerodrom);
        }
    }
    
}
