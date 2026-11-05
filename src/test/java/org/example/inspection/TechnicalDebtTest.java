package org.example.inspection;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TechnicalDebtTest {
    @Test
    void complexityAboveTenAddsThirtyMinutesPerPoint() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        new SourceCodeFile("Simple.java", 10, 10, false, true).accept(visitor);
        assertEquals(0, visitor.getMinutes());
        new SourceCodeFile("Complex.java", 10, 12, false, true).accept(visitor);
        assertEquals(60, visitor.getMinutes());
    }

    @Test
    void missingTestsAddTwoHours() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        new SourceCodeFile("Service.java", 10, 2, false, false).accept(visitor);
        assertEquals(120, visitor.getMinutes());
    }

    @Test
    void vulnerableDependencyAddsOneHour() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        new DependencyFile("pom.xml", List.of(
                new Dependency("safe", "1", false, false),
                new Dependency("old", "1", true, false))).accept(visitor);
        assertEquals(60, visitor.getMinutes());
    }

    @Test
    void nestedDemoCombinesAllRules() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        DemoProject.create().accept(visitor);
        assertEquals(300, visitor.getMinutes());
        assertTrue(visitor.getReport().contains("300 minutes"));
    }

    @Test
    void emptyDirectoryAddsNoDebt() {
        TechnicalDebtVisitor visitor = new TechnicalDebtVisitor();
        new Directory("empty").accept(visitor);
        assertEquals(0, visitor.getMinutes());
    }
}
