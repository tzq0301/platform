package nju.ics.platformserver.application;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomUnusedPortsManager {
    private final Set<Integer> usedPorts;

    private final Random random;

    private final int origin;

    private final int bound;

    public RandomUnusedPortsManager(int origin, int bound) {
        this.usedPorts = new HashSet<>();
        this.random = new Random();
        this.origin = origin;
        this.bound = bound;
    }

    public synchronized int generate() {
        int port;

        do {
            port = random.nextInt(origin, bound);
        } while (usedPorts.contains(port));

        return port;
    }

    public synchronized void makeUnused(int port) {
        usedPorts.remove(port);
    }
}
