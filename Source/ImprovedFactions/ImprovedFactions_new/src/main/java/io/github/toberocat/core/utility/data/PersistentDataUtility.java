package io.github.toberocat.core.utility.data;

import io.github.toberocat.MainIF;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PersistentDataUtility {
    public static final NamespacedKey FACTION_CLAIMED_KEY = new NamespacedKey(MainIF.getIF(), "faction-claimed");
    public static final NamespacedKey PLAYER_FACTION_REGISTRY = new NamespacedKey(MainIF.getIF(), "if-joined-faction");

    public static <T extends PersistentDataContainer, E> void Write(NamespacedKey key, PersistentDataType type, E value, T object) {
        object.set(FACTION_CLAIMED_KEY, type, value);
    }

    public static <T extends PersistentDataContainer, E> E Read(NamespacedKey key, PersistentDataType type, T object) {
        return (E) object.get(key, type);
    }

    public static <T extends PersistentDataContainer, E> boolean Has(NamespacedKey key, PersistentDataType type, T object) {
        return object.has(key, type);
    }
}
