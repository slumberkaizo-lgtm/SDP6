package org.example.inspection;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechnicalDebtVisitorTest {
    @Test
    void complexityDebtStartsAboveTen() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        new SourceCodeFile("Simple.java", 50, 10, false, true).accept(visitor);

        assertEquals(0, visitor.getTotalDebtMinutes());
        assertTrue(visitor.getFindings().isEmpty());

        new SourceCodeFile("Complex.java", 50, 11, false, true).accept(visitor);

        assertEquals(30, visitor.getTotalDebtMinutes());
        assertEquals(1, visitor.getFindings().size());
    }

    @Test
    void missingTestsAddTwoHours() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        new SourceCodeFile("Service.java", 20, 2, false, false).accept(visitor);

        assertEquals(120, visitor.getTotalDebtMinutes());
        assertEquals(1, visitor.getFindings().size());
    }

    @Test
    void onlyVulnerableDependenciesAddDebt() {
        DependencyFile file = new DependencyFile("pom.xml", List.of(
                new Dependency("safe", "1.0", false, false),
                new Dependency("vulnerable", "1.0", true, false),
                new Dependency("prohibited", "1.0", false, true)
        ));
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        file.accept(visitor);

        assertEquals(60, visitor.getTotalDebtMinutes());
        assertEquals(1, visitor.getFindings().size());
    }

    @Test
    void testAndConfigurationFilesDoNotAddDebt() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        new TestFile("ServiceTest.java", 40, 0).accept(visitor);
        new ConfigurationFile("application.properties", true, true).accept(visitor);

        assertEquals(0, visitor.getTotalDebtMinutes());
        assertTrue(visitor.getFindings().isEmpty());
    }

    @Test
    void demoCombinesAllThreeDebtRules() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        DemoProject.create().accept(visitor);

        assertEquals(420, visitor.getTotalDebtMinutes());
        assertEquals(3, visitor.getFindings().size());
    }

    @Test
    void emptyDirectoryHasNoDebt() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        new Directory("empty").accept(visitor);

        assertEquals(0, visitor.getTotalDebtMinutes());
        assertTrue(visitor.getFindings().isEmpty());
    }

    @Test
    void nestedDirectoriesAccumulateEachFindingOnce() {
        Directory project = new Directory("project");
        Directory src = new Directory("src");
        Directory module = new Directory("payments");
        module.add(new SourceCodeFile("PaymentService.java", 50, 12, false, false));
        src.add(module);
        project.add(src);
        project.add(new DependencyFile("pom.xml", List.of(
                new Dependency("legacy", "1.0", true, false)
        )));
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();

        project.accept(visitor);

        assertEquals(240, visitor.getTotalDebtMinutes());
        assertEquals(3, visitor.getFindings().size());
    }
}
