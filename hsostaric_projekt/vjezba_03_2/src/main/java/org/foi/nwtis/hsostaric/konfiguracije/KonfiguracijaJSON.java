/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.konfiguracije;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author NWTiS_1
 */
public class KonfiguracijaJSON extends KonfiguracijaApstraktna {

    public KonfiguracijaJSON(String datoteka) {
        super(datoteka);
    }

   @Override
    public void ucitajKonfiguraciju() throws NemaKonfiguracije {
        try {
            ucitajKonfiguraciju(datoteka);
        } catch (NeispravnaKonfiguracija ex) {
          ex.printStackTrace();
        }
    }

    @Override
    public void ucitajKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NemaKonfiguracije("Neispravan naziv datoteke !" + getClass());
        }
        File file = new File(datoteka);
        if (file.exists() && file.isFile()) {
            try {
                Gson gson = new Gson();
                try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                    this.postavke=gson.fromJson(bufferedReader, Properties.class);
                    bufferedReader.close();
                }

            } catch (IOException ex) {
                throw new NeispravnaKonfiguracija("problem kod učitavanja konfiguracije");
            }
        } else {
            throw new NemaKonfiguracije("Datoteka pod nazivom" + datoteka + " ne postoji ili nije datoteka");
        }
    }

    @Override
    public void spremiKonfiguraciju() throws NemaKonfiguracije, NeispravnaKonfiguracija {
        spremiKonfiguraciju(datoteka);
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NemaKonfiguracije, NeispravnaKonfiguracija {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NemaKonfiguracije("Neispravan naziv datoteke !" + getClass());
        }
        File file = new File(datoteka);
        if (file.exists() && file.isFile() || !file.exists()) {
            try {
                Gson gson = new Gson();
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
                    gson.toJson(this.postavke, new BufferedWriter(bufferedWriter) );
                    bufferedWriter.close();
                }

            } catch (IOException ex) {
                throw new NeispravnaKonfiguracija("problem kod učitavanja konfiguracije");
            }
        } else {
            throw new NemaKonfiguracije("Datoteka pod nazivom" + datoteka + " ne postoji ili nije datoteka");
        }
    }

   
    
}
