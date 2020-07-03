/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.dkermek.ws.serveri.StatusKorisnika;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.hsostaric.podaci.Korisnik;
import org.foi.nwtis.hsostaric.podaci.KorisnikDAO;
import org.foi.nwtis.hsostaric.ws.klijenti.Aerodromi_WS;

/**
 *
 * @author Hrvoje-PC
 */
public class ObradaZahtjeva extends Thread {

    private Socket veza;
    private InputStream inputStream;
    private OutputStream outputStream;
    private InputStreamReader streamReader;
    private OutputStreamWriter streamWriter;
    private StringBuilder stringBuilder;
    private BP_Konfiguracija bpk;
    int bajt;
    private KorisnikDAO korisnikDAO;
    private String korisnickoGrupa;
    private String lozinkaGrupa;

    public ObradaZahtjeva(Socket veza, BP_Konfiguracija bpk) {
        this.veza = veza;
        this.bpk = bpk;
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        System.out.println("inicijaliziran zahtjev.");
        obradaPodataka();
    }

    @Override
    public synchronized void start() {
        System.out.println("Zahtjev start.");
        korisnickoGrupa = bpk.getKonfig()
                .dajPostavku("grupa.username");
        lozinkaGrupa = bpk.getKonfig()
                .dajPostavku("grupa.password");

        super.start(); //To change body of generated methods, choose Tools | Templates.
    }

    private void obradaPodataka() {
        try {
            System.out.println("Idem u obradu zahtjeva.");
            String zahtjev = "";
            stringBuilder = new StringBuilder();
            inputStream = veza.getInputStream();
            streamReader = new InputStreamReader(inputStream, "UTF-8");
            while ((bajt = streamReader.read()) != -1) {
                stringBuilder.append((char) bajt);
            }
            veza.shutdownInput();
            zahtjev = stringBuilder.toString();
            System.out.println("Dobiven zahtjev: " + zahtjev);
            outputStream = veza.getOutputStream();
            streamWriter = new OutputStreamWriter(outputStream,
                    "UTF-8");
            analizaZahtjeva(zahtjev);
        } catch (IOException ex) {
            System.out.println("Greška: " + ex);
        }
    }

    private int ispitajVrijednost(String zahtjev) {
        String regexPosluzitelj = "^(KORISNIK [\\w]{3,10}); (LOZINKA [\\w\\#\\-\\!]{3,15});( DODAJ;| PAUZA;| RADI;| KRAJ;| STANJE;)?$";
        String regexGrupa = "^(KORISNIK [\\w]{3,10}); (LOZINKA [\\w\\#\\-\\!]{3,15}); "
                + "GRUPA (PRIJAVI;|ODJAVI;|AKTIVIRAJ;|BLOKIRAJ;|STANJE;|(AERODROMI (([A-Z\\-]{3,9}, )*[A-Z\\-]){3,9});)?$";
        if (provjeriRegex(regexPosluzitelj, zahtjev)) {
            return 0;
        } else if (provjeriRegex(regexGrupa, zahtjev)) {
            return 1;
        }
        return 2;
    }

    private boolean provjeriRegex(String izraz, String zahtjev) {
        Pattern pattern = Pattern.compile(izraz);
        Matcher m = pattern.matcher(zahtjev);
        return m.find();
    }

    private void analizaZahtjeva(String zahtjev) {
        int rezultat = ispitajVrijednost(zahtjev);
        String poruka = "";
        try {
            switch (rezultat) {
                case 0:
                    poruka = obradiKomanduPosluzitelja(zahtjev);
                    break;
                case 1:
                    poruka = obradiKomanduGrupe(zahtjev);
                    break;
                default:
                    poruka = "Greska kod posluzitelja;";
                    break;
            }
            vratiKorisnikuPoruku(poruka);
        } catch (IOException ex) {
            System.out.println("Greška: " + ex);
        }
    }

    private synchronized void vratiKorisnikuPoruku(String poruka) throws IOException {
        System.out.println("Vraćam poruku: " + poruka);
        streamWriter.write(poruka);
        streamWriter.flush();
        veza.shutdownOutput();
        veza.close();
    }
//TODO modificiraj autentifikaciju korisnika na poslužitelju

