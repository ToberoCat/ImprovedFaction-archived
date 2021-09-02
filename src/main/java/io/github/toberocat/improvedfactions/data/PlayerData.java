package io.github.toberocat.improvedfactions.data;

import io.github.toberocat.improvedfactions.factions.Faction;
import org.bukkit.Chunk;

public class PlayerData {

    public Display display;
    public ChunkData chunkData;

    public Faction playerFaction;

    public PlayerData() {
        display = new Display();
        chunkData = new ChunkData();
    }

    public class Display {
        public boolean alreadyDisplayedRegion = false;
    }

    public class ChunkData {
        public boolean isInClaimedChunk = false;
        public String factionRegistry = "";
    }
}

