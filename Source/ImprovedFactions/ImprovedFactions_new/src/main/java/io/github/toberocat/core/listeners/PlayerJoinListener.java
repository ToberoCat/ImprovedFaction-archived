package io.github.toberocat.core.listeners;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.calender.TimeCore;
import io.github.toberocat.core.utility.data.PersistentDataUtility;
import io.github.toberocat.core.utility.factions.members.FactionMemberManager;
import io.github.toberocat.core.utility.language.LangMessage;
import io.github.toberocat.core.utility.language.Language;
import io.github.toberocat.core.utility.settings.PlayerSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void Join(PlayerJoinEvent event) {
        AsyncCore.Run(() -> {
            Player player = event.getPlayer();

            if (MainIF.getIF().isStandby() && player.hasPermission("factions.messages.standby")) {
                Language.sendMessage(LangMessage.PLUGIN_STANDBY_MESSAGE, player);
            }

            if (!player.getPersistentDataContainer().has(PersistentDataUtility.PLAYER_FACTION_REGISTRY, PersistentDataType.STRING)) {
                GetPlayerFaction(player);
            }

            if (player.hasPermission("factions.messages.review")) {
                SendReviewMessage(player);
            }

            LoadFaction();

            FactionMemberManager.PlayerJoin(event);
            PlayerSettings.PlayerJoined(player.getUniqueId());
        });
    }

    private void SendReviewMessage(Player player) {
        int[] time = TimeCore.dateStringToDate(MainIF.getConfigManager().getValue("general.newestConfig"));

        if (time == null) {
            MainIF.getIF().SaveShutdown("&cLooks like someone tinkered with the &6general.newestConfig&c. Please set this value back to what is was, because this value should only be modified by the system");
            return;
        }
        int difference = Math.abs(TimeCore.getDays(TimeCore.getDateDifference(time, TimeCore.getMachineDate())));
        if (difference >= 5 && difference <= 7) {
            player.sendMessage(Language.getPrefix() + Language.format(
                    "&fHey, it looks like you are using ImprovedFactions&f now for quite some time. How about you write a review on https://www.spigotmc.org/resources/improved-factions.95617/. " +
                            "It doesn't need to be positive, but it would help me a lot. &8Tip:&7 This message is only visible for you and will disappear after some days"));
        }
    }

    private void LoadFaction() { // Get Player faction and load faction if player is in unloaded one

    }

    private void GetPlayerFaction(Player player) { // 1. Option: Player is in no faction, 2. Option: Server recently switched to v1.0

    }
}
