package com.sentinel.discovery;

import com.sentinel.config.ConfigException;
import com.sentinel.config.EnvConfig;
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
public class Discovery extends Thread {
    private MulticastSocket socket;
    private InetAddress group;

    private final ExecutorService threadPool = Executors.newSingleThreadExecutor();

    private boolean running = true;
    private final Actor me;

    private final Map<String, Actor> cache = new ConcurrentHashMap<>();
    private final List<DiscoveryListener> listeners = new ArrayList<>();

    /**
     * Constructor.
     * @param role
     * @throws com.sentinel.discovery.DiscoveryException
     */
    public Discovery(String role) throws DiscoveryException {
        try {
            EnvConfig.load(Configuration.class);
        } catch (ConfigException ex) {
            throw new DiscoveryException("Cannot load configuration.", ex);
        }

        String ip = NetworkUtil.getIp();

        me = new Actor(role, ip);
        cache.put(me.key(), me);

        try {
            setupNetworking();
        } catch(IOException ex) {
            throw new DiscoveryException("Cannot setup network.", ex);
        }
    }

    public void addDiscoveryListener(DiscoveryListener l) {
        listeners.add(l);
    }

    @Override
    public final void start() {
        super.start();
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
        
        sendMessage(me.toJSON());
    }

    /**
     * Run the multicast listener.
     */
    @Override
    public void run() {
        byte[] buf = new byte[512];

        while(running) {
            DatagramPacket packet;
            packet = new DatagramPacket(buf, buf.length);
            packet.getLength();
            try {
                socket.receive(packet);
                threadPool.invokeAll(Collections.singletonList(Executors.callable(() -> {
                    try {
                        processMessage(Arrays.copyOf(packet.getData(), packet.getLength()));
                    } catch (IOException ex) {
                        
                    }
                })));
            } catch(SocketException ex) {
                running = false;
                return;
            } catch(IOException | InterruptedException ex) {
                
            }
        }
    }

    private void processMessage(byte[] bytes) throws IOException {
        JSONTokener tokener = new JSONTokener(new String(bytes));
        JSONObject root = new JSONObject(tokener);
        Actor actor = Actor.fromJSON(root);
        if(!actorKnown(actor)) {
            cache.put(actor.key(), actor);
            sendMessage(me.toJSON());
            listeners.forEach((l) -> {
                l.discovered(actor);
            });
        }
    }

    /**
     * Send a gossip message over multicast.
     * 
     * @param message a message.
     * @throws IOException in case of a sending error.
     */
    public void sendMessage(String message) throws IOException {
        byte[] bytes = message.getBytes();
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, group, Configuration.PORT);
        socket.send(packet);
    }

    private boolean actorKnown(Actor actor) {
        return cache.get(actor.key()) != null;
    }
}
