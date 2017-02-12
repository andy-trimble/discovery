package com.sentinel.discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * A discovery protocol implemented using multicast.
 * 
 * @author Andy Trimble
 */
public class Discovery {
    private MulticastSocket socket;
    private InetAddress group;

    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

    private boolean running = true;
    private final Actor me;

    private final Map<String, Actor> cache = new ConcurrentHashMap<>();
    private final List<DiscoveryListener> listeners = new ArrayList<>();

    /**
     * Constructor.
     * 
     * @param role the role for this actor
     * @throws com.sentinel.discovery.DiscoveryException
     */
    public Discovery(String role) throws DiscoveryException {
        String ip = NetworkUtil.getIp();

        UUID uuid = UUID.randomUUID();
        me = new Actor(role, ip, uuid.toString());
        cache.put(me.key(), me);

        try {
            setupNetworking();
        } catch(IOException ex) {
            throw new DiscoveryException("Cannot setup network.", ex);
        }
    }

    /**
     * Register a discovery callback object.
     * 
     * @param l a listener
     */
    public void addDiscoveryListener(DiscoveryListener l) {
        listeners.add(l);
    }

    /**
     * Shutdown the discovery service.
     */
    public void shutdown() {
        this.running = false;
        socket.close();
    }

    /**
     * Setup the multicast facilities.
     * 
     * @throws java.io.IOException
     */
    private void setupNetworking() throws IOException, DiscoveryException {
        if(NetworkUtil.getNetworkInterface() == null) {
            throw new DiscoveryException("No Network interface can be found.");
        }
        
        try {
            if(!NetworkUtil.getNetworkInterface().supportsMulticast()) {
                throw new DiscoveryException("Network Interface doesn't support multicast.");
            }
        } catch (SocketException ex) {
            throw new DiscoveryException("Error determining multicast support.", ex);
        }

        try {
            if(NetworkUtil.getNetworkInterface() == null || !NetworkUtil.getNetworkInterface().isUp()) {
                throw new DiscoveryException("Cannot form cluster. Network interface is down.");
            }
        } catch(SocketException ex) {
            throw new DiscoveryException("Cannot form cluster.", ex);
        }
        
        socket = new MulticastSocket(Configuration.PORT);
        socket.setReuseAddress(true);
        socket.setTimeToLive(Configuration.TTL);
        group = InetAddress.getByName(Configuration.GROUP);
        socket.joinGroup(new InetSocketAddress(group, Configuration.PORT), NetworkUtil.getNetworkInterface());
    }

    /**
     * Run the multicast listener.
     */
    public void start() {
        byte[] buf = new byte[512];

        new Thread() {
            @Override
            public void run() {
                for(int i = 0; i < Configuration.ANNOUNCE_COUNT; ++i) {
                    // Allow the listener to start so we receive announcements
                    try {
                        Thread.sleep(Configuration.ANNOUNCE_WAIT);
                    } catch (InterruptedException ex) { }

                    try {
                        sendMessage(me.toJSON());
                    } catch (IOException ex) { }
                }
            }
        }.start();

        while(running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            packet.getLength();
            try {
                socket.receive(packet);
                threadPool.invokeAll(Collections.singletonList(Executors.callable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processMessage(Arrays.copyOf(packet.getData(), packet.getLength()));
                        } catch (IOException ex) {
                            
                        }
                    }
                })));
            } catch(SocketException ex) {
                running = false;
                return;
            } catch(IOException | InterruptedException ex) {
                
            }
        }
    }

    /**
     * Process a raw UDP message.
     * 
     * @param bytes the UDP message
     * @throws IOException 
     */
    private void processMessage(byte[] bytes) throws IOException {
        JSONTokener tokener = new JSONTokener(new String(bytes));
        JSONObject root = new JSONObject(tokener);
        Actor actor = Actor.fromJSON(root);
        if(cache.get(actor.key()) == null) {
            cache.put(actor.key(), actor);
            sendMessage(me.toJSON());
            for(final DiscoveryListener l : listeners) {
                l.discovered(actor);
            };
        }
    }

    /**
     * Send a message over multicast.
     * 
     * @param message a message.
     * @throws IOException in case of a sending error.
     */
    public void sendMessage(String message) throws IOException {
        byte[] bytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, Configuration.PORT);
        socket.send(packet);
    }
}
