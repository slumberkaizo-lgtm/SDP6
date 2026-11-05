package org.example.inspection;

import java.util.Locale;

public class ProjectStatisticsVisitor implements ProjectVisitor {
    private int totalFiles;
    private int linesOfCode;
    private int testCount;
    private int dependencyCount;
    private int sourceFileCount;
    private int testFileCount;
    private int totalComplexity;

    @Override
    public void visit(SourceCodeFile file) {
        totalFiles++;
        sourceFileCount++;
        linesOfCode += file.getLinesOfCode();
        totalComplexity += file.getComplexity();
    }

    @Override
    public void visit(TestFile file) {
        totalFiles++;
        testFileCount++;
        linesOfCode += file.getLinesOfCode();
        testCount += file.getTestCount();
    }

    @Override
    public void visit(ConfigurationFile file) {
        totalFiles++;
    }

    @Override
    public void visit(DependencyFile file) {
        totalFiles++;
        dependencyCount += file.getDependencies().size();
    }

    @Override
    public void visit(Directory directory) {
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public int getTestCount() {
        return testCount;
    }

    public int getDependencyCount() {
        return dependencyCount;
    }

    public int getSourceFileCount() {
        return sourceFileCount;
    }

    public int getTestFileCount() {
        return testFileCount;
    }

    public double getAverageComplexity() {
        return sourceFileCount == 0 ? 0 : (double) totalComplexity / sourceFileCount;
    }

    public double getTestSourceRatio() {
        return sourceFileCount == 0 ? 0 : (double) testFileCount / sourceFileCount;
    }

    public String getReport() {
        return String.format(Locale.US,
                "Project Statistics\nTotal files: %d\nLines of code: %d\n"
                        + "Source files: %d\nTest files: %d\nTests: %d\nDependencies: %d\n"
                        + "Average complexity: %.2f\nTest/source ratio: %.2f",
                totalFiles, linesOfCode, sourceFileCount, testFileCount, testCount,
                dependencyCount, getAverageComplexity(), getTestSourceRatio());
    }
}
