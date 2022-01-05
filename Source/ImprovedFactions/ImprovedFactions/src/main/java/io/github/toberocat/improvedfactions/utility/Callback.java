package io.github.toberocat.improvedfactions.utility;

import com.comphenix.protocol.PacketType;
import io.github.toberocat.improvedfactions.factions.Faction;
import org.bukkit.entity.Player;

import java.util.List;

public interface Callback {
    boolean CallBack(Faction faction, Player player, Object object);

    static boolean GetCallbacks(List<Callback> callbacks, Faction faction, Player player, Object object) {
        boolean result = true;
        for (Callback callback : callbacks) {
            if (!callback.CallBack(faction, player, object)) {
                result = false;
            }
        }
        return result;
    }
}
