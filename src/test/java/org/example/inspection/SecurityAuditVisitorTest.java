package org.example.inspection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SecurityAuditVisitorTest {
    @Test
    void detectsCredentialsInSourceAndConfiguration() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();

        new SourceCodeFile("Service.java", 20, 2, true, true).accept(visitor);
        new ConfigurationFile("application.properties", true, false).accept(visitor);

        assertEquals(2, visitor.getCriticalIssueCount());
        assertEquals(2, visitor.getIssues().size());
        assertTrue(visitor.getReport().contains("Service.java"));
        assertTrue(visitor.getReport().contains("application.properties"));
    }

    @Test
    void detectsOnlyVulnerableDependencies() {
        DependencyFile file = new DependencyFile("pom.xml", List.of(
                new Dependency("safe-library", "1.0", false, false),
                new Dependency("old-library", "1.0", true, false)
        ));
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();

        file.accept(visitor);

        assertEquals(1, visitor.getCriticalIssueCount());
        assertEquals(1, visitor.getIssues().size());
        assertTrue(visitor.getReport().contains("old-library"));
    }

    @Test
    void detectsInsecureConfiguration() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();

        new ConfigurationFile("application.properties", false, true).accept(visitor);

        assertEquals(1, visitor.getCriticalIssueCount());
        assertEquals(1, visitor.getIssues().size());
    }

    @Test
    void complexityAboveFifteenCreatesWarningWithoutCriticalIssue() {
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();

        new SourceCodeFile("Boundary.java", 20, 15, false, true).accept(visitor);
        new SourceCodeFile("Risky.java", 20, 16, false, true).accept(visitor);

        assertEquals(1, visitor.getIssues().size());
        assertEquals(0, visitor.getCriticalIssueCount());
        assertTrue(visitor.getReport().contains("Risky.java"));
    }

    @Test
    void safeProjectHasNoSecurityIssues() {
        Directory project = new Directory("project");
        project.add(new SourceCodeFile("Service.java", 20, 2, false, true));
        project.add(new TestFile("ServiceTest.java", 10, 1));
        project.add(new ConfigurationFile("application.properties", false, false));
        project.add(new DependencyFile("pom.xml", List.of(
                new Dependency("safe-library", "1.0", false, false)
        )));
        SecurityAuditVisitor visitor = new SecurityAuditVisitor();

        project.accept(visitor);

        assertTrue(visitor.getIssues().isEmpty());
        assertEquals(0, visitor.getCriticalIssueCount());
    }
}
