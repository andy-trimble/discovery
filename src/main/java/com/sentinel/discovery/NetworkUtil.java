package com.sentinel.discovery;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * A utility class providing network tools.
 * 
 * @author Andy Trimble
 */
public class NetworkUtil {
    private static NetworkInterface nic = null;
    private static InetAddress inetAddress = null;

    private static String ip;

    private NetworkUtil() {

    }

    /**
     * Get the IP address of this instance.
     * 
     * @return the IP address associated with the selected network interface
     * @throws com.sentinel.discovery.DiscoveryException
     */
    public static String getIp() throws DiscoveryException {
        if(nic == null) {
            findNIC();
        }

        return ip;
    }

    /**
     * Get the configured network interface.
     * 
     * @return the network interface this member is using
     * @throws com.sentinel.discovery.DiscoveryException
     */
    public static NetworkInterface getNetworkInterface() throws DiscoveryException {
        if(nic == null) {
            findNIC();
        }

        return nic;
    }

    /**
     * Get the InetAddress object.
     * 
     * @return the InetAddress object
     * @throws com.sentinel.discovery.DiscoveryException
     */
    public static InetAddress getInetAddress() throws DiscoveryException {
        if(nic == null) {
            findNIC();
        }

        return inetAddress;
    }

    public static boolean hasNIC() throws DiscoveryException {
        try {
            nic = NetworkInterface.getByName(Configuration.NETWORK_INTERFACE);
        } catch (SocketException ex) {
            throw new DiscoveryException("Unable to determine IP address", ex);
        }

        return nic != null;
    }

    private static void findNIC() throws DiscoveryException {
        try {
            nic = NetworkInterface.getByName(Configuration.NETWORK_INTERFACE);

            if(nic != null && !nic.isUp()) {
                throw new DiscoveryException("Selected network interface '" + Configuration.NETWORK_INTERFACE + "' is down. Please select an alternate network interface.");
            }

            if(nic != null) {
                Enumeration e = nic.getInetAddresses();
                while(e.hasMoreElements()) {
                    InetAddress i = (InetAddress) e.nextElement();
                    String address = i.getHostAddress();

                    if(!address.contains(":")) {
                        inetAddress = i;
                        ip = address;
                        break;
                    }
                }
            } else {
                throw new DiscoveryException("Selected network interface '" + Configuration.NETWORK_INTERFACE + "' cannot be found. Please select an alternate network interface.");
            }
        } catch(SocketException ex) {
            throw new DiscoveryException("Unable to determine IP address", ex);
        }
    }
}
