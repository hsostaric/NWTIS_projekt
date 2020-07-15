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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Properties;


/**
 *
 * @author NWTiS_1
 */
public class KonfiguracijaBIN extends KonfiguracijaApstraktna {

    public KonfiguracijaBIN(String datoteka) {
        super(datoteka);
    }

    @Override
    public void ucitajKonfiguraciju() throws NemaKonfiguracije {
        try {
            ucitajKonfiguraciju(datoteka);
        } catch (NeispravnaKonfiguracija ex) {

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
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file))) {
                    Object object = objectInputStream.readObject();
                    if(object instanceof Properties){
                        this.postavke= (Properties)object;
                    }}
            } catch (IOException | ClassNotFoundException ex) {
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
                try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file))) {
                    objectOutputStream.writeObject(this.postavke);
                }

            } catch (IOException ex) {
                throw new NeispravnaKonfiguracija("problem kod učitavanja konfiguracije");
            }
        } else {
            throw new NemaKonfiguracije("Datoteka pod nazivom" + datoteka + " ne postoji ili nije datoteka");
        }
    }

}
