package io.github.toberocat.improvedfactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.toberocat.improvedfactions.extentions.list.ExtensionList;

import java.io.IOException;
import java.net.URL;

public class UpdateChecker {
    private String version;
    private URL versionJson;

    public UpdateChecker(String version, URL versionJson) {
        this.version = version;
        this.versionJson = versionJson;
    }

    public String getVersion() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode version = mapper.readValue(versionJson, ObjectNode.class);
        if (version.has("version")) {
            return version.get("version").toString().replace("\"", "");
        }
        return "";
    }

    public boolean isNewestVersion() throws IOException {
        return getVersion().equals(version);
    }
}
