package org.example.inspection;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InspectionTest {
    private Directory project(int complexity, boolean credentials, boolean tested) {
        Directory root = new Directory("project");
        root.add(new SourceCodeFile("Service.java", 100, complexity, credentials, tested));
        root.add(new TestFile("ServiceTest.java", 40, 3));
        return root;
    }

    @Test
    void sourceCredentialsAreCritical() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        new SourceCodeFile("Service.java", 10, 2, true, true).accept(visitor);
        assertEquals(1, visitor.getCriticalIssues());
        assertTrue(visitor.getIssues().get(0).contains("Service.java"));
    }

    @Test
    void configurationHasTwoSeparateSecurityRules() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        new ConfigurationFile("app.properties", true, true).accept(visitor);
        assertEquals(2, visitor.getCriticalIssues());
        assertEquals(2, visitor.getIssues().size());
    }

    @Test
    void onlyVulnerableDependenciesAreSecurityIssues() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        new DependencyFile("pom.xml", List.of(
                new Dependency("safe", "1", false, false),
                new Dependency("old", "1", true, false))).accept(visitor);
        assertEquals(1, visitor.getCriticalIssues());
        assertTrue(visitor.getReport().contains("old"));
    }

    @Test
    void complexityWarningStartsAboveFifteen() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        new SourceCodeFile("Normal.java", 10, 15, false, true).accept(visitor);
        new SourceCodeFile("Risky.java", 10, 16, false, true).accept(visitor);
        assertEquals(1, visitor.getIssues().size());
        assertEquals(0, visitor.getCriticalIssues());
        assertTrue(visitor.getIssues().get(0).contains("Risky.java"));
    }

    @Test
    void safeProjectHasNoIssues() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();
        project(5, false, true).accept(visitor);
        assertTrue(visitor.getIssues().isEmpty());
    }

    @Test
    void statisticsIncludeAllFileTypes() {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        DemoProject.create().accept(visitor);
        assertEquals(5, visitor.getFiles());
        assertEquals(320, visitor.getLines());
        assertEquals(2, visitor.getSources());
        assertEquals(1, visitor.getTestFiles());
        assertEquals(5, visitor.getTests());
        assertEquals(3, visitor.getDependencies());
        assertEquals(11.0, visitor.getAverageComplexity(), 0.0001);
        assertEquals(0.5, visitor.getTestSourceRatio(), 0.0001);
    }

    @Test
    void nestedDirectoriesVisitEachFileOnce() {
        Directory root = new Directory("root");
        Directory nested = new Directory("nested");
        nested.add(project(7, false, true));
        root.add(nested);
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        root.accept(visitor);
        assertEquals(2, visitor.getFiles());
        assertEquals(140, visitor.getLines());
        assertEquals(7.0, visitor.getAverageComplexity());
    }

    @Test
    void emptyDirectoryHasZeroStatistics() {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        new Directory("empty").accept(visitor);
        assertEquals(0, visitor.getFiles());
        assertEquals(0, visitor.getLines());
        assertEquals(0.0, visitor.getAverageComplexity());
        assertEquals(0.0, visitor.getTestSourceRatio());
    }

    @Test
    void testsWithoutSourcesDoNotDivideByZero() {
        ProjectStatisticsVisitor visitor = new ProjectStatisticsVisitor();
        new TestFile("OnlyTest.java", 20, 2).accept(visitor);
        assertEquals(2, visitor.getTests());
        assertEquals(0.0, visitor.getTestSourceRatio());
    }

    @Test
    void qualityPassesAtBothExactBoundaries() {
        Directory root = new Directory("project");
        for (int i = 0; i < 5; i++) {
            root.add(new SourceCodeFile("Service" + i, 10, 10, false, true));
        }
        root.add(new TestFile("FirstTest", 10, 1));
        root.add(new TestFile("SecondTest", 10, 1));
        QualityGateVisitor visitor = new QualityGateVisitor();
        root.accept(visitor);
        assertTrue(visitor.isPassed());
        assertTrue(visitor.getReport().contains("FINAL RESULT: PASSED"));
    }

    @Test
    void securityFailureBlocksRelease() {
        QualityGateVisitor visitor = new QualityGateVisitor();
        project(5, true, true).accept(visitor);
        assertFalse(visitor.isSecurityPassed());
        assertTrue(visitor.isComplexityPassed());
        assertTrue(visitor.isTestsPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void complexityFailureBlocksRelease() {
        QualityGateVisitor visitor = new QualityGateVisitor();
        project(11, false, true).accept(visitor);
        assertTrue(visitor.isSecurityPassed());
        assertFalse(visitor.isComplexityPassed());
        assertTrue(visitor.isTestsPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void manyTestMethodsDoNotReplaceEnoughTestFiles() {
        Directory root = project(5, false, true);
        root.add(new SourceCodeFile("Second.java", 10, 5, false, true));
        root.add(new SourceCodeFile("Third.java", 10, 5, false, true));
        root.add(new SourceCodeFile("Fourth.java", 10, 5, false, true));
        QualityGateVisitor visitor = new QualityGateVisitor();
        root.accept(visitor);
        assertTrue(visitor.isSecurityPassed());
        assertTrue(visitor.isComplexityPassed());
        assertFalse(visitor.isTestsPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void emptyProjectFailsReleaseGate() {
        QualityGateVisitor visitor = new QualityGateVisitor();
        new Directory("empty").accept(visitor);
        assertFalse(visitor.isPassed());
        assertFalse(visitor.isTestsPassed());
    }

    @Test
    void sourceCredentialsAndMissingTestsViolateCompliance() {
        ComplianceVisitor visitor = new ComplianceVisitor();
        new SourceCodeFile("Service.java", 10, 2, true, false).accept(visitor);
        assertEquals(2, visitor.getViolations().size());
        assertFalse(visitor.isCompliant());
    }

    @Test
    void exposedConfigurationViolatesCompliance() {
        ComplianceVisitor visitor = new ComplianceVisitor();
        new ConfigurationFile("app.properties", true, false).accept(visitor);
        assertEquals(1, visitor.getViolations().size());
    }

    @Test
    void nullEmptyAndBlankVersionsViolateCompliance() {
        ComplianceVisitor visitor = new ComplianceVisitor();
        new DependencyFile("pom.xml", List.of(
                new Dependency("a", null, false, false),
                new Dependency("b", "", false, false),
                new Dependency("c", "  ", false, false),
                new Dependency("d", "1", false, false))).accept(visitor);
        assertEquals(3, visitor.getViolations().size());
    }

    @Test
    void prohibitedDependencyViolatesCompliance() {
        ComplianceVisitor visitor = new ComplianceVisitor();
        new DependencyFile("pom.xml", List.of(
                new Dependency("blocked", "1", false, true))).accept(visitor);
        assertEquals(1, visitor.getViolations().size());
        assertTrue(visitor.getReport().contains("blocked"));
    }

    @Test
    void validProjectPassesCompliance() {
        Directory root = project(5, false, true);
        root.add(new ConfigurationFile("app.properties", false, false));
        root.add(new DependencyFile("pom.xml", List.of(
                new Dependency("safe", "1", false, false))));
        ComplianceVisitor visitor = new ComplianceVisitor();
        root.accept(visitor);
        assertTrue(visitor.isCompliant());
    }

    @Test
    void negativeMetricsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new SourceCodeFile("A", -1, 2, false, false));
        assertThrows(IllegalArgumentException.class,
                () -> new SourceCodeFile("A", 1, -2, false, false));
        assertThrows(IllegalArgumentException.class, () -> new TestFile("T", -1, 1));
        assertThrows(IllegalArgumentException.class, () -> new TestFile("T", 1, -1));
    }

    @Test
    void directoryRejectsNullElements() {
        assertThrows(NullPointerException.class, () -> new Directory("root").add(null));
    }
}

