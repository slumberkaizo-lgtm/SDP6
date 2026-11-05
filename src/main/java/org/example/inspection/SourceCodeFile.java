package org.example.inspection;

import java.util.Objects;

public class SourceCodeFile implements ProjectElement {
    private final String name;
    private final int linesOfCode;
    private final int complexity;
    private final boolean hardcodedCredentials;
    private final boolean hasTests;

    public SourceCodeFile(String name, int linesOfCode, int complexity,
                          boolean hardcodedCredentials, boolean hasTests) {
        if (linesOfCode < 0 || complexity < 0) {
            throw new IllegalArgumentException("Lines of code and complexity cannot be negative");
        }
        this.name = Objects.requireNonNull(name);
        this.linesOfCode = linesOfCode;
        this.complexity = complexity;
        this.hardcodedCredentials = hardcodedCredentials;
        this.hasTests = hasTests;
    }

    public String getName() {
        return name;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public int getComplexity() {
        return complexity;
    }

    public boolean hasHardcodedCredentials() {
        return hardcodedCredentials;
    }

    public boolean hasTests() {
        return hasTests;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
