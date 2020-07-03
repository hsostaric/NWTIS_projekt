/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import javax.ejb.Local;
import javax.ejb.Stateless;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.ejb.eb.Airports;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.Lokacija;
import org.foi.nwtis.rest.podaci.MeteoPodaci;

/**
 *
 * @author Hrvoje-PC
 */
@Stateless
@Local
public class Meteorolog {

    @Getter
    @Setter
    private String apiLIQ;

    @Getter
    @Setter
    private String apiKeyOWM;
    private MeteoPodaci meteoPodaci;
    private LIQKlijent lIQKlijent;
    private OWMKlijent klijent;

    private void dohvatiMeteoPodatke(Airports aerodrom) {
        klijent = new OWMKlijent(apiKeyOWM);
        String koordinate = aerodrom.getCoordinates();
        String polje[] = koordinate.split(",");
        Lokacija lokacija = new Lokacija();
        lokacija.setLatitude(polje[1]
                .trim());
        lokacija.setLongitude(polje[0]
                .trim());
        meteoPodaci = klijent.getRealTimeWeather(lokacija.getLatitude(),
                lokacija.getLongitude());
    }

    public String dohvatiTemperaturu( Airports aerodrom) {
        this.apiKeyOWM = apiKeyOWM;
        dohvatiMeteoPodatke(aerodrom);
        return meteoPodaci.getTemperatureValue()
                .toString() + " " + meteoPodaci.getTemperatureUnit();
    }

    public String dohvatiVlagu( Airports aerodrom) {
        this.apiKeyOWM = apiKeyOWM;
        dohvatiMeteoPodatke(aerodrom);
        return meteoPodaci.getHumidityValue()
                .toString() + " " + meteoPodaci.getHumidityUnit();
    }

    public Lokacija dajLokacijuLoqatoinIQ(String naziv) {
        Lokacija lokacija;
        lIQKlijent = new LIQKlijent(apiLIQ);
        lokacija=lIQKlijent.getGeoLocation(naziv);
        return lokacija;
    }

}
