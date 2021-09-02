package io.github.toberocat.improvedfactions.extentions;

import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.UpdateChecker;

public abstract class Extension {

    protected ExtensionRegistry registry;
    /**
     * Empty constructor needed, else not able to load .jar
     */
    public Extension() {}

    protected abstract ExtensionRegistry register();

    public final void Enable(ImprovedFactionsMain plugin) {
        registry = register();

        plugin.getServer().getConsoleSender().sendMessage("§7[Factions] §eLoading " + registry.getName() + " v" + registry.getVersion());
        OnEnable(plugin);
    }

    /**
     * This function is called when the extension is enabling.
     * This should add all the functionally needed in this extension
     * @param plugin the JavaPlugin
     */
    protected void OnEnable(ImprovedFactionsMain plugin) {

    }
    /**
     * This function is called when the extension is disabling.
     * This should remove all the functionally needed in this extension
     * @param plugin the JavaPlugin
     */
    protected void OnDisable(ImprovedFactionsMain plugin) {

    }

    public ExtensionRegistry getRegistry() {
        return registry;
    }
}
