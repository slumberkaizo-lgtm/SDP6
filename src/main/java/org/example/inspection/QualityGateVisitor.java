package org.example.inspection;

public class QualityGateVisitor implements ProjectVisitor {
    private final SecurityAuditVisitor security = new SecurityAuditVisitor();
    private final ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();

    @Override
    public void visit(SourceCodeFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(TestFile file) {
        statistics.visit(file);
    }

    @Override
    public void visit(ConfigurationFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(DependencyFile file) {
        security.visit(file);
        statistics.visit(file);
    }

    @Override
    public void visit(Directory directory) {
    }

    public boolean isSecurityPassed() {
        return security.getCriticalIssues() == 0;
    }

    public boolean isComplexityPassed() {
        return statistics.getAverageComplexity() <= 10;
    }

    public boolean isTestsPassed() {
        return statistics.getTestSourceRatio() >= 0.40;
    }

    public boolean isPassed() {
        return isSecurityPassed() && isComplexityPassed() && isTestsPassed();
    }

    private String status(boolean passed) {
        return passed ? "PASS" : "FAIL";
    }

    public String getReport() {
        return "Quality Gate\nSecurity: " + status(isSecurityPassed())
                + "\nComplexity: " + status(isComplexityPassed())
                + "\nTests: " + status(isTestsPassed())
                + "\nFINAL RESULT: " + (isPassed() ? "PASSED" : "FAILED");
    }
}

