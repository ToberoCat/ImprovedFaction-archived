package io.github.toberocat.core.utility.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.toberocat.MainIF;
import io.github.toberocat.core.gui.faction.FactionSettingsGui;
import io.github.toberocat.core.gui.faction.MemberGui;
import io.github.toberocat.core.gui.faction.OnlineGUI;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.factions.Faction;
import io.github.toberocat.core.utility.factions.FactionUtility;
import io.github.toberocat.core.utility.factions.OpenType;
import io.github.toberocat.core.utility.gui.GUISettings;
import io.github.toberocat.core.utility.language.Language;
import io.github.toberocat.core.utility.settings.type.CallbackSettings;
import io.github.toberocat.core.utility.settings.type.EnumSetting;
import io.github.toberocat.core.utility.settings.type.Setting;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class FactionSettings {
    @JsonIgnore
    public static final Map<String, Setting> DEFAULT_SETTINGS = new HashMap<>();

    @JsonIgnore
    private Map<String, Setting> factionSettings;

    public FactionSettings() {
        this.factionSettings = new HashMap<>(DEFAULT_SETTINGS);
    }

    @JsonIgnore
    public static void register() {
        DEFAULT_SETTINGS.put("openType", new EnumSetting(OpenType.values(),
                Utility.createItem(Material.END_PORTAL_FRAME, "&eChange join type", new String[] {
                        "&8Allow other people to", "&8find you using", "&8/f join"
                })));

        DEFAULT_SETTINGS.put("change?rename", new CallbackSettings((player) -> {
            Faction faction = FactionUtility.getPlayerFaction(player);
            if (faction == null) return;

            new AnvilGUI.Builder().onClose((user) -> {})
                    .onComplete((user, text) -> {
                        faction.setDisplayName(Language.format(text.replaceAll(" ", "_")));
                        return AnvilGUI.Response.close();
                    }).text(faction.getDisplayName())
                    .itemLeft(new ItemStack(Material.GRAY_BANNER))
                    .title("§eChange your name")
                    .plugin(MainIF.getIF())
                    .open(player);
        }, "Input", Utility.createItem(Material.OAK_SIGN, "&eRename faction", new String[] { "&8Change your name",
                "&7Tip: Use color codes" })));

        DEFAULT_SETTINGS.put("change?motd", new CallbackSettings((player) -> {
            Faction faction = FactionUtility.getPlayerFaction(player);
            if (faction == null) return;

            new AnvilGUI.Builder().onClose((user) -> {})
                    .onComplete((user, text) -> {
                        faction.setMotd(Language.format(text));
                        return AnvilGUI.Response.close();
                    }).text(faction.getMotd())
                    .itemLeft(new ItemStack(Material.GRAY_BANNER))
                    .title("§eChange your motd")
                    .plugin(MainIF.getIF())
                    .open(player);
        }, "Input", Utility.createItem(Material.OAK_SIGN, "&eSet Motd", new String[] { "&8Change your motd",
                "&7Tip: Use color codes" })));
        DEFAULT_SETTINGS.put("open?members", new CallbackSettings((player) -> {
            Faction faction = FactionUtility.getPlayerFaction(player);
            if (faction == null) return;

            AsyncCore.runLaterSync(0, () -> {
                new MemberGui(player, faction, new GUISettings().setQuitIcon(true).setQuitCallback(() ->
                        new FactionSettingsGui(player)));
            });
        }, "Gui", Utility.getSkull("https://textures.minecraft.net/texture/a05a32bbab24f84db679df637b7769fbf8f26e8f5765d0fbdbdea288cb6548f8",
                1, "Manage your members", "&8Manage your members",
                "&7Tip: Click on the head to manage them individually")));

        DEFAULT_SETTINGS.put("open?online", new CallbackSettings((player) -> {
            Faction faction = FactionUtility.getPlayerFaction(player);
            if (faction == null) return;

            AsyncCore.runLaterSync(0, () -> {
                new OnlineGUI(player, faction, new GUISettings().setQuitIcon(true).setQuitCallback(() ->
                        new FactionSettingsGui(player)));
            });
        }, "Gui", Utility.getSkull("http://textures.minecraft.net/texture/fa6d51c22c8958285c00aaaf93b621c15be10aa04838afe1d89cd9c3603144df",
                1, "See online members", "&8See your members online time")));
    }

    public Map<String, Setting> getFactionSettings() {
        return factionSettings;
    }

    public void setFactionSettings(Map<String, Setting> factionSettings) {
        this.factionSettings = factionSettings;
    }
}
