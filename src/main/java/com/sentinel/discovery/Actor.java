package com.sentinel.discovery;

import org.json.JSONObject;

/**
 * Actor defines a discovered member in the cluster. It is defined as a role,
 * IP address, and unique identifier.
 * 
 * @author Andrew Trimble
 */
public class Actor {
    /** The Actor's role */
    private final String role;

    /** The Actor's IP */
    private final String ip;

    /** The Actor's unique ID */
    private final String id;

    /**
     * Create a new Actor.
     * 
     * @param role the role
     * @param ip the IP address
     * @param id the unique identifier
     */
    public Actor(String role, String ip, String id) {
        this.role = role;
        this.ip = ip;
        this.id = id;
    }

    /**
     * Get the currently assigned role.
     * 
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Get the IP address.
     * 
     * @return the IP address
     */
    public String getIP() {
        return ip;
    }

    /**
     * Get the unique ID.
     * 
     * @return the unique ID
     */
    public String getID() {
        return id;
    }

    /**
     * Represent this actor as a JSON string.
     * 
     * @return the JSON representation
     */
    public String toJSON() {
        return String.format("{\"role\":\"%s\",\"ip\":\"%s\",\"id\":\"%s\"}", role, ip, id);
    }

    /**
     * Construct an actor from a JSON string.
     * 
     * @param root the JSON object's root
     * @return an Actor
     */
    public static Actor fromJSON(JSONObject root) {
        return new Actor(root.getString("role"), root.getString("ip"), root.getString("id"));
    }

    /**
     * Returns the JSON representation of this Actor.
     * 
     * @return the JSON representation
     */
    @Override
    public String toString() {
        return toJSON();
    }

    /**
     * A unique identifier composed of the role, IP, and ID of this actor.
     * 
     * @return a unique key
     */
    public String key() {
        return String.format("%s:%s:%s", role, ip, id);
    }
}
