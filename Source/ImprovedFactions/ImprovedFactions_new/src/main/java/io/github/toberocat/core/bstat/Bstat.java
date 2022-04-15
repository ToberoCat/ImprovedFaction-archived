package io.github.toberocat.core.bstat;

import io.github.toberocat.MainIF;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.gitreport.GitReport;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class Bstat {
    public static void register(MainIF plugin) {
        try {
            Metrics metrics = new Metrics(plugin, 14810);

            MainIF.LogMessage(Level.INFO, "&aSuccessfully loaded &6bstat &adata collection");
        } catch (Exception e) {
            MainIF.LogMessage(Level.INFO, "&cCouldn't load bstat");
        }
    }
}
