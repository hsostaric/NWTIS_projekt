/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.rest.klijenti;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

/**
 * Jersey REST client generated for REST resource:ProjektResource
 * [/aerodromi]<br>
 * USAGE:
 * <pre>
 *        Aplikacija_3RS client = new Aplikacija_3RS();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Hrvoje-PC
 */
public class Aplikacija_3RS {

    private WebTarget webTarget;
    private Client client;
    private static final String BASE_URI = "http://localhost:8084/hsostaric_aplikacija_3_web/webresources";
    private  String username;
    private  String password;
    
    public Aplikacija_3RS(String username, String password) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
        this.username=username;
        this.password=password;
    }
    
    public Aplikacija_3RS() {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        webTarget = client.target(BASE_URI).path("aerodromi");
    }
    
    public <T> T dajAerodomeKorisnika(Class<T> responseType) throws ClientErrorException {
        WebTarget resource = webTarget;
        return (T) resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header("korisnik", username)
                .header("lozinka",password)
                .get(responseType);
    }

  public <T> T prestaniPratitAerodrom(Class<T> responseType, String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
        resource=resource.path(icao);
        System.out.println(resource.getUri().toString());
        return  resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header("korisnik", username)
                .header("lozinka",password).delete(responseType);
    }

      public <T> T obrisiLetoveAerodroma(Class<T> responseType,String icao) throws ClientErrorException {
        WebTarget resource = webTarget;
      resource= resource.path(icao).path("avioni");
        return (T) resource.request(javax.ws.rs.core.MediaType.APPLICATION_JSON).header("korisnik", username)
                .header("lozinka",password)
                .delete(responseType);
    }
    
    public void close() {
        client.close();
    }
    
}
