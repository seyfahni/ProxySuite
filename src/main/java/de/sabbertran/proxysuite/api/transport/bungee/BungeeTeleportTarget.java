package de.sabbertran.proxysuite.api.transport.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import static net.md_5.bungee.api.event.ServerConnectEvent.Reason;

/**
 * 
 */
public interface BungeeTeleportTarget {

    /**
     * Connect the given player to this targets server. It is assumed that the represented target is valid. If not and
     * thus {@link #getTargetServer(net.md_5.bungee.api.ProxyServer)} equals null, an {@link IllegalArgumentException}
     * is thrown.
     *
     * @param proxy the proxy to work on
     * @param toSend the player to connect
     * 
     * @throws IllegalArgumentException if the server does not exist
     */
    default void connectToServer(ProxyServer proxy, ProxiedPlayer toSend) {
        ServerInfo server = getTargetServer(proxy);
        if (server == null) {
            throw new IllegalArgumentException("target server undefined");
        } else if (!toSend.getServer().getInfo().equals(server)) {
            toSend.connect(server, Reason.COMMAND);
        }
    }

    /**
     * Get the server that the target is on.
     * May return null.
     *
     * @param proxy the proxy to work on
     * @return the server with this target
     */
    ServerInfo getTargetServer(ProxyServer proxy);
}
