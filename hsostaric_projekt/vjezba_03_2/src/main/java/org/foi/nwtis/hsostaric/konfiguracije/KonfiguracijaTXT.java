/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.konfiguracije;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Hrvoje Šoštarić
 */
public class KonfiguracijaTXT extends KonfiguracijaApstraktna {

    public KonfiguracijaTXT(String datoteka) {
        super(datoteka);
    }

    @Override
    public void ucitajKonfiguraciju() throws NemaKonfiguracije {
        try {
            ucitajKonfiguraciju(datoteka);
        } catch (org.foi.nwtis.hsostaric.konfiguracije.NeispravnaKonfiguracija ex) {

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
                this.postavke.load(new FileInputStream(file));

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
                this.postavke.store(new FileOutputStream(file), "hsostaric");

            } catch (IOException ex) {
                throw new NeispravnaKonfiguracija("problem kod učitavanja konfiguracije");
            }
        } else {
            throw new NemaKonfiguracije("Datoteka pod nazivom" + datoteka + " ne postoji ili nije datoteka");
        }
    }

}
