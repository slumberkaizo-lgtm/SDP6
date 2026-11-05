package org.example.inspection;

public class SourceCodeFile implements ProjectElement {
    private final String name;
    private final int lines;
    private final int complexity;
    private final boolean credentials;
    private final boolean tested;

    public SourceCodeFile(String name, int lines, int complexity, boolean credentials, boolean tested) {
        if (lines < 0 || complexity < 0) {
            throw new IllegalArgumentException("Metrics cannot be negative");
        }
        this.name = name;
        this.lines = lines;
        this.complexity = complexity;
        this.credentials = credentials;
        this.tested = tested;
    }

    public String getName() {
        return name;
    }

    public int getLines() {
        return lines;
    }

    public int getComplexity() {
        return complexity;
    }

    public boolean getCredentials() {
        return credentials;
    }

    public boolean getTested() {
        return tested;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}

