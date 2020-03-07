package kernbeisser.Enums;

import kernbeisser.Useful.Named;

public enum ContainerDefinition implements Named {
    STATIC("Fest"),
    UNKNOWN("Unbekannt"),
    ROUNDED("ungefähr");
    private final String name;

    ContainerDefinition(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getName() {
        return null;
    }
}
