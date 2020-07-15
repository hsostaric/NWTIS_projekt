/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.hsostaric.ejb.sb;

import java.util.List;
import javax.ejb.Local;
import org.foi.nwtis.hsostaric.ejb.eb.MqttPoruke;

/**
 *
 * @author Hrvoje-PC
 */
@Local
public interface MqttPorukeFacadeLocal {

    void create(MqttPoruke mqttPoruke);

    void edit(MqttPoruke mqttPoruke);

    void remove(MqttPoruke mqttPoruke);

    MqttPoruke find(Object id);

    List<MqttPoruke> findAll();

    List<MqttPoruke> findRange(int[] range);

    int count();
    
}
