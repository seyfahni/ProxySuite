package de.sabbertran.proxysuite.utils;

import com.google.gson.Gson;
import java.util.Optional;

/**
 *
 */
public class Regestry {
    
    private Gson gson;
    
    private Regestry() {
    }
    
    public static Regestry getInstance() {
        return RegestryHolder.INSTANCE;
    }
    
    private static class RegestryHolder {

        private static final Regestry INSTANCE = new Regestry();
    }

    public void registerGson(Gson gson) {
        this.gson = gson;
    }
    
    public Gson getGson() {
        return gson;
    }
    
    public static Gson gson() {
        return getInstance().getGson();
    }
    
    public static Optional<Gson> optionalGson() {
        return Optional.ofNullable(gson());
    }
}
