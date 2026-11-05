package org.example.inspection;

import java.util.Locale;

public class ProjectStatisticsVisitor implements ProjectVisitor {
    private int files;
    private int lines;
    private int sources;
    private int testFiles;
    private int tests;
    private int dependencies;
    private int complexity;

    @Override
    public void visit(SourceCodeFile file) {
        files++;
        sources++;
        lines += file.getLines();
        complexity += file.getComplexity();
    }

    @Override
    public void visit(TestFile file) {
        files++;
        testFiles++;
        lines += file.getLines();
        tests += file.getTestCount();
    }

    @Override
    public void visit(ConfigurationFile file) {
        files++;
    }

    @Override
    public void visit(DependencyFile file) {
        files++;
        dependencies += file.getDependencies().size();
    }

    @Override
    public void visit(Directory directory) {
    }

    public int getFiles() {
        return files;
    }

    public int getLines() {
        return lines;
    }

    public int getSources() {
        return sources;
    }

    public int getTestFiles() {
        return testFiles;
    }

    public int getTests() {
        return tests;
    }

    public int getDependencies() {
        return dependencies;
    }

    public double getAverageComplexity() {
        return sources == 0 ? 0 : (double) complexity / sources;
    }

    public double getTestSourceRatio() {
        return sources == 0 ? 0 : (double) testFiles / sources;
    }

    public String getReport() {
        return String.format(Locale.US,
                "Project Statistics\nFiles: %d\nLines: %d\nSource files: %d\nTest files: %d"
                        + "\nTests: %d\nDependencies: %d\nAverage complexity: %.2f\nTest/source ratio: %.2f",
                files, lines, sources, testFiles, tests, dependencies,
                getAverageComplexity(), getTestSourceRatio());
    }
}