    private synchronized String obradiKomanduPosluzitelja(String zahtjev) {
        String polje[] = zahtjev.split(";");
        String username = polje[0]
                .trim().
                substring(8,
                        polje[0].length());
        String lozinka = polje[1]
                .substring(polje[1].indexOf("LOZINKA") + 8,
                        polje[1].length()).trim();

        korisnikDAO = new KorisnikDAO(bpk);
        boolean postoji = korisnikDAO.AutentificirajKorisnika(username.trim(), lozinka);
        if (postoji == true) {
            if (polje.length > 2) {
                return obradaDodatnihNaredbiPosluzitelja(username.trim(), lozinka, zahtjev);
            }
            return "OK 10;";
        } else {
            if (zahtjev.contains("DODAJ")) {
                Korisnik korisnik = new Korisnik(username.trim(), lozinka.trim());
                return obradiNaredbuDodaj(korisnik);
            }
        }
        return "ERR 11;";
    }

    //TODO modificiraj autentifikaciju korisnika u grupi
    private synchronized String obradiKomanduGrupe(String zahtjev) {
        String polje[] = zahtjev.split(";");
        String username = polje[0]
                .trim().
                substring(8,
                        polje[0].length());
        String lozinka = polje[1]
                .substring(polje[1].indexOf("LOZINKA") + 8,
                        polje[1].length()).trim();

        korisnikDAO = new KorisnikDAO(bpk);
        boolean postoji = korisnikDAO.AutentificirajKorisnika(username.trim(), lozinka.trim());
        if (postoji == true) {
            if (polje.length > 2) {
                return obradaDodatnihNaredbiGrupe(username.trim(), lozinka.trim(), zahtjev);
            }
            return "OK 10;";
        }
        return "ERR 11;";
    }

    private synchronized String obradaDodatnihNaredbiPosluzitelja(String username, String lozinka, String parametar) {
        Korisnik korisnik = new Korisnik(username, lozinka);
        if (parametar.contains("PAUZA;")) {
            return obradiNaredbuPauza();
        } else if (parametar.contains("DODAJ;")) {
            return obradiNaredbuDodaj(korisnik);
        } else if (parametar.contains("RADI;")) {
            return obradiNaredbuRadi();
        } else if (parametar.contains("KRAJ")) {
            return ObradiNaredbuKraj();
        } else {
            return obradiNaredbuStanje();
        }
    }

    private synchronized String obradaDodatnihNaredbiGrupe(String username, String lozinka, String parametar) {
        Korisnik korisnik = new Korisnik(korisnickoGrupa, lozinkaGrupa);
        if (parametar.contains("PRIJAVI;")) {
            return obradiNaredbuGrupePrijavi(korisnik, username, lozinka);
        } else if (parametar.contains("ODJAVI;")) {
            return obradiNaredbuGrupeOdjavi(korisnik, username, lozinka);
        } else if (parametar.contains("AKTIVIRAJ;")) {
            return obradiNaredbuGrupeAktiviraj(korisnik, username, lozinka);
        } else if (parametar.contains("BLOKIRAJ;")) {
            return obradiNaredbuGrupeBlokiraj(korisnik, username, lozinka);
        } else if (parametar.contains("STANJE;")) {
            return obradiNaredbuGrupeStanje(korisnik, username, lozinka);
        } else {
          return obradiNaredbuGrupeAerodromi(korisnik, parametar, username, lozinka);
        }
      
    }

    private synchronized String obradiNaredbuDodaj(Korisnik korisnik) {
        korisnikDAO = new KorisnikDAO(bpk);
        boolean postoji = korisnikDAO.provjeriPostojanostKorisnickog(korisnik);
        if (postoji == true) {
            return "ERR 12";
        } else {
            boolean dodan = korisnikDAO.registrirajKorisnika(korisnik);
            if (dodan == true) {
                return "OK 10;";
            }
        }
        return "ERR 12";
    }

    private synchronized String obradiNaredbuPauza() {
        if (GlavnaDretva.pauza == true) {
            return "ERR 13";
        }
        GlavnaDretva.pauza = true;
        return "OK 10;";
    }

    private synchronized String obradiNaredbuRadi() {
        if (GlavnaDretva.pauza == false) {
            return "ERR 14;";
        }
        GlavnaDretva.pauza = false;
        return "OK 10;";
    }

    private synchronized String obradiNaredbuStanje() {
        if (GlavnaDretva.pauza == false) {
            return "OK 11;";
        }
        return "OK 12;";
    }

    private synchronized String ObradiNaredbuKraj() {
        GlavnaDretva.pauza = true;
        GlavnaDretva.radi = false;
        return "OK 10;";
    }

    private synchronized String obradiNaredbuGrupePrijavi(Korisnik korisnik, String username, String lozinka) {
        Aerodromi_WS aerodromi_WS = new Aerodromi_WS();
        StatusKorisnika statusKorisnika = aerodromi_WS.dajStatusKorisnika(korisnik.getUsername(),
                korisnik.getPassword());
        if (statusKorisnika == StatusKorisnika.NEPOSTOJI
                || statusKorisnika == StatusKorisnika.PASIVAN
                || statusKorisnika == StatusKorisnika.DEREGISTRIRAN) {
            aerodromi_WS.registrirajGrupu(korisnik.getUsername(),
                    korisnik.getPassword());
            return "OK 20;";
        } else {
            return "ERR 20;";
        }
    }

