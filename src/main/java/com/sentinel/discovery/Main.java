/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sentinel.discovery;

/**
 *
 * @author atrimble
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        com.sentinel.discovery.Configuration.NETWORK_INTERFACE = "en0";

        Thread th = new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("Initializing discovery process");
                    Discovery d = new Discovery("java-server");
                    d.addDiscoveryListener((Actor actor) -> {
                        System.out.println("Discovered new actor: " + actor.toString());
                    });
                    System.out.println("Hello, I am " + d.me().toString());
                    d.start();
                } catch (DiscoveryException ex) {
                    System.out.println("Error creating multicast discovery agent." + ex);
                }
            }
        };

        th.start();

        th.join();
    }
}
