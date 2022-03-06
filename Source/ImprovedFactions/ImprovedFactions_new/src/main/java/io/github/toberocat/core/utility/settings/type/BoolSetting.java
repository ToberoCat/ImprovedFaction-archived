package io.github.toberocat.core.utility.settings.type;

import io.github.toberocat.core.utility.settings.type.Setting;
import org.bukkit.inventory.ItemStack;

public class BoolSetting extends Setting<Boolean> {
    public BoolSetting(boolean selected, ItemStack display) {
        super(selected, display);
    }
}
