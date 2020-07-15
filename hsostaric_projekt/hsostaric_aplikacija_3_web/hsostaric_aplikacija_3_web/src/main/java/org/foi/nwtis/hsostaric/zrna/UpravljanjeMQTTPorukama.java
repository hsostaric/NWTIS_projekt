/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.zrna;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.hsostaric.ejb.eb.MqttPravi;
import org.foi.nwtis.hsostaric.ejb.sb.MqttPraviFacadeLocal;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Hrvoje-PC
 */
@Named(value = "upravljanjeMQTTPorukama")
@ViewScoped
public class UpravljanjeMQTTPorukama implements Serializable {

    @Getter
    @Setter
    private List<MqttPravi> lista = new ArrayList<>();

    @EJB
    MqttPraviFacadeLocal mqttPraviFacadeLocal;

    @Getter
    @Setter
    private String sadrzajPoruka = "";
    @Getter
    @Setter
    private int brojStranica = 0;
    @Inject
    ServletContext servletContext;
    BP_Konfiguracija bpk;

    public UpravljanjeMQTTPorukama() {
    }

    @PostConstruct
    public void Init() {
        bpk = (BP_Konfiguracija) servletContext.getAttribute("BP_Konfig");
        brojStranica = Integer.parseInt(bpk
                .getKonfig()
                .dajPostavku("brojPoStranici"));
        lista = mqttPraviFacadeLocal.findAll();
    }

    public String obrisiZapise() {
        if (lista.isEmpty()) {
            sadrzajPoruka = "Nemate nikakvih poruka za brisanje!";
        } else {
            for (MqttPravi mqttPravi : lista) {
                mqttPraviFacadeLocal.remove(mqttPravi);
            }
            sadrzajPoruka = "";
            lista.clear();
        }
        return "";
    }

    public String konvertirajDatum(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY HH:mm:ss.SSS");
        String datum = sdf.format(date);
        return datum;
    }
}
