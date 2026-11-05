package org.example.inspection;

import java.util.ArrayList;
import java.util.List;

public class SecurityAuditVisitor implements ProjectVisitor {
    private final List<String> issues = new ArrayList<>();
    private int criticalIssueCount;

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hasHardcodedCredentials()) {
            addCritical(file.getName() + ": hardcoded credentials");
        }
        if (file.getComplexity() > 15) {
            issues.add("WARNING: " + file.getName() + ": high-risk source file (complexity > 15)");
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.hasExposedCredentials()) {
            addCritical(file.getName() + ": exposed credentials");
        }
        if (file.isInsecureConfiguration()) {
            addCritical(file.getName() + ": insecure configuration");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.getDependencies()) {
            if (dependency.isVulnerable()) {
                addCritical(file.getName() + ": vulnerable dependency " + dependency.getName());
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    private void addCritical(String issue) {
        issues.add("CRITICAL: " + issue);
        criticalIssueCount++;
    }

    public List<String> getIssues() {
        return List.copyOf(issues);
    }

    public int getCriticalIssueCount() {
        return criticalIssueCount;
    }

    public String getReport() {
        String details = issues.isEmpty() ? "No security issues found." : String.join("\n", issues);
        return "Security Audit\nCritical issues: " + criticalIssueCount + "\n" + details;
    }
}
