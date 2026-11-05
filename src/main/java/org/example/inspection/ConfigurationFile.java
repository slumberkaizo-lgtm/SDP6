package org.example.inspection;

import java.util.Objects;

public class ConfigurationFile implements ProjectElement {
    private final String name;
    private final boolean exposedCredentials;
    private final boolean insecureConfiguration;

    public ConfigurationFile(String name, boolean exposedCredentials, boolean insecureConfiguration) {
        this.name = Objects.requireNonNull(name);
        this.exposedCredentials = exposedCredentials;
        this.insecureConfiguration = insecureConfiguration;
    }

    public String getName() {
        return name;
    }

    public boolean hasExposedCredentials() {
        return exposedCredentials;
    }

    public boolean isInsecureConfiguration() {
        return insecureConfiguration;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
