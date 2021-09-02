package io.github.toberocat.improvedfactions.extentions;

import java.net.URL;

public class ExtensionObject {
    private String name;
    private String description;
    private URL download;

    public ExtensionObject() {}

    public ExtensionObject(String name, String description, URL download) {
        this.name = name;
        this.description = description;
        this.download = download;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public URL getDownload() {
        return download;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDownload(URL download) {
        this.download = download;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ExtensionObject{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", download=" + download +
                '}';
    }
}
