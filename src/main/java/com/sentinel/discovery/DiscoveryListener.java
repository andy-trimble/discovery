package com.sentinel.discovery;

/**
 * A callback interface for discovery events.
 * 
 * @author Andrew Trimble
 */
public interface DiscoveryListener {
    /**
     * Method called when a new actor is discovered.
     * 
     * @param actor the discovered actor
     */
    void discovered(Actor actor);
}
