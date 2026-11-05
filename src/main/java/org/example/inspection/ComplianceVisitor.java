package org.example.inspection;

import java.util.ArrayList;
import java.util.List;

public class ComplianceVisitor implements ProjectVisitor {
    private final List<String> violations = new ArrayList<>();

    @Override
    public void visit(SourceCodeFile file) {
        if (file.getCredentials()) {
            violations.add(file.getName() + ": remove source credentials");
        }
        if (!file.getTested()) {
            violations.add(file.getName() + ": production module needs tests");
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.getCredentials()) {
            violations.add(file.getName() + ": remove exposed credentials");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.getDependencies()) {
            if (dependency.getVersion() == null || dependency.getVersion().isBlank()) {
                violations.add(dependency.getName() + ": specify a version");
            }
            if (dependency.getProhibited()) {
                violations.add(dependency.getName() + ": prohibited dependency");
            }
        }
    }

    @Override
    public void visit(Directory directory) {
    }

    public List<String> getViolations() {
        return List.copyOf(violations);
    }

    public boolean isCompliant() {
        return violations.isEmpty();
    }

    public String getReport() {
        return "Compliance Check\nViolations: " + violations.size() + "\n"
                + (isCompliant() ? "All rules passed." : String.join("\n", violations));
    }
}

