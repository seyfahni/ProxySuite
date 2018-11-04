package de.sabbertran.proxysuite.bungee.commands.gamemode;

public enum GameMode {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2),
    SPECTATOR(3),
    ;
    
    private final int id;
    
    private GameMode(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
