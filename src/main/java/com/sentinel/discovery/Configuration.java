package com.sentinel.discovery;

/**
 * The static configuration for this actor.
 * 
 * @author Andrew Trimble
 */
public class Configuration {
    /** The network interface to which this actor is bound. */
    public static String NETWORK_INTERFACE = "eth0";

    /** The Multicast group. */
    public static String GROUP = "230.1.1.1";

    /** The Multicast port. */
    public static int PORT = 8989;
    
    /** The Multicast TTL. */
    public static int TTL = 1;

    /** The number of times to announce the actor on startup. */
    public static int ANNOUNCE_COUNT = 6;
    
    /** The milliseconds to wait between announcements. */
    public static long ANNOUNCE_WAIT = 500;
}
