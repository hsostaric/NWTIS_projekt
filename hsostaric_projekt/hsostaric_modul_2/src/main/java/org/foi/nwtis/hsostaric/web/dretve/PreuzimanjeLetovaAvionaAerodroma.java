package org.foi.nwtis.hsostaric.web.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import org.foi.nwtis.hsostaric.ejb.eb.Airplanes;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;
import org.foi.nwtis.hsostaric.ejb.eb.Myairportslog;
import org.foi.nwtis.hsostaric.ejb.eb.MyairportslogPK;
import org.foi.nwtis.hsostaric.ejb.sb.AirplanesFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.AirportsFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.KorisniciFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.MyairportslogFacadeLocal;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

/**
 * Dretva koja periodički preuzima podatke o letovima iz aerodroma korisnika
 *
 * @author Hrvoje Šoštarić
 */
@Stateless
public class PreuzimanjeLetovaAvionaAerodroma extends Thread {

    Boolean krajPreuzimanja = false;
    BP_Konfiguracija konf;
    OSKlijent oSKlijent;
    String korisnickoIme;
    String lozinka;
    Date pocetniDatum;
    Date zavrsniDatum;
    int ciklusPodatakaAerodroma;
    int pauzaIzmedjuAerodroma;
    List<AvionLeti> listaDolazaka;
    long dan = 86400000;
    String korisnickoPosluzitelj;
    String lozinkaPosluzitelj;
    boolean radi;
    String posluzitelj;
    int port;
    Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private InputStreamReader inputStreamReader;
    private OutputStreamWriter outputStreamWriter;
    private List<String> aerodromiKorisnika;
    private long trajanje;
    AirportsFacadeLocal airportsFacadeLocal;
    MyairportsFacadeLocal myairportsFacadeLocal;
    MyairportslogFacadeLocal myairportslogFacadeLocal;
    KorisniciFacadeLocal korisniciFacadeLocal;
    AirplanesFacadeLocal airplanesFacadeLocal;

    public PreuzimanjeLetovaAvionaAerodroma() {
    }

    public PreuzimanjeLetovaAvionaAerodroma(BP_Konfiguracija konf) {
        this.konf = konf;
    }

    public PreuzimanjeLetovaAvionaAerodroma(BP_Konfiguracija bpk, AirportsFacadeLocal airportsFacadeLocal,
            MyairportsFacadeLocal myairportsFacadeLocal, MyairportslogFacadeLocal myairportslogFacadeLocal, KorisniciFacadeLocal korisniciFacadeLocal,
            AirplanesFacadeLocal airplanesFacadeLocal) {
        this.konf = bpk;
        this.airportsFacadeLocal = airportsFacadeLocal;
        this.myairportsFacadeLocal = myairportsFacadeLocal;
        this.myairportslogFacadeLocal = myairportslogFacadeLocal;
        this.airplanesFacadeLocal = airplanesFacadeLocal;
    }

    @Override
    public void interrupt() {
        krajPreuzimanja = true;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        aerodromiKorisnika = new ArrayList<>();
        while (krajPreuzimanja == false) {
            if (pocetniDatum.compareTo(dajDanasnjiDatum()) <= 0) {
                aerodromiKorisnika = myairportsFacadeLocal.dajJedinstveneAerodrome();
                long poc = System.currentTimeMillis();
                boolean preuzimam = provjeriDostupnostPorta();
                if (preuzimam == true) {
                    for (String a : aerodromiKorisnika) {
                        preuzmiPolaskeAviona(a);
                    }
                    trajanje = System.currentTimeMillis() - poc;
                    try {
                        if ((ciklusPodatakaAerodroma - trajanje) < 0) {
                            sleep(0);
                        } else {
                            sleep(ciklusPodatakaAerodroma - trajanje);
                        }
                    } catch (InterruptedException ex) {
                        krajPreuzimanja = false;
                        System.out.println("Greška:" + ex);
                    }
                    azurirajPocetniDatumPreuzimanja();
                } else {
                    System.out.println("Ne preuzimam podatke.");
                    trajanje = System.currentTimeMillis() - poc;
                    try {
                        sleep(ciklusPodatakaAerodroma - trajanje);
                    } catch (InterruptedException ex) {
                        krajPreuzimanja = false;
                        System.out.println("Greska: " + ex);
                    }
                }
            }
            spavajDan();
        }
    }

