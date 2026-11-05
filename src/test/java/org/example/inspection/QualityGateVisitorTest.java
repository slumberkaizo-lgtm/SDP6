package org.example.inspection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QualityGateVisitorTest {
    @Test
    void passesAtExactComplexityAndTestRatioBoundaries() {
        Directory project = new Directory("project");
        for (int i = 1; i <= 5; i++) {
            project.add(new SourceCodeFile("Service" + i + ".java", 20, 10, false, true));
        }
        project.add(new TestFile("FirstTest.java", 10, 2));
        project.add(new TestFile("SecondTest.java", 10, 2));
        QualityGateVisitor visitor = new QualityGateVisitor();

        project.accept(visitor);

        assertTrue(visitor.isSecurityPassed());
        assertTrue(visitor.isComplexityPassed());
        assertTrue(visitor.isTestsPassed());
        assertTrue(visitor.isPassed());
    }

    @Test
    void criticalSecurityIssueFailsOtherwiseHealthyProject() {
        Directory project = new Directory("project");
        project.add(new SourceCodeFile("Service.java", 20, 8, true, true));
        project.add(new TestFile("ServiceTest.java", 10, 2));
        QualityGateVisitor visitor = new QualityGateVisitor();

        project.accept(visitor);

        assertFalse(visitor.isSecurityPassed());
        assertTrue(visitor.isComplexityPassed());
        assertTrue(visitor.isTestsPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void highAverageComplexityFailsOtherwiseHealthyProject() {
        Directory project = new Directory("project");
        project.add(new SourceCodeFile("Service.java", 20, 11, false, true));
        project.add(new TestFile("ServiceTest.java", 10, 2));
        QualityGateVisitor visitor = new QualityGateVisitor();

        project.accept(visitor);

        assertTrue(visitor.isSecurityPassed());
        assertFalse(visitor.isComplexityPassed());
        assertTrue(visitor.isTestsPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void lowTestRatioFailsEvenWithManyTestMethods() {
        Directory project = new Directory("project");
        for (int i = 1; i <= 3; i++) {
            project.add(new SourceCodeFile("Service" + i + ".java", 20, 5, false, true));
        }
        project.add(new TestFile("ServicesTest.java", 100, 30));
        QualityGateVisitor visitor = new QualityGateVisitor();

        project.accept(visitor);

        assertTrue(visitor.isSecurityPassed());
        assertTrue(visitor.isComplexityPassed());
        assertFalse(visitor.isTestsPassed());
        assertFalse(visitor.isPassed());
    }

    @Test
    void emptyProjectFailsTestRequirement() {
        QualityGateVisitor visitor = new QualityGateVisitor();

        new Directory("empty").accept(visitor);

        assertTrue(visitor.isSecurityPassed());
        assertTrue(visitor.isComplexityPassed());
        assertFalse(visitor.isTestsPassed());
        assertFalse(visitor.isPassed());
    }
}
