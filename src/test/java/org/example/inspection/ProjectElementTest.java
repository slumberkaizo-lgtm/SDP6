package org.example.inspection;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProjectElementTest {
    @Test
    void elementsCallTheirMatchingVisitorMethods() {
        RecordingVisitor visitor = new RecordingVisitor();
        List<ProjectElement> elements = List.of(
                new SourceCodeFile("Service.java", 20, 2, false, true),
                new TestFile("ServiceTest.java", 10, 1),
                new ConfigurationFile("application.properties", false, false),
                new DependencyFile("pom.xml", List.of()),
                new Directory("project")
        );

        for (ProjectElement element : elements) {
            element.accept(visitor);
        }

        assertEquals(List.of("source", "test", "configuration", "dependency", "directory"),
                visitor.visited);
    }

    @Test
    void directoryVisitsNestedElementsInOrder() {
        Directory root = new Directory("project");
        Directory src = new Directory("src");
        Directory services = new Directory("services");
        services.add(new SourceCodeFile("Service.java", 20, 2, false, true));
        src.add(services);
        root.add(src);
        root.add(new TestFile("ServiceTest.java", 10, 1));
        RecordingVisitor visitor = new RecordingVisitor();

        root.accept(visitor);

        assertEquals(List.of("directory", "directory", "directory", "source", "test"),
                visitor.visited);
    }

    @Test
    void negativeMetricsAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new SourceCodeFile("Service.java", -1, 2, false, true));
        assertThrows(IllegalArgumentException.class,
                () -> new SourceCodeFile("Service.java", 20, -1, false, true));
        assertThrows(IllegalArgumentException.class,
                () -> new TestFile("ServiceTest.java", -1, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new TestFile("ServiceTest.java", 10, -1));
    }

    @Test
    void directoryRejectsNullChildren() {
        Directory directory = new Directory("project");

        assertThrows(NullPointerException.class, () -> directory.add(null));
        assertEquals(0, directory.getChildren().size());
    }

    private static class RecordingVisitor implements ProjectVisitor {
        private final List<String> visited = new ArrayList<>();

        @Override
        public void visit(SourceCodeFile file) {
            visited.add("source");
        }

        @Override
        public void visit(TestFile file) {
            visited.add("test");
        }

        @Override
        public void visit(ConfigurationFile file) {
            visited.add("configuration");
        }

        @Override
        public void visit(DependencyFile file) {
            visited.add("dependency");
        }

        @Override
        public void visit(Directory directory) {
            visited.add("directory");
        }
    }
}
