package io.github.toberocat.improvedfactions.extentions;

public class ExtensionRegistry {

    private String name;
    private double version;

    public ExtensionRegistry(String name, double version) {
        this.name = name;
        this.version = version;
    }

    public double getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }
}
