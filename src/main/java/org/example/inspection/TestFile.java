package org.example.inspection;

public class TestFile implements ProjectElement {
    private final String name;
    private final int lines;
    private final int testCount;

    public TestFile(String name, int lines, int testCount) {
        if (lines < 0 || testCount < 0) {
            throw new IllegalArgumentException("Metrics cannot be negative");
        }
        this.name = name;
        this.lines = lines;
        this.testCount = testCount;
    }

    public String getName() {
        return name;
    }

    public int getLines() {
        return lines;
    }

    public int getTestCount() {
        return testCount;
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
    }
}

