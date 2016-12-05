/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sentinel.discovery;

import org.json.JSONObject;

/**
 *
 * @author atrimble
 */
public class Actor {
    private final String role;
    private final String ip;

    public Actor(String role, String ip) {
        this.role = role;
        this.ip = ip;
    }

    public String getRole() {
        return role;
    }

    public String getIP() {
        return ip;
    }

    public String toJSON() {
        return String.format("{\"role\":\"%s\",\"ip\":\"%s\"}", role, ip);
    }

    public static Actor fromJSON(JSONObject root) {
        return new Actor(root.getString("role"), root.getString("ip"));
    }

    @Override
    public String toString() {
        return toJSON();
    }

    public String key() {
        return String.format("%s:%s", role, ip);
    }
}