    private synchronized String obradiNaredbuGrupeOdjavi(Korisnik korisnik, String username, String lozinka) {
        Aerodromi_WS aerodromi_WS = new Aerodromi_WS();
        StatusKorisnika statusKorisnika = aerodromi_WS.dajStatusKorisnika(korisnik.getUsername(),
                korisnik.getPassword());
        if (statusKorisnika == StatusKorisnika.REGISTRIRAN || statusKorisnika == StatusKorisnika.BLOKIRAN) {
            aerodromi_WS.odjaviGrupu(korisnik.getUsername(),
                    korisnik.getPassword());
            return "OK 20;";
        } else {
            return "ERR 21;";
        }
    }

    private synchronized String obradiNaredbuGrupeStanje(Korisnik korisnik, String username, String lozinka) {
        Aerodromi_WS aerodromi_WS = new Aerodromi_WS();
        StatusKorisnika statusKorisnika = aerodromi_WS.dajStatusKorisnika(korisnik.getUsername(),
                korisnik.getPassword());
        if (statusKorisnika == StatusKorisnika.AKTIVAN || statusKorisnika==StatusKorisnika.REGISTRIRAN) {
            return "OK 21;";
        } else if (statusKorisnika == StatusKorisnika.BLOKIRAN) {
            return "OK 22;";
        } else {
            return "ERR 21";
        }
    }

    private synchronized String obradiNaredbuGrupeAktiviraj(Korisnik korisnik, String username, String lozinka) {
        Aerodromi_WS aerodromi_WS = new Aerodromi_WS();
        StatusKorisnika statusKorisnika = aerodromi_WS.dajStatusKorisnika(korisnik.getUsername(),
                korisnik.getPassword());
        if (statusKorisnika == StatusKorisnika.DEREGISTRIRAN || statusKorisnika == StatusKorisnika.NEPOSTOJI) {
            return "ERR 21;";
        } else if (statusKorisnika == StatusKorisnika.AKTIVAN) {
            return "ERR 22;";
        } else {
            aerodromi_WS.aktivirajGrupu(korisnik.getUsername(),
                    korisnik.getPassword());
            return "OK 20;";
        }
    }

    private synchronized String obradiNaredbuGrupeBlokiraj(Korisnik korisnik, String username, String lozinka) {
        Aerodromi_WS aerodromi_WS = new Aerodromi_WS();
        StatusKorisnika statusKorisnika = aerodromi_WS.dajStatusKorisnika(korisnik.getUsername(),
                korisnik.getPassword());
        if (statusKorisnika == StatusKorisnika.AKTIVAN || statusKorisnika == StatusKorisnika.REGISTRIRAN) {
            aerodromi_WS.blokirajGrupu(korisnik.getUsername(),
                    korisnik.getPassword());
            return "OK 20;";
        } else if (statusKorisnika == StatusKorisnika.NEAKTIVAN) {
            return "ERR 23";
        } else {
            return "ERR 21;";
        }
    }

    private synchronized String obradiNaredbuGrupeAerodromi(Korisnik korisnik, String parametar, String korisnicko, String lozinka) {
        Aerodromi_WS aerodromi_WS = new Aerodromi_WS();
        StatusKorisnika statusKorisnika = aerodromi_WS.dajStatusKorisnika(korisnik.getUsername(),
                korisnik.getPassword());
        if (statusKorisnika == StatusKorisnika.BLOKIRAN) {
            if (aerodromi_WS.aerodromiGrupe(korisnik.getUsername(),
                    korisnik.getPassword()) == true) {
                aerodromi_WS.obrisiAerodromeGrupe(korisnik.getUsername(),
                        korisnik.getPassword());
            }
            String zapisiAerdroma = parametar.substring(parametar
                    .indexOf("AERODROMI") + 9).trim();
            String icaosi[] = zapisiAerdroma.split(",");
            for (String a : icaosi) {
                String ic=a.replace(";", " ")
                                .trim();
                System.out.println("|"+ic.trim()+"|");
                aerodromi_WS.dodajAerodromGrupi(korisnik.getUsername(), korisnik.getPassword(),
                        ic.trim());
            }
            return "OK 20;";
        } else if (statusKorisnika == StatusKorisnika.AKTIVAN
                || statusKorisnika == StatusKorisnika.REGISTRIRAN) {
            return "ERR 31;";
        }
        return "ERR 32;";
    }
}
