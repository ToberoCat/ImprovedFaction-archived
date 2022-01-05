package io.github.toberocat.core.utility.callbacks;

import io.github.toberocat.MainIF;

public interface ExceptionCallback extends Callback {
    default void Callback() {
        try {
            ECallback();
        } catch (Exception e) {
            if (MainIF.getConfigManager().getValue("general.printStacktrace")) e.printStackTrace();
            MainIF.getIF().SaveShutdown(e.getMessage());
        }
    }
    void ECallback() throws Exception;
}
