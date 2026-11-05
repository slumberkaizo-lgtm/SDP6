package org.example.inspection;

import java.util.List;

public class DemoProject {
    public static Directory create() {
        Directory root = new Directory("fintech-project");
        Directory src = new Directory("src");
        src.add(new SourceCodeFile("PaymentService.java", 150, 14, true, true));
        src.add(new SourceCodeFile("UserService.java", 100, 8, false, false));
        Directory tests = new Directory("tests");
        tests.add(new TestFile("PaymentServiceTest.java", 70, 5));
        Directory config = new Directory("config");
        config.add(new ConfigurationFile("application.properties", true, true));
        root.add(src);
        root.add(tests);
        root.add(config);
        root.add(new DependencyFile("pom.xml", List.of(
                new Dependency("spring-core", "6.2.0", false, false),
                new Dependency("legacy-json", "1.0", true, false),
                new Dependency("debug-tool", "", false, true)
        )));
        return root;
    }
}
