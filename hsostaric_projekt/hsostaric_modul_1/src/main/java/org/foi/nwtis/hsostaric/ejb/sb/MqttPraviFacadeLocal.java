/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.hsostaric.ejb.eb.MqttPravi;

/**
 *
 * @author Hrvoje-PC
 */
@Local
public interface MqttPraviFacadeLocal {

    void create(MqttPravi mqttPravi);

    void edit(MqttPravi mqttPravi);

    void remove(MqttPravi mqttPravi);

    MqttPravi find(Object id);

    List<MqttPravi> findAll();

    List<MqttPravi> findRange(int[] range);

    int count();
    
}
