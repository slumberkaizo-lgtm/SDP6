package org.example.inspection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComplianceVisitorTest {
    @Test
    void sourceCredentialsAndMissingTestsAreSeparateViolations() {
        ComplianceVisitor visitor = new ComplianceVisitor();

        new SourceCodeFile("Service.java", 20, 2, true, false).accept(visitor);

        assertEquals(2, visitor.getViolations().size());
        assertFalse(visitor.isCompliant());
        assertTrue(visitor.getReport().contains("Service.java"));
    }

    @Test
    void exposedConfigurationCredentialsViolateCompliance() {
        ComplianceVisitor visitor = new ComplianceVisitor();

        new ConfigurationFile("application.properties", true, false).accept(visitor);

        assertEquals(1, visitor.getViolations().size());
        assertFalse(visitor.isCompliant());
    }

    @Test
    void nullEmptyAndBlankDependencyVersionsAreRejected() {
        DependencyFile file = new DependencyFile("pom.xml", List.of(
                new Dependency("missing", null, false, false),
                new Dependency("empty", "", false, false),
                new Dependency("blank", "   ", false, false),
                new Dependency("valid", "1.0", false, false)
        ));
        ComplianceVisitor visitor = new ComplianceVisitor();

        file.accept(visitor);

        assertEquals(3, visitor.getViolations().size());
        assertFalse(visitor.isCompliant());
    }

    @Test
    void prohibitedDependencyViolatesCompliance() {
        ComplianceVisitor visitor = new ComplianceVisitor();
        DependencyFile file = new DependencyFile("pom.xml", List.of(
                new Dependency("blocked-library", "1.0", false, true)
        ));

        file.accept(visitor);

        assertEquals(1, visitor.getViolations().size());
        assertFalse(visitor.isCompliant());
        assertTrue(visitor.getReport().contains("blocked-library"));
    }

    @Test
    void compliantProjectHasNoViolations() {
        Directory project = new Directory("project");
        project.add(new SourceCodeFile("Service.java", 20, 2, false, true));
        project.add(new TestFile("ServiceTest.java", 10, 1));
        project.add(new ConfigurationFile("application.properties", false, false));
        project.add(new DependencyFile("pom.xml", List.of(
                new Dependency("safe-library", "1.0", false, false)
        )));
        ComplianceVisitor visitor = new ComplianceVisitor();

        project.accept(visitor);

        assertTrue(visitor.getViolations().isEmpty());
        assertTrue(visitor.isCompliant());
    }
}
