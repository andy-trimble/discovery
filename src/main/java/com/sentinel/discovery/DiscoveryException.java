package com.sentinel.discovery;

/**
 * An exception to be thrown in the event of an issue with discovery.
 * 
 * @author Andrew Trimble
 */
public class DiscoveryException extends Exception {

    /**
     * Creates a new instance of <code>DiscoveryException</code> without detail
     * message.
     */
    public DiscoveryException() {
    }

    /**
     * Constructs an instance of <code>DiscoveryException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public DiscoveryException(String msg) {
        super(msg);
    }

    /**
     * Create a new instance of <code>DiscoveryException</code> with a detail
     * message and a cause.
     * 
     * @param msg a detail message
     * @param cause the cause of the exception
     */
    public DiscoveryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
