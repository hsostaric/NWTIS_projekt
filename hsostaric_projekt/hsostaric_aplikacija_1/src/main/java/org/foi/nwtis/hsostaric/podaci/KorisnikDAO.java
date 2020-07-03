/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.hsostaric.konfiguracije.bp.BP_Konfiguracija;

/**
 *
 * @author Hrvoje-PC
 */
public class KorisnikDAO {

    Statement statement;
    ResultSet resultSet;
    private BP_Konfiguracija bpk;

    public KorisnikDAO(BP_Konfiguracija bpk) {
        this.bpk = bpk;
    }

    public KorisnikDAO() {
    }

    private Connection dajKonenkciju() throws ClassNotFoundException, SQLException {
        String url = bpk.getServerDatabase() + bpk.getUserDatabase();
        String bpkorisnik = bpk.getUserUsername();
        String bplozinka = bpk.getUserPassword();
        System.out.println("Url: " + url + "\nUsername: " + bpkorisnik + "\nPassword: " + bplozinka);
        Class.forName(bpk.getDriverDatabase(url));
        return DriverManager.getConnection(url, bpkorisnik, bplozinka);
    }

    public boolean AutentificirajKorisnika(String username, String password) {
        try {
            Connection connection;
            int broj = 0;
            String url = bpk.getServerDatabase() + bpk.getUserDatabase();
            String bpkorisnik = bpk.getUserUsername();
            String bplozinka = bpk.getUserPassword();
            System.out.println("Url: " + url + "\nUsername: " + bpkorisnik + "\nPassword: " + bplozinka);
            Class.forName(bpk.getDriverDatabase(url));
            connection = DriverManager.getConnection(url, bpkorisnik, bplozinka);
            String sql = "SELECT COUNT(*) as broj FROM KORISNICI where KOR_IME =? AND LOZINKA =?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, username.trim());
                ps.setString(2, password.trim());
                resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    broj = resultSet.getInt("broj");
                    System.out.println("Broj je : " + broj);
                    break;
                }
                resultSet.close();
            }
            connection.close();
            return broj > 0;
        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean provjeriPostojanostKorisnickog(Korisnik korisnik) {
        try {
            Connection connection;
            int broj = 0;
            String url = bpk.getServerDatabase() + bpk.getUserDatabase();
            String bpkorisnik = bpk.getUserUsername();
            String bplozinka = bpk.getUserPassword();
            System.out.println("Url: " + url + "\nUsername: " + bpkorisnik + "\nPassword: " + bplozinka);
            Class.forName(bpk.getDriverDatabase(url));
            connection = DriverManager.getConnection(url, bpkorisnik, bplozinka);
            String sql = "SELECT COUNT(*) as broj FROM KORISNICI where KOR_IME = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, korisnik.getUsername());
                resultSet = ps.executeQuery();
                while (resultSet.next()) {
                    broj = resultSet.getInt("broj");
                    break;
                }
                resultSet.close();
            }
            connection.close();
            return broj > 0;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Greska: " + ex);
        }
        return false;
    }

    public boolean registrirajKorisnika(Korisnik korisnik) {
        try {
            Connection connection;
            int broj = 0;
            String url = bpk.getServerDatabase() + bpk.getUserDatabase();
            String bpkorisnik = bpk.getUserUsername();
            String bplozinka = bpk.getUserPassword();
            System.out.println("Url: " + url + "\nUsername: " + bpkorisnik + "\nPassword: " + bplozinka);
            Class.forName(bpk.getDriverDatabase(url));
            connection = DriverManager.getConnection(url, bpkorisnik, bplozinka);
            String sql = "INSERT INTO KORISNICI VALUES (?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, korisnik.getUsername());
                ps.setString(2, korisnik.getPassword());
                broj = ps.executeUpdate();
            }
            connection.close();
            return broj > 0;
        } catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Greska: " + ex);
        }
        return false;
    }

}
