package org.example.inspection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Directory implements ProjectElement {
    private final String name;
    private final List<ProjectElement> children = new ArrayList<>();

    public Directory(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getName() {
        return name;
    }

    public void add(ProjectElement element) {
        children.add(Objects.requireNonNull(element));
    }

    public List<ProjectElement> getChildren() {
        return List.copyOf(children);
    }

    @Override
    public void accept(ProjectVisitor visitor) {
        visitor.visit(this);
        for (ProjectElement child : children) {
            child.accept(visitor);
        }
    }
}
