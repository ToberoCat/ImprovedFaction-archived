package io.github.toberocat.improvedfactions.commands.factionCommands;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.utility.ChunkUtils;
import io.github.toberocat.improvedfactions.utility.TCallback;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;

import java.util.List;

public class MapSubCommand extends SubCommand {
    public MapSubCommand() {
        super("map", LangMessage.MAP_DESCRIPTION);
    }

    @Override
    protected void CommandExecute(Player player, String[] args) {
        Chunk center = player.getLocation().getChunk();
        int dst = ImprovedFactionsMain.getPlugin().getConfig().getInt("general.mapViewDistance");
        int leftTopX = center.getX() - dst/2;
        int leftTopZ = center.getZ() - dst/2;

        int rightDownX = center.getX() + dst/2;
        int rightDownZ = center.getZ() + dst/2;

        Chunk[][] chunks = new Chunk[dst][dst];

        for (int x = leftTopX; x < rightDownX; x++) {
            for (int z = leftTopZ; z < rightDownZ; z++) {
                chunks[x-leftTopX][z-leftTopZ] = player.getLocation().getWorld().getChunkAt(x, z);
            }
        }

        TextComponent map = new TextComponent();
        for (int i = 0; i < dst; i++) {
            for (int j = 0; j < dst; j++) {
                getChunk(chunks[i][j],player, textComponent -> {
                    map.addExtra(textComponent);
                });
            }
            map.addExtra("\n");
        }
        player.spigot().sendMessage(map);
    }

    @Override
    protected List<String> CommandTab(Player player, String[] args) {
        return null;
    }

    private void getChunk(Chunk chunk, Player player, TCallback<TextComponent> callback) {
        Faction faction = ChunkUtils.GetFactionClaimedChunk(chunk);
            String color = "";
            String hover;
            if (faction == null) { //Wildness
                color = "§2";
                hover = color + "Wildness";
            }
            else {
                color = faction == FactionUtils.getFaction(player) ? "§a" : "§c";
                hover = color + faction.getDisplayName();
            }

            if (player.getLocation().getChunk() == chunk) {
                color = "§f";
                hover = color +  "You";
            }

            TextComponent com = new TextComponent(color + "■");
            com.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                    hover).create()));
            callback.Callback(com);
    }
}