    /**
     * Metoda koja dretvu stavlja u stanje spavaj 1 dan ukoliko je krajnje
     * vrijeme manje jednako današnjem danu
     */
    private void spavajDan() {
        if (pocetniDatum.compareTo(zavrsniDatum) == 0
                && zavrsniDatum.compareTo(dajDanasnjiDatum()) >= 0) {
            try {
                sleep(dan);
            } catch (InterruptedException ex) {
                System.out.println("Greska: " + ex);
            }
        }
    }

    /**
     * Metoda koja preuzima letove za primljeni aerodrom
     *
     * @param aerodrom aerodrom za koji se preuzimaju letovi
     */
    public synchronized void preuzmiPolaskeAviona(String aerodrom) {
        boolean prazan;
        prazan = myairportslogFacadeLocal.provjeripreuzetostDana(aerodrom, pocetniDatum);
       
        if (prazan == true) {
             System.out.println("Preuzimam za datum: " + pretvoriDatumUString(pocetniDatum));
            Date krajnjiDatum = uvecajDatumZaDan(pocetniDatum);
            listaDolazaka = oSKlijent.getDepartures(aerodrom, pocetniDatum
                    .getTime() / 1000, krajnjiDatum
                            .getTime() / 1000);
            if (listaDolazaka.size() > 0) {
                pohraniAvione();
            }
            evidentirajAerodrom(aerodrom, listaDolazaka.size());
            listaDolazaka.clear();
            try {
                sleep(pauzaIzmedjuAerodroma);
            } catch (InterruptedException ex) {
                System.out.println("Greska: " + ex);
            }
        }
    }

    public synchronized void evidentirajAerodrom(String ident, int brojDolazaka) {
        Airports airports = airportsFacadeLocal.find(ident);
        Myairportslog myairportslog = new Myairportslog();
        myairportslog.setAirports(airports);
        myairportslog.setBrojLetova(brojDolazaka);
        myairportslog.setStored(new Date());
        MyairportslogPK myairportslogPK = new MyairportslogPK(ident, pocetniDatum);
        myairportslog.setMyairportslogPK(myairportslogPK);
        myairportslogFacadeLocal.create(myairportslog);
        airports.getMyairportslogList().add(myairportslog);
        airportsFacadeLocal.edit(airports);

    }

    /**
     * Metoda koja poziva metodu iz baze podataka za pohranu aviona.
     */
    private synchronized void pohraniAvione() {
        for (AvionLeti avion : listaDolazaka) {
            if (avion.getEstArrivalAirport() != null) {
                Airplanes airplanes = new Airplanes();
                airplanes.setStored(new Date());
                airplanes.setLastseen(avion.getLastSeen());
                airplanes.setIcao24(avion.getIcao24());
                airplanes.setFirstseen(avion.getFirstSeen());
                airplanes.setEstdepartureairportvertdistance(avion.getEstDepartureAirportVertDistance());
                airplanes.setEstdepartureairporthorizdistance(avion.getEstDepartureAirportHorizDistance());
                Airports airports = airportsFacadeLocal.find(avion.getEstDepartureAirport());
                airplanes.setEstdepartureairport(airports);
                airplanes.setEstarrivalairportvertdistance(avion.getEstArrivalAirportVertDistance());
                airplanes.setEstarrivalairporthorizdistance(avion.getEstArrivalAirportHorizDistance());
                airplanes.setEstarrivalairport(avion.getEstArrivalAirport());
                airplanes.setDepartureairportcandidatescount(avion.getDepartureAirportCandidatesCount());
                airplanes.setCallsign(avion.getCallsign());
                airplanes.setArrivalairportcandidatescount(avion.getArrivalAirportCandidatesCount());
                airplanesFacadeLocal.create(airplanes);
                airports.getAirplanesList().add(airplanes);
                airportsFacadeLocal.edit(airports);
            }
        }
    }

