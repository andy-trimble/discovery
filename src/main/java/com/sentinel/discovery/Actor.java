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
    private final String id;

    public Actor(String role, String ip, String id) {
        this.role = role;
        this.ip = ip;
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public String getIP() {
        return ip;
    }

    public String getID() {
        return id;
    }

    public String toJSON() {
        return String.format("{\"role\":\"%s\",\"ip\":\"%s\",\"id\":\"%s\"}", role, ip, id);
    }

    public static Actor fromJSON(JSONObject root) {
        return new Actor(root.getString("role"), root.getString("ip"), root.getString("id"));
    }

    @Override
    public String toString() {
        return toJSON();
    }

    public String key() {
        return String.format("%s:%s:%s", role, ip, id);
    }
}
