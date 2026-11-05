package org.example.inspection;

public class ConfigurationFile implements ProjectElement {
    private final String name;
    private final boolean credentials;
    private final boolean insecure;

    public ConfigurationFile(String name, boolean credentials, boolean insecure) {
        this.name = name;
        this.credentials = credentials;
        this.insecure = insecure;
    }

    public String getName() {
        return name;
    }

    public boolean getCredentials() {
        return credentials;
    }

    public boolean getInsecure() {
        return insecure;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}

