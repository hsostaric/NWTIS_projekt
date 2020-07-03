/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zrna;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.ejb.sb.Upravljac;
import org.foi.nwtis.hsostaric.konfiguracije.Konfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "upravljanjeKomandama")
@ViewScoped
public class UpravljanjeKomandama implements Serializable {

    @Inject
    ServletContext servletContext;
    @Inject
    PrijavaKorisnika prijavaKorisnika;

    @Getter
    @Setter
    private String komandaZaSlanje = "";

    @Getter
    @Setter
    private String odgovorServera = "";

    private Socket socket;

    private int port = 0;
    private String posluzitelj = "";
    private BP_Konfiguracija bpk;
    private Konfiguracija konf;

    private InputStreamReader inputStreamReader;
    private InputStream inputStream;
    private OutputStream outputStream;
    private OutputStreamWriter outputStreamWriter;
    @Getter
    @Setter
    private String korisnickoGrupa = "";
    @Getter
    @Setter
    private String lozinkaGrupa = "";

    @Getter
    @Setter
    private String komandaZaSlanjeGrupa = "";

    @Getter
    @Setter
    private String odgovorServeraGrupa = "";
    @Inject
    Upravljac upravljac;

    public UpravljanjeKomandama() {
    }

    @PostConstruct
    public void init() {
        bpk = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
        konf = bpk.getKonfig();
        port = Integer.parseInt(konf
                .dajPostavku("serverSocket.port"));
        posluzitelj = konf.dajPostavku("serverSocket.posluzitelj");

    }

    private String zapocniVezuSaPosluziteljem(String zahtjev) {
        try {
            socket = new Socket(posluzitelj, port);
            return radSazahtjevom(zahtjev);
        } catch (IOException ex) {
            return "ERR, neuspješna veza sa poslužiteljem";
        }
    }

    private String radSazahtjevom(String zahtjev) throws IOException {
        StringBuilder sb = new StringBuilder();
        int red = 0;
        String odgovor = "";
        System.out.println("Zahtjev: " + zahtjev);
        outputStream = socket.getOutputStream();
        outputStreamWriter = new OutputStreamWriter(outputStream, "UTF-8");
        outputStreamWriter.write(zahtjev);
        outputStreamWriter.flush();
        socket.shutdownOutput();
        inputStream = socket.getInputStream();
        inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        while ((red = inputStreamReader.read()) != -1) {
            sb.append((char) red);
        }
        odgovor = sb.toString();
        System.out.println("Primljen odgovor je: " + odgovor);
        socket.shutdownInput();
        socket.close();
        return odgovor;
    }

    public String provjeriStanjePosluzitelja() {
        komandaZaSlanje = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; STANJE;";
        odgovorServera = zapocniVezuSaPosluziteljem(komandaZaSlanje);
        return "";
    }

    public String nastaviSaRadom() {
        komandaZaSlanje = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; RADI;";
        odgovorServera = zapocniVezuSaPosluziteljem(komandaZaSlanje);
        return "";
    }

    public String prekiniRad() {
        komandaZaSlanje = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; KRAJ;";
        odgovorServera = zapocniVezuSaPosluziteljem(komandaZaSlanje);
        return "";
    }

    public String staviUPauzu() {
        komandaZaSlanje = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; PAUZA;";
        odgovorServera = zapocniVezuSaPosluziteljem(komandaZaSlanje);
        return "";
    }

    public String registrirajGrupu() {
        komandaZaSlanjeGrupa = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; GRUPA PRIJAVI;";
        odgovorServeraGrupa = zapocniVezuSaPosluziteljem(komandaZaSlanjeGrupa);
        return "";
    }

    public String odjaviGrupu() {
        komandaZaSlanjeGrupa = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; GRUPA ODJAVI;";
          odgovorServeraGrupa = zapocniVezuSaPosluziteljem(komandaZaSlanjeGrupa);
        return "";
    }

    public String aktivirajGrupu() {
        komandaZaSlanjeGrupa = "KORISNIK " + prijavaKorisnika.getKorisnickoIme()+ "; LOZINKA " + prijavaKorisnika.getLozinka() + "; GRUPA AKTIVIRAJ;";
         odgovorServeraGrupa = zapocniVezuSaPosluziteljem(komandaZaSlanjeGrupa);
        return "";
    }

    public String blokirajGrupu() {
        komandaZaSlanjeGrupa = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; GRUPA BLOKIRAJ;";
          odgovorServeraGrupa = zapocniVezuSaPosluziteljem(komandaZaSlanjeGrupa);
        return "";
    }

    public String grupaStanje() {
        komandaZaSlanjeGrupa = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; GRUPA STANJE;";
          odgovorServeraGrupa = zapocniVezuSaPosluziteljem(komandaZaSlanjeGrupa);
        return "";
    }

    public String grupaAerodromi() {
        StringBuilder sb = new StringBuilder();
        komandaZaSlanjeGrupa = "KORISNIK " + prijavaKorisnika.getKorisnickoIme() + "; LOZINKA " + prijavaKorisnika.getLozinka() + "; GRUPA AERODROMI ";
        List<String> lista = new ArrayList<>();
        lista = upravljac.vratiKorisnikoveAerodrome(prijavaKorisnika
                .getKorisnickoIme());
        if (!lista.isEmpty()) {
            for (String icao : lista) {
                sb.append(" ").append(icao).append(",");
            }
            String nastavak = sb.toString();
            if (nastavak.endsWith(",")) {
                nastavak = nastavak.substring(0, nastavak.length() - 1).trim() + ";";
                komandaZaSlanjeGrupa += nastavak;
                odgovorServeraGrupa = zapocniVezuSaPosluziteljem(komandaZaSlanjeGrupa);
            }
        } else {
            komandaZaSlanjeGrupa = "Nemožete izvršiti radnju jer ne pratite nikakve aerodrome!";
        }
        return "";
    }
}
