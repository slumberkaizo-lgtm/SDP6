package org.example.inspection;

public class Main {
    public static void main(String[] args) {
        Directory project = DemoProject.create();
        System.out.println("Project: " + project.getName());

        SecurityAuditVisitor security = new SecurityAuditVisitor();
        project.accept(security);
        System.out.println("\n" + security.getReport());

        ProjectStatisticsVisitor statistics = new ProjectStatisticsVisitor();
        project.accept(statistics);
        System.out.println("\n" + statistics.getReport());

        QualityGateVisitor quality = new QualityGateVisitor();
        project.accept(quality);
        System.out.println("\n" + quality.getReport());

        ComplianceVisitor compliance = new ComplianceVisitor();
        project.accept(compliance);
        System.out.println("\n" + compliance.getReport());

        TechnicalDebtVisitor debt = new TechnicalDebtVisitor();
        project.accept(debt);
        System.out.println("\n" + debt.getReport());
    }
}
