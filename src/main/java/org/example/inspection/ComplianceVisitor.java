package org.example.inspection;

import java.util.ArrayList;
import java.util.List;

public class ComplianceVisitor implements ProjectVisitor {
    private final List<String> violations = new ArrayList<>();

    @Override
    public void visit(SourceCodeFile file) {
        if (file.hasHardcodedCredentials()) {
            violations.add(file.getName() + ": credentials must not be stored in source code");
        }
        if (!file.hasTests()) {
            violations.add(file.getName() + ": production module must have tests");
        }
    }

    @Override
    public void visit(TestFile file) {
    }

    @Override
    public void visit(ConfigurationFile file) {
        if (file.hasExposedCredentials()) {
            violations.add(file.getName() + ": credentials must not be exposed");
        }
    }

    @Override
    public void visit(DependencyFile file) {
        for (Dependency dependency : file.getDependencies()) {
            if (dependency.getVersion() == null || dependency.getVersion().isBlank()) {
                violations.add(file.getName() + ": version is missing for " + dependency.getName());
            }
            if (dependency.isProhibited()) {
                violations.add(file.getName() + ": prohibited dependency " + dependency.getName());
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
        String details = isCompliant() ? "All compliance rules passed." : String.join("\n", violations);
        return "Compliance Check\nViolations: " + violations.size() + "\n" + details;
    }
}
