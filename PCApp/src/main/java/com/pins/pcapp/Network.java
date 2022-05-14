package com.pins.pcapp;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class Network {
    private final IPAddress networkAddress;
    private final IPAddress maxAddress;

    public Network(InetAddress address, short mask) {
        IPAddress ip = new IPAddress(address.toString().substring(1));
        networkAddress = ip.getNetworkAddress(mask);
        maxAddress = ip.getMaxAddress(mask);
    }

    public List<String> getAllAddresses() {
        List<String> tmp = new ArrayList<>();
        IPAddress curr = new IPAddress(networkAddress);

        while (!curr.equals(maxAddress)){
            tmp.add(curr.toString());
            curr = curr.next();
        }
        return tmp;
    }
}
