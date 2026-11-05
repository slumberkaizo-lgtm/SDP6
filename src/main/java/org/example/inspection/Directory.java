package org.example.inspection;

import java.util.ArrayList;
import java.util.List;

public class Directory implements ProjectElement {
    private final String name;
    private final List<ProjectElement> children = new ArrayList<>();

    public Directory(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void add(ProjectElement element) {
        children.add(java.util.Objects.requireNonNull(element));
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
        for (ProjectElement child : children) {
            child.accept(visitor);
        }
    }
}

