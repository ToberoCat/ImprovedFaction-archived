package io.github.toberocat.core.utility.claim;

import io.github.toberocat.core.utility.data.PersistentDataUtility;
import io.github.toberocat.core.utility.factions.Faction;
import io.github.toberocat.core.utility.Result;
import org.bukkit.Chunk;
import org.bukkit.persistence.PersistentDataType;

public class ClaimManager {

    public static final String SAFEZONE_REGISTRY = "__glb:safezone__";
    public static final String WARZONE_REGISTRY = "__glb:warzone__";
    public static final String UNCLAIMABLE_REGISTRY = "__glb:unclaimable__";

    public static final String UNCLAIMED_CHUNK_REGISTRY = "NONE";

    public Result ClaimChunk(Faction faction, Chunk chunk) {
        return ProtectChunk(faction.getRegistryName(), chunk);
    }

    public Result ProtectChunk(String registry, Chunk chunk) {
        if (PersistentDataUtility.Has(PersistentDataUtility.FACTION_CLAIMED_KEY,
                PersistentDataType.STRING, chunk.getPersistentDataContainer())) {
            return new Result(false).setMessages("CHUNK_ALREADY_PROTECTED", "&c&lHey!&f The chunk you want to claim got already claimed");
        }

        PersistentDataUtility.Write(PersistentDataUtility.FACTION_CLAIMED_KEY,
                PersistentDataType.STRING,
                registry,
                chunk.getPersistentDataContainer());
        return new Result(true);
    }

    public Result<String> RemoveProtection(Chunk chunk) {
        if (!PersistentDataUtility.Has(PersistentDataUtility.FACTION_CLAIMED_KEY,
                PersistentDataType.STRING, chunk.getPersistentDataContainer())) {
            return  new Result(true);
        }
        String claimRegistry = PersistentDataUtility.Read(PersistentDataUtility.FACTION_CLAIMED_KEY,
                PersistentDataType.STRING,
                chunk.getPersistentDataContainer());

        PersistentDataUtility.Write(PersistentDataUtility.FACTION_CLAIMED_KEY,
                PersistentDataType.STRING,
                UNCLAIMED_CHUNK_REGISTRY,
                chunk.getPersistentDataContainer());

        return new Result<String>(true).setPaired(claimRegistry);
    }
}
