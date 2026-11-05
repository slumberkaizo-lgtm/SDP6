package org.example.inspection;

public class TechnicalDebtVisitor implements ProjectVisitor {
    private int minutes;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.getComplexity() > 10) {
            minutes += (file.getComplexity() - 10) * 30;
        }
        if (!file.getTested()) {
            minutes += 120;
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
            if (dependency.getVulnerable()) {
                minutes += 60;
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    public int getMinutes() {
        return minutes;
    }

    public String getReport() {
        return "Technical Debt\nEstimated work: " + minutes + " minutes";
    }
}
