/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.eb;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Hrvoje-PC
 */
@Entity
@Table(name = "MQTT_PORUKE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "MqttPoruke.findAll", query = "SELECT m FROM MqttPoruke m")})
public class MqttPoruke implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID_PORUKA", nullable = false)
    private int idPoruka;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(nullable = false, length = 200)
    private String sadrzaj;
    @Basic(optional = false)
    @NotNull
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date vrijeme;
    @JoinColumn(name = "USERNAME", referencedColumnName = "KOR_IME", nullable = false)
    @ManyToOne(optional = false)
    private Korisnici username;

    public MqttPoruke() {
    }

    public MqttPoruke(Integer id) {
        this.id = id;
    }

    public MqttPoruke(Integer id, int idPoruka, String sadrzaj, Date vrijeme) {
        this.id = id;
        this.idPoruka = idPoruka;
        this.sadrzaj = sadrzaj;
        this.vrijeme = vrijeme;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getIdPoruka() {
        return idPoruka;
    }

    public void setIdPoruka(int idPoruka) {
        this.idPoruka = idPoruka;
    }

    public String getSadrzaj() {
        return sadrzaj;
    }

    public void setSadrzaj(String sadrzaj) {
        this.sadrzaj = sadrzaj;
    }

    public Date getVrijeme() {
        return vrijeme;
    }

    public void setVrijeme(Date vrijeme) {
        this.vrijeme = vrijeme;
    }

    public Korisnici getUsername() {
        return username;
    }

    public void setUsername(Korisnici username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MqttPoruke)) {
            return false;
        }
        MqttPoruke other = (MqttPoruke) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "org.foi.nwtis.hsostaric.ejb.eb.MqttPoruke[ id=" + id + " ]";
    }
    
}
