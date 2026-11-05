package org.example.inspection;

import java.util.Objects;

public class TestFile implements ProjectElement {
    private final String name;
    private final int linesOfCode;
    private final int testCount;

    public TestFile(String name, int linesOfCode, int testCount) {
        if (linesOfCode < 0 || testCount < 0) {
            throw new IllegalArgumentException("Lines of code and test count cannot be negative");
        }
        this.name = Objects.requireNonNull(name);
        this.linesOfCode = linesOfCode;
        this.testCount = testCount;
    }

    public String getName() {
        return name;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public int getTestCount() {
        return testCount;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}
