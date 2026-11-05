package org.example.inspection;

import java.util.ArrayList;
import java.util.List;

public class TechnicalDebtVisitor implements ProjectVisitor {
    private final List<String> findings = new ArrayList<>();
    private int totalDebtMinutes;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.getComplexity() > 10) {
            int minutes = (file.getComplexity() - 10) * 30;
            findings.add(file.getName() + ": reduce complexity - " + minutes + " minutes");
            totalDebtMinutes += minutes;
        }
        if (!file.hasTests()) {
            findings.add(file.getName() + ": add missing tests - 120 minutes");
            totalDebtMinutes += 120;
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.getDependencies()) {
            if (dependency.isVulnerable()) {
                findings.add(file.getName() + ": update vulnerable dependency "
                        + dependency.getName() + " - 60 minutes");
                totalDebtMinutes += 60;
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    public int getTotalDebtMinutes() {
        return totalDebtMinutes;
    }

    public List<String> getFindings() {
        return List.copyOf(findings);
    }

    public String getReport() {
        String details = findings.isEmpty() ? "No technical debt found." : String.join("\n", findings);
        return "Technical Debt\nTotal debt: " + totalDebtMinutes + " minutes\n" + details;
    }
}
