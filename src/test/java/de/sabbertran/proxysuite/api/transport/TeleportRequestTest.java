package de.sabbertran.proxysuite.api.transport;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapterFactory;
import de.sabbertran.proxysuite.libraries.com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.sabbertran.proxysuite.utils.Location;
import de.sabbertran.proxysuite.utils.Regestry;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.*;

public class TeleportRequestTest {
    
    public TeleportRequestTest() {
    }

    @BeforeAll
    public static void initializeRegestry() {
        TypeAdapterFactory teleportTargetTypeAdapterFactory = RuntimeTypeAdapterFactory.of(TeleportTarget.class)
                .registerSubtype(PlayerTarget.class)
                .registerSubtype(LocationTarget.class);
        Gson gson = new GsonBuilder()
                .registerTypeAdapterFactory(teleportTargetTypeAdapterFactory)
                .create();
        Regestry.getInstance().registerGson(gson);
    }
    
    @AfterAll
    public static void tearDownRegestry() {
        Regestry.getInstance().registerGson(null);
    }
    
    @Test
    public void testToJsonAndBackLocationTarget() {
        UUID uuid = UUID.randomUUID();
        Location location = new Location("someserver", "myworld", 123.73, 75.38, -6334.76, 18.4f, 74.1f);
        TeleportTarget target = new LocationTarget(location);
        Instant warmup = Instant.now().plus(7, ChronoUnit.SECONDS);
        
        TeleportRequest original = new TeleportRequest(uuid, target, warmup);

        String json = Regestry.gson().toJson(original);
        TeleportRequest copy = Regestry.gson().fromJson(json, TeleportRequest.class);
        
        assertEquals(original, copy);
    }

    @Test
    public void testToJsonAndBackPlayerTarget() {
        UUID teleported = UUID.randomUUID();
        UUID player = UUID.randomUUID();
        TeleportTarget target = new PlayerTarget(player);
        Instant warmup = Instant.now().plus(7, ChronoUnit.SECONDS);
        
        TeleportRequest original = new TeleportRequest(teleported, target, warmup);

        String json = Regestry.gson().toJson(original);
        TeleportRequest copy = Regestry.gson().fromJson(json, TeleportRequest.class);
        
        assertEquals(original, copy);
    }
}
