/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.podaci;

import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Hrvoje-PC
 */
public class Korisnik {

    @Getter
    @Setter
    private String username;
    @Getter
    @Setter
    private String password;

    public Korisnik(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Korisnik() {
    }

}
