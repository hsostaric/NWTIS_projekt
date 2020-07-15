/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.web.mqttslusac;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.hsostaric.ejb.eb.MqttPravi;
import org.foi.nwtis.hsostaric.ejb.sb.MqttPraviFacadeLocal;
import org.foi.nwtis.hsostaric.ejb.sb.MyairportsFacadeLocal;
import org.foi.nwtis.hsostaric.konfiguracije.Konfiguracija;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;
import org.foi.nwtis.hsostaric.podaci.Mqtt_data;
import org.fusesource.hawtbuf.Buffer;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.fusesource.mqtt.client.Callback;
import org.fusesource.mqtt.client.CallbackConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

/**
 *
 * @author Hrvoje-PC
 */
public class MqttListener extends Thread {

    private BP_Konfiguracija konfa;
    private boolean radi = true;
    private String username;
    private String password;
    private int port;
    private String host;
    private String destination;
    private Konfiguracija konf;
    MqttPraviFacadeLocal mqttPraviFacadeLocal;
    MyairportsFacadeLocal myairportsFacadeLocal;
    MqttPravi mqttPravi;
    String korisnickoIme;

    public MqttListener(BP_Konfiguracija bpk, MqttPraviFacadeLocal mqttPraviFacadeLocal, MyairportsFacadeLocal myairportsFacadeLocal) {
        this.konfa = bpk;
        this.mqttPraviFacadeLocal = mqttPraviFacadeLocal;
        this.myairportsFacadeLocal = myairportsFacadeLocal;
    }

    @Override
    public void interrupt() {
        radi = false;
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        MQTT mqtt = new MQTT();
        mqtt.setUserName(username);
        mqtt.setPassword(password);
        try {
            mqtt.setHost(host, port);
        } catch (URISyntaxException ex) {
            System.out.println("Greska: " + ex);
        }
        final CallbackConnection connection = mqtt.callbackConnection();
        connection.listener((new org.fusesource.mqtt.client.Listener() {
            @Override
            public void onConnected() {
                System.out.println("Otvorena veza na MQTT");
            }

            @Override
            public void onDisconnected() {
                System.out.println("Prekinuta veza na MQTT");
            }

            @Override
            public void onPublish(UTF8Buffer utfb, Buffer buffer, Runnable r) {
                String poruka = buffer.utf8().toString();
                System.out.println("Strigla poruka: " + poruka);
                obradaMqttPoruke(poruka);

            }

            private void obradaMqttPoruke(String poruka) throws JsonSyntaxException {
                Gson gson = new Gson();
                Mqtt_data mqtt_data;
                mqtt_data = gson.fromJson(poruka, Mqtt_data.class);
                Boolean prazno = false;
                if (korisnickoIme.equals(mqtt_data.getKorisnik())) {
                    prazno = myairportsFacadeLocal.pracenjeAerodroma(
                            mqtt_data.getAerodrom().trim());
                    if (prazno == false) {
                        mqttPravi = new MqttPravi();
                        mqttPravi.setAerodrom(mqtt_data.getAerodrom().trim());
                        mqttPravi.setAvion(mqtt_data.getAvion().trim());
                        mqttPravi.setKorisnik(mqtt_data.getKorisnik().trim());
                        mqttPravi.setOznaka(mqtt_data.getOznaka().trim());
                        mqttPravi.setPoruka(mqtt_data.getPoruka());
                        Date datum = pretvoriStringUDate(mqtt_data.getVrijeme());
                        mqttPravi.setVrijeme(new java.sql.Date(datum.getTime()));
                        mqttPraviFacadeLocal.create(mqttPravi);
                    }
                }
            }

            @Override
            public void onFailure(Throwable thrwbl) {
                System.out.println("Problem u vezi na MQTT");
            }
        }));
        connection.connect(new Callback<Void>() {
            @Override
            public void onSuccess(Void t) {
                Topic[] topics = {new Topic(destination, QoS.AT_LEAST_ONCE)};
                connection.subscribe(topics, new Callback<byte[]>() {
                    @Override
                    public void onSuccess(byte[] t) {
                        System.out.println("Pretplata na: " + destination);
                    }

                    @Override
                    public void onFailure(Throwable thrwbl) {
                        System.out.println("Problem kod pretplate na: " + destination);
                    }
                });
            }

            @Override
            public void onFailure(Throwable thrwbl) {
                System.out.println("Neuspjela pretplata na: " + destination);
            }
        });
        synchronized (MqttListener.class) {
            while (radi == true && !interrupted()) {
                try {
                    MqttListener.class.wait();
                } catch (InterruptedException ex) {
                    radi = false;
                    Logger.getLogger(MqttListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override

    public synchronized void start() {
        konf = konfa.getKonfig();
        username = konf.dajPostavku("grupa.username");
        password = konf.dajPostavku("grupa.password");
        port = Integer.parseInt(konf.dajPostavku("grupa.port"));
        host = konf.dajPostavku("grupa.host");
        destination = konf.dajPostavku("grupa.destination") + username;
        korisnickoIme = konf.dajPostavku("grupa.korisnikNaziv");
        super.start();
    }

    private Date pretvoriStringUDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
        try {
            return sdf.parse(date);
        } catch (ParseException ex) {
            System.out.println("Greska kod parsanja: " + ex);
        }
        return null;
    }
}
