package io.github.toberocat.core.extensions.list;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.toberocat.core.extensions.ExtensionObject;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.version.Version;

import java.io.IOException;
import java.net.URL;

public class ExtensionListLoader {

    private static ExtensionObject[] extensionList;

    public static ExtensionObject[] readList() {
        if (extensionList == null) loadList().await();
        return extensionList;
    }

    public static AsyncCore<ExtensionObject[]> loadList() {
        return AsyncCore.Run(() -> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                extensionList = mapper.readValue(new URL("https://raw.githubusercontent.com/ToberoCat/ImprovedFaction/master/extensions.json"),
                        ExtensionObject[].class);
            } catch (IOException e) {
                Utility.except(e);
            }
            return extensionList;
        });
    }

    public static void unloadList() {
        extensionList = null;
    }

    public static AsyncCore<Version> getExtensionVersion(String filename) {
        return AsyncCore.Run(() -> {
            boolean unload = false;
            if (extensionList == null) {
                unload = true;
                loadList();
            }

            for (ExtensionObject obj : extensionList) {
                if (obj.getFileName().equals(filename)) {
                    return obj.getNewestVersion();
                }
            }

            if (unload) unloadList();

            return null;
        });

    }
}
