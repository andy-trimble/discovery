/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sentinel.discovery;

import com.sentinel.config.Config;

/**
 *
 * @author atrimble
 */
public class Configuration {
    @Config(name = "DISCOVERY_NETWORK_INTERFACE", defaultValue = "eth0")
    public static String NETWORK_INTERFACE;

    @Config(name = "DISCOVERY_GROUP", defaultValue = "239.0.0.1")
    public static String GROUP;

    @Config(name = "DISCOVERY_PORT", defaultValue = "8989")
    public static int PORT;
    
    @Config(name = "DISCOVERY_TTL", defaultValue = "1")
    public static int TTL;
}
