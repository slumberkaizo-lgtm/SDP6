package org.example.inspection;

import java.util.ArrayList;
import java.util.List;

public class SecurityAuditVisitor implements ProjectVisitor {
    private final List<String> issues = new ArrayList<>();
    private int criticalIssues;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.getCredentials()) {
            critical(file.getName() + ": hardcoded credentials");
        }
        if (file.getComplexity() > 15) {
            issues.add("WARNING: " + file.getName() + ": high complexity");
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.getCredentials()) {
            critical(file.getName() + ": exposed credentials");
        }
        if (file.getInsecure()) {
            critical(file.getName() + ": insecure configuration");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.getDependencies()) {
            if (dependency.getVulnerable()) {
                critical(file.getName() + ": vulnerable dependency " + dependency.getName());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    private void critical(String message) {
        criticalIssues++;
        issues.add("CRITICAL: " + message);
    }

    public int getCriticalIssues() {
        return criticalIssues;
    }

    public List<String> getIssues() {
        return List.copyOf(issues);
    }

    public String getReport() {
        return "Security Audit\nCritical issues: " + criticalIssues + "\n"
                + (issues.isEmpty() ? "No issues." : String.join("\n", issues));
    }
}

