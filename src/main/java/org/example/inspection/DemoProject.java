package org.example.inspection;

import java.util.List;

public class DemoProject {
    public static Directory create() {
        Directory project = new Directory("fintech-project");
        Directory src = new Directory("src");
        Directory tests = new Directory("tests");
        Directory config = new Directory("config");

        src.add(new SourceCodeFile("PaymentService.java", 180, 18, true, true));
        src.add(new SourceCodeFile("UserService.java", 120, 8, false, false));
        tests.add(new TestFile("PaymentServiceTest.java", 90, 6));
        config.add(new ConfigurationFile("application.properties", true, true));

        DependencyFile dependencies = new DependencyFile("pom.xml", List.of(
                new Dependency("payment-core", "2.0", false, false),
                new Dependency("legacy-json", "1.0", true, false),
                new Dependency("banned-debug-tool", "", false, true)
        ));

        project.add(src);
        project.add(tests);
        project.add(config);
        project.add(dependencies);
        return project;
    }
}