    /**
     * Metoda koja ažurira dan za preuzimanje letova.
     */
    private synchronized void azurirajPocetniDatumPreuzimanja() {
        Calendar c = Calendar.getInstance();
        c.setTime(pocetniDatum);
        c.add(Calendar.DATE, 1);
        pocetniDatum = c.getTime();
    }

    @Override
    public synchronized void start() {
        korisnickoPosluzitelj = vratiPostavku("dretva.username");
        lozinkaPosluzitelj = vratiPostavku("dretva.lozinka");
        posluzitelj = vratiPostavku("serverSocket.posluzitelj");
        port = Integer.parseInt(vratiPostavku("serverSocket.port"));
        korisnickoIme = vratiPostavku("OpenSkyNetwork.korisnik");
        lozinka = vratiPostavku("OpenSkyNetwork.lozinka");
        ciklusPodatakaAerodroma = Integer.
                parseInt(vratiPostavku("preuzimanje.ciklus")) * 1000;
        pauzaIzmedjuAerodroma = Integer.
                parseInt(vratiPostavku("preuzimanje.pauza"));
        pocetniDatum = pretvoriStringuDate(vratiPostavku("preuzimanje.pocetak"));
        zavrsniDatum = pretvoriStringuDate(vratiPostavku("preuzimanje.kraj"));
        oSKlijent = new OSKlijent(korisnickoIme, lozinka);
        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * Metoda koja za primljeni ključ vraća postavku iz konfiguracijske datoteke
     *
     * @param kljuc ključ za koji se vraća vrijednost
     * @return
     */
    public synchronized String vratiPostavku(String kljuc) {
        return konf.getKonfig()
                .dajPostavku(kljuc);
    }

    /**
     * Metoda koja vraća današnji datum u formatu dd.MM.yyyy .
     */
    private synchronized Date dajDanasnjiDatum() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        String datum = dateFormat.format(date);
        return pretvoriStringuDate(datum);
    }

    private synchronized Date pretvoriDatumuDrugiFormat(Date date) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String datum = dateFormat.format(date);

            return dateFormat.parse(datum);
        } catch (ParseException ex) {
            System.out.println("Greska u formatiranju: " + ex);
        }
        return null;
    }

    /**
     * Metoda koja pretvara primljeni string u format Date.
     *
     * @param strDatum primljeni datum u obliku string
     * @return konvertirani datum iz stringa
     */
    public synchronized Date pretvoriStringuDate(String strDatum) {
        Date datum = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        try {
            datum = sdf.parse(strDatum);
        } catch (ParseException ex) {
            System.out.println("Greška: " + ex);
        }
        return datum;
    }

    public String pretvoriDatumUString(Date datum) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        return sdf.format(datum);
    }

    /**
     * Metoda koja uvećava primljeni datum za jedan dan.
     */
    private synchronized Date uvecajDatumZaDan(Date datum) {
        Calendar c = Calendar.getInstance();
        c.setTime(datum);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }

    private boolean provjeriDostupnostPorta() {
        try {
            String zahtjev = "";
            socket = new Socket(posluzitelj, port);
            zahtjev = "KORISNIK " + korisnickoPosluzitelj + "; LOZINKA " + lozinkaPosluzitelj + "; STANJE;";
            return radSazahtjevom(zahtjev);
        } catch (IOException ex) {
            System.out.println("Nedostupnost porta: " + ex);
            return false;
        }
    }

    private boolean radSazahtjevom(String zahtjev) throws IOException {
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
        return odgovor.contains("OK 11;");
    }
}
