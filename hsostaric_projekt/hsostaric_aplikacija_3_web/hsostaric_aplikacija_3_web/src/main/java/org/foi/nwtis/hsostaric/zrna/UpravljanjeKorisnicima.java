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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.ejb.eb.Korisnici;
import org.foi.nwtis.hsostaric.ejb.sb.Upravljac;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "upravljanjeKorisnicima")
@ViewScoped
public class UpravljanjeKorisnicima implements Serializable {

    @Inject
    PrijavaKorisnika prijavaKorisnika;
    @Inject
    ServletContext servletContext;
    @Getter
    @Setter
    private BP_Konfiguracija bpk;
    @Inject
    Upravljac upravljac;
    @Getter
    @Setter
    private String ime = "";
    @Getter
    @Setter
    private String prezime = "";
    @Getter
    @Setter
    private String emailAdresa = "";
    @Getter
    @Setter
    private String lozinka = "";
    @Getter
    @Setter
    private String lozinkaRegistracija = "";
    @Getter
    @Setter
    private String ponovljenaLozinka = "";
    @Getter
    @Setter
    private String porukaUspjeha = "";
    @Getter
    @Setter
    private List<Korisnici> listaKorisnika = new ArrayList<>();
    @Getter
    @Setter
    private String elementPretrage = "";
    @Getter
    @Setter
    private int brojStranica = 0;
    private int port = 0;
    private String posluzitelj = "";
    private Socket socket;
    private OutputStreamWriter outputStreamWriter;
    private OutputStream outputStream;
    private InputStream inputStream;
    private InputStreamReader inputStreamReader;
    @Getter
    @Setter
    private String korisnickoIme = "";
    @Getter
    @Setter
    private String korisnickoImeRegistracija = "";
    @Getter
    @Setter
    private String pogresnaPrijava = "";

    @Getter
    @Setter
    private String neuspjesnaRegistracija = "";

    public UpravljanjeKorisnicima() {

    }

    @PostConstruct
    public void init() {
        listaKorisnika = upravljac.dajSveKorisnikeIzBaze();
        bpk = (BP_Konfiguracija) servletContext
                .getAttribute("BP_Konfig");
        brojStranica = Integer
                .parseInt(bpk.getKonfig()
                        .dajPostavku("brojPoStranici"));

        port = Integer.parseInt(bpk.getKonfig().dajPostavku("serverSocket.port"));
        posluzitelj = bpk.getKonfig()
                .dajPostavku("serverSocket.posluzitelj");
    }

    public String formatirajDatum(Date datumPreuzimanja) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.
                format(datumPreuzimanja);
    }

    public String filtrirajKorisnike() {
        if (elementPretrage.equals("")) {
            elementPretrage = "%";
        }
        listaKorisnika = upravljac.dajFiltriraneKorisnike(elementPretrage);
        if (listaKorisnika.isEmpty()
                || listaKorisnika == null) {
            listaKorisnika = new ArrayList<>();
        }
        return "";
    }

    private String zapocniVezuSaPosluziteljem(String zahtjev) {
        try {
            socket = new Socket(posluzitelj, port);
            return radSazahtjevom(zahtjev);
        } catch (IOException ex) {
            return "ERR";
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

    public void postaviVarijable() {
        prijavaKorisnika.setKorisnickoIme(korisnickoIme);
        prijavaKorisnika.setPrijavljen(true);
        prijavaKorisnika.setLozinka(lozinka);
    }

    public String prijavaKorisnika() {
        String zahtjev = "KORISNIK " + korisnickoIme + "; LOZINKA " + lozinka + ";";
        String rezultat = zapocniVezuSaPosluziteljem(zahtjev);
        if (rezultat.contains("ERR")) {
            if (rezultat.contains("ERR 11")) {
                neuspjesnaRegistracija = "";
                pogresnaPrijava = "Unijeli ste pogrešne podatke !";
            } else {
                neuspjesnaRegistracija = "";
                pogresnaPrijava = "Nije moguce uspostaviti vezu sa posluziteljem";
            }
        } else {
            postaviVarijable();
            return "uspjesnaPrijava";
        }
        return "";
    }

    private boolean ispravnostMaila() {
        System.out.println("Email adresa je: " + emailAdresa);
        String regex = "^([a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6})$";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        Matcher m = pattern.matcher(emailAdresa);
        return m.find();
    }

    public String registrirajKorisnika() {
        Boolean nepostoji = true;
        if (!lozinkaRegistracija.equals(ponovljenaLozinka)) {
            neuspjesnaRegistracija = "Lozinke se moraju podudarati";
        } else {
            if (ispravnostMaila() == true) {
                nepostoji = upravljac.zauzetostEmaila(emailAdresa);
                if (nepostoji == false) {
                    neuspjesnaRegistracija = "Unijeli ste već postojeći mail !";
                } else {
                    vezaPremaPosluziteljuRegistracija();
                }
            } else {
                neuspjesnaRegistracija = "Email ne odgovara traženom izrazu !";
            }
        }
        return "";
    }

    private void vezaPremaPosluziteljuRegistracija() {
        String komanda;
        komanda = "KORISNIK " + korisnickoImeRegistracija + "; LOZINKA " + lozinkaRegistracija + "; DODAJ;";
        String rezultat = zapocniVezuSaPosluziteljem(komanda);
        if (rezultat.contains("ERR")) {
            if (rezultat.contains("ERR 12")) {
                neuspjesnaRegistracija = "Korisničko ime već postoji!";
            } else {
                neuspjesnaRegistracija = "Nije moguce uspostaviti vezu sa posluziteljem";
            }
        } else {
            upravljac.dodajKorisnikaLokalno(korisnickoImeRegistracija, lozinkaRegistracija, emailAdresa, ime, prezime);
            neuspjesnaRegistracija = "Uspješno ste obavili registraciju !";
            listaKorisnika = upravljac.dajSveKorisnikeIzBaze();
        }
    }

}
