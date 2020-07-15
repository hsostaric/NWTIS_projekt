/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.rest.serveri;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.foi.nwtis.hsostaric.ejb.eb.Airplanes;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;
import org.foi.nwtis.hsostaric.ejb.eb.Myairports;
import org.foi.nwtis.hsostaric.ejb.sb.Upravljac;
import org.foi.nwtis.hsostaric.server.Aerodrom;
import org.foi.nwtis.hsostaric.server.Lokacija;
import org.foi.nwtis.hsostaric.websocket.endpoint.WebSocketClient;
import org.foi.nwtis.hsostaric.ws.klijenti.Aplikacija_3WS;
import org.foi.nwtis.podaci.Odgovor;

/**
 * REST Web Service
 *
 * @author Hrvoje-PC
 */
@Path("/aerodromi")
@RequestScoped
public class ProjektResource {

    @Context
    private UriInfo context;
    @Inject
    Upravljac upravljac;

    Boolean stanje = false;

    public ProjektResource() {
    }

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodomeKorisnika(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) throws UnknownHostException {
        Boolean autentificiraj = false;
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        List<Aerodrom> listaMojihAerodroma = new ArrayList<>();
        autentificiraj = upravljac.autentificirajKorisnika(korisnik, lozinka);
        if (autentificiraj == true) {
            long pocetak = System.currentTimeMillis();
            Aplikacija_3WS aplikacija_3WS = new Aplikacija_3WS();
            listaMojihAerodroma = aplikacija_3WS.dajKorisnikoveAerodrome(korisnik, lozinka);
            long kraj = System.currentTimeMillis() - pocetak;
            upravljac.unesiUDnevnik(korisnik, "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi",
                    "Dohvat korisnikovih aerodroma", inetAddress.getHostAddress(), new Date(), (int) kraj);
            if (listaMojihAerodroma.isEmpty()
                    || listaMojihAerodroma == null) {
                return vratiOdgovor("40", "Greška prilikom dohvata mojih aerodroma", listaMojihAerodroma);
            }
            return vratiOdgovor("10", "OK", listaMojihAerodroma);
        }
        return vratiOdgovor("40", "Greška prilikom autentifikacije", listaMojihAerodroma);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajAerodromKorisniku(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @JsonProperty("icao") String icao) throws UnknownHostException {
        Boolean dodano = false;
        Boolean autoriziran = false;
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        autoriziran = upravljac.autentificirajKorisnika(korisnik, lozinka);
        if (autoriziran == true) {
            long pocetak = System.currentTimeMillis();
            JsonObject jobj = new Gson()
                    .fromJson(icao, JsonObject.class);
            String result = jobj.get("icao").getAsString();
            try {
                obaviPosaoPremaEndPointu(korisnik, result);
            } catch (URISyntaxException ex) {
                System.out.println("Greska: " + ex);
                return vratiOdgovor("40", "Greska kod dodavanja aerodroma!", new ArrayList<>());
            } catch (InterruptedException ex) {
                System.out.println("Greska: " + ex);
            }
            long kraj = System.currentTimeMillis() - pocetak;
            upravljac.unesiUDnevnik(korisnik, "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi",
                    "Unos novog aerodroma", inetAddress.getHostAddress(), new Date(), (int) kraj);
            if (stanje == true) {
                return vratiOdgovor("10", "Aerodrom je uspješno dodan korisniku.", new ArrayList<>());
            }
        }
        return vratiOdgovor("40", "Aerodrom nije dodan na ENDPOINT!", new ArrayList<>());
    }

    private void obaviPosaoPremaEndPointu(String korisnik, String result) throws URISyntaxException, InterruptedException {
        final WebSocketClient webSocketClient = new WebSocketClient(
                new URI("ws://localhost:8084/hsostaric_aplikacija_2_web/dodavanjeAerodroma"));
        webSocketClient.addMessageHandler((String message) -> {
            System.out.println("Stigla poruka od servera: " + message);
            stanje = message.contains("OK");
        });
        webSocketClient.sendMessage(korisnik + ";" + result);
        Thread.sleep(2000);
    }

    @Path("{icao}")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response vratiPodatkeKorisnikovogAerodroma(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao) throws UnknownHostException {
        Boolean postoji = upravljac.autentificirajKorisnika(korisnik, lozinka);
        Airports airports = null;
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        List<Aerodrom> lista = new ArrayList<>();
        if (postoji == true) {
            long pocetak = System.currentTimeMillis();
            airports = upravljac.dohvatiMojAerodrom(korisnik, icao);
            long kraj = System.currentTimeMillis() - pocetak;
            upravljac.unesiUDnevnik(korisnik, "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi/" + icao, "Dohvat podataka za aerodrom",
                    inetAddress.getHostAddress(), new Date(), (int) kraj);
            if (airports != null) {
                lista = vratiAerodrom(airports);
                return vratiOdgovor("10", "OK", lista);
            }
        }
        return vratiOdgovor("40", "Greška prilikom dohvata mojih aerodroma", lista);
    }

    @Path("{icao}")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response obrisiPracenjeAerodroma(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao) throws UnknownHostException {
        Boolean postoji = upravljac.autentificirajKorisnika(korisnik, lozinka);
        InetAddress inetAddress;
        System.out.println("Korisnik: "+korisnik);
        System.out.println("Lozinka: "+lozinka);
        System.out.println("ICAO: "+icao);
        inetAddress = InetAddress.getLocalHost();
        List<Myairports> airports = new ArrayList<>();
        List<Airplanes> listaLetova = new ArrayList<>();
        if (postoji == true) {
            System.out.println("Autoriziran.");
            long pocetak = System.currentTimeMillis();
            airports = upravljac.vratiZapisIzMojihAerodroma(korisnik, icao);
            if (!airports.isEmpty()) {
                listaLetova = upravljac.dohvatiLetoveAerodroma(icao);
                if (listaLetova.isEmpty()) {
                    upravljac.prestaniPratitiAerodrom(airports.get(0));
                    long kraj = System.currentTimeMillis() - pocetak;
                    upravljac.unesiUDnevnik(korisnik, "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi/" + icao,
                            "Brisanje korisnikovoh aerodroma", inetAddress.getHostAddress(), new Date(), (int) kraj);
                    return vratiOdgovor("10", "OK", new ArrayList());
                }
                long kraj = System.currentTimeMillis() - pocetak;
                upravljac.unesiUDnevnik(korisnik, "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi/" + icao,
                        "Brisanje korisnikovoh aerodroma", inetAddress.getHostAddress(), new Date(), (int) kraj);
            }
        }
        return vratiOdgovor("40", "Aerodrom nije obrisan", new ArrayList());
    }

    @Path("{icao}/avioni")
    @DELETE
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response obrisiLetoveAerodroma(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao) throws UnknownHostException {
        List<Myairports> airports = new ArrayList<>();
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        List<Airplanes> listaLetova = new ArrayList<>();
        Boolean postoji = upravljac.autentificirajKorisnika(korisnik, lozinka);
        if (postoji == true) {
            long pocetak = System.currentTimeMillis();
            airports = upravljac.vratiZapisIzMojihAerodroma(korisnik, icao);
            System.out.println("Identifikator: "+airports.get(0).getIdent().getIdent());
            if (!airports.isEmpty()) {
                listaLetova = upravljac.dohvatiLetoveAerodroma(icao);
                System.out.println("Lista letova size: "+listaLetova.size());
                if (!listaLetova.isEmpty()) {
                    upravljac.obrisiLetoveAviona(listaLetova);
                    long kraj = System.currentTimeMillis() - pocetak;
                    upravljac.unesiUDnevnik(korisnik, "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi/" + icao + "/avioni",
                            "Brisanje letova aerodroma", inetAddress.getHostAddress(), new Date(), (int) kraj);
                    return vratiOdgovor("10", "OK", new ArrayList<>());
                }
            }
            long kraj = System.currentTimeMillis() - pocetak;
            upravljac.unesiUDnevnik(korisnik, "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi/" + icao + "/avioni",
                    "Brisanje letova aerodroma", inetAddress.getHostAddress(), new Date(), (int) kraj);
        }
        return vratiOdgovor("40", "Brisanje letova nije obavljeno", new ArrayList<>());
    }

    @Path("/svi")
    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrome(
            @HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("naziv") String naziv,
            @QueryParam("drzava") String drzava) throws UnknownHostException {
        InetAddress inetAddress;
        inetAddress = InetAddress.getLocalHost();
        List<Aerodrom> lista = null;
        Aplikacija_3WS aplikacija_3WS = new Aplikacija_3WS();
        Boolean autentificiran = false;
        autentificiran = upravljac.autentificirajKorisnika(korisnik, lozinka);
        if (autentificiran == true) {
            lista = obradiDohvatAerodromaPremaDrzavinazivu(naziv, aplikacija_3WS, korisnik, lozinka, drzava, inetAddress);

            if (lista == null
                    || lista.isEmpty()) {
                return vratiOdgovor("40", "Došlo je do problema prilikom pretraživanja/filtriranja sadrzaja",
                        new ArrayList());
            }
            return vratiOdgovor("10", "OK", lista);
        }
        return vratiOdgovor("40", "Pogrešni podaci za prijavu",
                new ArrayList());
    }

    private List<Aerodrom> obradiDohvatAerodromaPremaDrzavinazivu(String naziv, Aplikacija_3WS aplikacija_3WS, String korisnik, String lozinka, String drzava, InetAddress inetAddress) {
        List<Aerodrom> lista;
        String putanja;
        long pocetak = System.currentTimeMillis();
        if (naziv != null && !naziv.isEmpty()) {
            lista = aplikacija_3WS.dajAerodromeNaziv(korisnik, lozinka, naziv);
            putanja = "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi/svi?naziv=" + naziv;
        } else if (drzava != null && !drzava.isEmpty()) {
            lista = aplikacija_3WS.vratiAerodromeDrzave(korisnik, lozinka, drzava);
            putanja = "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi/svi?drzava=" + drzava;
        } else {
            lista = aplikacija_3WS.dajAerodromeNaziv(korisnik, lozinka, "%");
            putanja = "http://localhost:8084/hsostaric_aplikacija_3_web/webresources/aerodromi/svi?naziv=%";
        }
        long kraj = System.currentTimeMillis() - pocetak;
        upravljac.unesiUDnevnik(korisnik, putanja, "Dohvat aerodroma prema drzavi ili nazivu.", inetAddress.getHostAddress(), new Date(), (int) kraj);
        return lista;
    }

    private List<Aerodrom> vratiAerodrom(Airports airports) {
        Aerodrom aerodrom = new Aerodrom();
        aerodrom.setDrzava(airports.getIsoCountry());
        aerodrom.setIcao(airports.getIdent());
        aerodrom.setNaziv(airports.getName());
        String lokacija1[] = airports
                .getCoordinates()
                .split(",");
        Lokacija lokacija = new Lokacija();
        lokacija.setLatitude(lokacija1[1]
                .trim());
        lokacija.setLongitude(lokacija1[0]
                .trim());
        aerodrom.setLokacija(lokacija);
        List<Aerodrom> lista = new ArrayList<>();
        lista.add(aerodrom);
        return lista;
    }

    public Response vratiOdgovor(String status, String poruka, List<Aerodrom> kolekcija) {
        List<org.foi.nwtis.podaci.Aerodrom> lista = new ArrayList<>();
        if (!kolekcija.isEmpty()) {
            for (Aerodrom aerodrom : kolekcija) {
                org.foi.nwtis.podaci.Aerodrom a = new org.foi.nwtis.podaci.Aerodrom();
                a.setNaziv(aerodrom.getNaziv());
                a.setDrzava(aerodrom.getDrzava());
                org.foi.nwtis.rest.podaci.Lokacija lokacija = new org.foi.nwtis.rest.podaci.Lokacija();
                lokacija.setLatitude(aerodrom.getLokacija().getLatitude());
                lokacija.setLongitude(aerodrom.getLokacija().getLongitude());
                a.setLokacija(lokacija);
                a.setIcao(aerodrom.getIcao());
                lista.add(a);
            }
        }
        Odgovor odgovor = new Odgovor();
        odgovor.setOdgovor(lista.toArray());
        odgovor.setStatus(status);
        odgovor.setPoruka(poruka);
        return Response
                .ok(odgovor)
                .status(200)
                .build();
    }
}
