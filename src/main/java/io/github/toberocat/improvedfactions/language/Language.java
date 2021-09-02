package io.github.toberocat.improvedfactions.language;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Language {

    private static final Pattern pattern = Pattern.compile("(#[a-fA-F0-9]{6})");

    public static String getPrefix() {
        return format(ImprovedFactionsMain.getPlugin()
                        .getLanguageData().getConfig().getString("prefix") + " ");
    }

    public static String format(String msg) {
        if (Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")) {
            Matcher matcher = pattern.matcher(msg);
            while (matcher.find()) {
                String color = msg.substring(matcher.start(), matcher.end());
                msg = msg.replace(color, ChatColor.of(color) + "");
                matcher = pattern.matcher(msg);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
}
