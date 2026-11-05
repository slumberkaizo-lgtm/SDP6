package org.example.inspection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectStatisticsVisitorTest {
    @Test
    void calculatesStatisticsForEveryFileType() {
        Directory project = new Directory("project");
        project.add(new SourceCodeFile("Payment.java", 120, 8, false, true));
        project.add(new SourceCodeFile("User.java", 80, 5, false, true));
        project.add(new TestFile("PaymentTest.java", 50, 4));
        project.add(new TestFile("UserTest.java", 30, 2));
        project.add(new ConfigurationFile("application.properties", false, false));
        project.add(new DependencyFile("pom.xml", List.of(
                new Dependency("first", "1.0", false, false),
                new Dependency("second", "2.0", false, false)
        )));
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();

        project.accept(visitor);

        assertEquals(6, visitor.getTotalFiles());
        assertEquals(280, visitor.getLinesOfCode());
        assertEquals(6, visitor.getTestCount());
        assertEquals(2, visitor.getDependencyCount());
        assertEquals(2, visitor.getSourceFileCount());
        assertEquals(2, visitor.getTestFileCount());
        assertEquals(6.5, visitor.getAverageComplexity(), 0.0001);
        assertEquals(1.0, visitor.getTestSourceRatio(), 0.0001);
    }

    @Test
    void nestedFilesAreCountedExactlyOnce() {
        Directory project = new Directory("project");
        Directory src = new Directory("src");
        Directory services = new Directory("services");
        services.add(new SourceCodeFile("Service.java", 25, 3, false, true));
        src.add(services);
        project.add(src);
        project.add(new TestFile("ServiceTest.java", 15, 2));
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();

        project.accept(visitor);

        assertEquals(2, visitor.getTotalFiles());
        assertEquals(40, visitor.getLinesOfCode());
        assertEquals(2, visitor.getTestCount());
        assertEquals(3.0, visitor.getAverageComplexity(), 0.0001);
    }

    @Test
    void emptyDirectoryProducesZeroStatistics() {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();

        new Directory("empty").accept(visitor);

        assertEquals(0, visitor.getTotalFiles());
        assertEquals(0, visitor.getLinesOfCode());
        assertEquals(0, visitor.getTestCount());
        assertEquals(0, visitor.getDependencyCount());
        assertEquals(0.0, visitor.getAverageComplexity());
        assertEquals(0.0, visitor.getTestSourceRatio());
    }

    @Test
    void projectWithoutSourceFilesHasZeroRatioAndComplexity() {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();

        new TestFile("OnlyTest.java", 10, 3).accept(visitor);

        assertEquals(1, visitor.getTotalFiles());
        assertEquals(3, visitor.getTestCount());
        assertEquals(0, visitor.getSourceFileCount());
        assertEquals(0.0, visitor.getAverageComplexity());
        assertEquals(0.0, visitor.getTestSourceRatio());
    }
}
