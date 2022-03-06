package io.github.toberocat.core.utility.settings.type;

import io.github.toberocat.core.utility.settings.type.Setting;

public class HiddenSetting<T> extends Setting<T> {

    public HiddenSetting() {}
    public HiddenSetting(T t) {
        super(t, null);
    }
}
