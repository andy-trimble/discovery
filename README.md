# discovery
Multicast Discovery in Java.

[![Build Status](https://travis-ci.org/andy-trimble/discovery.svg?branch=master)](https://travis-ci.org/andy-trimble/discovery)

Basic Usage:
```Java
Discovery d = new Discovery("server");
d.addDiscoveryListener((Actor actor) -> {
    System.out.println("Discovered new actor: " + actor.toString());
});
d.start();

// Do some stuff

d.shutdown();
```
By default, the discovery mechanism listens on the network interface eth0. In order to change this behavior do the following:

```Java
com.sentinel.discovery.Configuration.NETWORK_INTERFACE = "wlan0"
```
All configuration is located in com.sentinel.discovery.Configuration. They are static variables, simply change them to the desired values statically.
