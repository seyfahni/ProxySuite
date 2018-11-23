package de.sabbertran.proxysuite.bungee.handlers;

import de.sabbertran.proxysuite.bungee.ProxySuite;
import de.sabbertran.proxysuite.utils.Location;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public class PositionHandler {

    private final ProxySuite main;
    private final HashMap<UUID, Runnable> positionRunnables;
    private final HashMap<UUID, Location> positions;
    private final HashMap<UUID, Location> localPositions;

    public PositionHandler(ProxySuite main) {
        this.main = main;
        positionRunnables = new HashMap<>();
        positions = new HashMap<>();
        localPositions = new HashMap<>();
    }

    public void requestPosition(ProxiedPlayer p) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("GetPosition");
            out.writeUTF(p.getName());
            out.writeUTF(p.getServer().getInfo().getName());
        } catch (IOException e) {
            main.getLogger().log(Level.SEVERE, null, e);
        }
        p.getServer().sendData("proxysuite:channel", b.toByteArray());
    }

    public void locationReceived(ProxiedPlayer p, Location loc) {
        if (positionRunnables.containsKey(p.getUniqueId())) {
            localPositions.put(p.getUniqueId(), loc);
            positionRunnables.remove(p.getUniqueId()).run();
        } else
            positions.put(p.getUniqueId(), loc);
    }

    public HashMap<UUID, Location> getLocalPositions() {
        return localPositions;
    }

    public void addPositionRunnable(ProxiedPlayer p, Runnable run) {
        positionRunnables.put(p.getUniqueId(), run);
    }
}
