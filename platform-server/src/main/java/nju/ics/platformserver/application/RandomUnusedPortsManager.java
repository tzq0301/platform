package nju.ics.platformserver.application;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUnusedPortsManager {
    private final Set<Integer> usedPorts;

    private final ThreadLocalRandom random;

    private final int origin;

    private final int bound;

    public RandomUnusedPortsManager(int origin, int bound) {
        this.usedPorts = new HashSet<>();
        this.random = ThreadLocalRandom.current();
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
