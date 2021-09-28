package me.mattdokn.flightbeacon;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;

record ScheduledTask(long timestamp, Runnable task) implements Comparable<ScheduledTask> {
    @Override
    public int compareTo(ScheduledTask other)
    {
        return Long.compare(this.timestamp, other.timestamp);
    }
}

public class FlightBeacon implements DedicatedServerModInitializer {
    public static final FlightStatusEffect FLIGHT = new FlightStatusEffect();
    public static final NoFallStatusEffect NOFALL = new NoFallStatusEffect();

    public static MinecraftServer server;
    public static PriorityQueue<ScheduledTask> scheduler;

    @Override
    public void onInitializeServer() {
        // Register our status effects
        Registry.register(Registry.STATUS_EFFECT, new Identifier("flightbeacon", "flight"), FLIGHT);
        Registry.register(Registry.STATUS_EFFECT, new Identifier("flightbeacon", "nofall"), NOFALL);

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
        });

        scheduler = new PriorityQueue<>();

        // Every world tick
        ServerTickEvents.END_SERVER_TICK.register(mc -> {
            // since the queue is sorted (priority) we can use a while loop here
            // Go through all ready tasks and run each one, then remove it from the queue
            while (!scheduler.isEmpty() && scheduler.element().timestamp() >= server.getOverworld().getTime())
                scheduler.remove().task().run();
        });
    }
}
