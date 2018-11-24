package de.sabbertran.proxysuite.api.transport;

/**
 * 
 */
public interface TeleportTarget {

    /**
     * Teleport the given player to the target on this server.
     *
     * @param server the server to work on
     * @param toTeleport the player to teleport
     */
    void teleportToTarget(org.bukkit.Server server, org.bukkit.entity.Player toTeleport);
    
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
    default void connectToServer(net.md_5.bungee.api.ProxyServer proxy, net.md_5.bungee.api.connection.ProxiedPlayer toSend) {
        net.md_5.bungee.api.config.ServerInfo server = getTargetServer(proxy);
        if (server == null) {
            throw new IllegalArgumentException("target server undefined");
        } else if (!toSend.getServer().getInfo().equals(server)) {
            toSend.connect(server, net.md_5.bungee.api.event.ServerConnectEvent.Reason.COMMAND);
        }
    }

    /**
     * Get the server that the target is on.
     * May return null.
     *
     * @param proxy the proxy to work on
     * @return the server with this target
     */
    net.md_5.bungee.api.config.ServerInfo getTargetServer(net.md_5.bungee.api.ProxyServer proxy);
}
