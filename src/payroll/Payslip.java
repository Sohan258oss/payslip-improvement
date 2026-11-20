package payroll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an immutable, itemized Payslip for an employee.
 * Encapsulates all earnings, allowances, deductions, and net salary for a pay period.
 *
 * Implements:
 * - Immutability: All attributes are private final to preserve audit trail integrity.
 * - Temporal Support: Captures both the operational pay period date and exact generation timestamp.
 * - Data Validation: Strictly validates input parameters and mathematical invariants.
 * - Comparable: Natural ordering based on generation timestamp and payslip ID.
 */
public class Payslip implements Comparable<Payslip> {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter PERIOD_FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");
    private static final double INVARIANT_TOLERANCE = 0.05;

    private final String payslipId;
    private final int employeeId;
    private final String employeeName;
    private final String department;
    private final String employeeType;
    private final double basicSalary;
    private final double hra;
    private final double da;
    private final double grossSalary;
    private final double pf;
    private final double tax;
    private final double totalDeductions;
    private final double netSalary;
    private final LocalDate payPeriodDate;
    private final LocalDateTime generatedAt;
    private final String generationDate;

    /**
     * Primary Constructor with validation and explicit pay period date.
     */
    public Payslip(String payslipId, int employeeId, String employeeName, String department,
                   String employeeType, double basicSalary, double hra, double da,
                   double grossSalary, double pf, double tax, double totalDeductions,
                   double netSalary, LocalDate payPeriodDate) {

        // Validate identifiers and text metadata
        validateNonEmptyString(payslipId, "Payslip ID");
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Employee ID must be strictly positive (> 0). Given: " + employeeId);
        }
        validateNonEmptyString(employeeName, "Employee name");
        validateNonEmptyString(department, "Department");
        validateNonEmptyString(employeeType, "Employee type");

        // Validate monetary values
        validateAmount(basicSalary, "Basic salary");
        validateAmount(hra, "HRA");
        validateAmount(da, "DA");
        validateAmount(grossSalary, "Gross salary");
        validateAmount(pf, "PF");
        validateAmount(tax, "Tax");
        validateAmount(totalDeductions, "Total deductions");
        validateAmount(netSalary, "Net salary");

        // Validate mathematical consistency invariants
        validateInvariants(basicSalary, hra, da, grossSalary, pf, tax, totalDeductions, netSalary);

        this.payslipId = payslipId.trim();
        this.employeeId = employeeId;
        this.employeeName = employeeName.trim();
        this.department = department.trim();
        this.employeeType = employeeType.trim();
        this.basicSalary = basicSalary;
        this.hra = hra;
        this.da = da;
        this.grossSalary = grossSalary;
        this.pf = pf;
        this.tax = tax;
        this.totalDeductions = totalDeductions;
        this.netSalary = netSalary;
        this.payPeriodDate = (payPeriodDate != null) ? payPeriodDate : LocalDate.now();
        this.generatedAt = LocalDateTime.now();
        this.generationDate = this.payPeriodDate.format(DATE_FORMATTER);
    }

    /**
     * Backward-compatible Constructor defaulting pay period date to the current date.
     */
    public Payslip(String payslipId, int employeeId, String employeeName, String department,
                   String employeeType, double basicSalary, double hra, double da,
                   double grossSalary, double pf, double tax, double totalDeductions,
                   double netSalary) {
        this(payslipId, employeeId, employeeName, department, employeeType,
             basicSalary, hra, da, grossSalary, pf, tax, totalDeductions,
             netSalary, LocalDate.now());
    }

    // Defensive validation helpers
    private static void validateNonEmptyString(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be null or empty.");
        }
    }

    private static void validateAmount(double amount, String fieldName) {
        if (Double.isNaN(amount) || Double.isInfinite(amount)) {
            throw new IllegalArgumentException(fieldName + " cannot be NaN or Infinite.");
        }
        if (amount < 0.0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative. Given: " + amount);
        }
    }

    private static void validateInvariants(double basic, double hra, double da, double gross,
                                           double pf, double tax, double deductions, double net) {
        double expectedGross = basic + hra + da;
        if (Math.abs(gross - expectedGross) > INVARIANT_TOLERANCE) {
            throw new IllegalArgumentException(String.format(
                    "Mathematical invariant error: Gross (%.2f) != Basic (%.2f) + HRA (%.2f) + DA (%.2f) = %.2f",
                    gross, basic, hra, da, expectedGross));
        }

        double expectedDeductions = pf + tax;
        if (Math.abs(deductions - expectedDeductions) > INVARIANT_TOLERANCE) {
            throw new IllegalArgumentException(String.format(
                    "Mathematical invariant error: Total Deductions (%.2f) != PF (%.2f) + Tax (%.2f) = %.2f",
                    deductions, pf, tax, expectedDeductions));
        }

        double expectedNet = gross - deductions;
        if (Math.abs(net - expectedNet) > INVARIANT_TOLERANCE) {
            throw new IllegalArgumentException(String.format(
                    "Mathematical invariant error: Net Salary (%.2f) != Gross (%.2f) - Deductions (%.2f) = %.2f",
                    net, gross, deductions, expectedNet));
        }
    }

    // Getters
    public String getPayslipId() { return payslipId; }
    public int getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getDepartment() { return department; }
    public String getEmployeeType() { return employeeType; }
    public double getBasicSalary() { return basicSalary; }
    public double getHra() { return hra; }
    public double getDa() { return da; }
    public double getGrossSalary() { return grossSalary; }
    public double getPf() { return pf; }
    public double getTax() { return tax; }
    public double getTotalDeductions() { return totalDeductions; }
    public double getNetSalary() { return netSalary; }
    public LocalDate getPayPeriodDate() { return payPeriodDate; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public String getGenerationDate() { return generationDate; }

    public String getFormattedPayPeriod() {
        return payPeriodDate.format(PERIOD_FORMATTER);
    }

    public void printPayslip() {
        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|                    EMPLOYEE PAYSLIP                         |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.printf("| Payslip ID: %-15s Date: %-22s |\n", payslipId, generationDate);
        System.out.printf("| Employee ID: %-14d Name: %-22s |\n", employeeId, employeeName);
        System.out.printf("| Department: %-15s Type: %-22s |\n", department, employeeType);
        System.out.println("+------------------------------+------------------------------+");
        System.out.println("| EARNINGS                     | DEDUCTIONS                   |");
        System.out.println("+------------------------------+------------------------------+");
        System.out.printf("| Basic Salary:  %12.2f  | Provident Fund (PF):%8.2f |\n", basicSalary, pf);
        System.out.printf("| HRA (20%%):    %12.2f  | Income Tax:         %8.2f |\n", hra, tax);
        System.out.printf("| DA (%s):      %12.2f  |                              |\n",
                employeeType.equalsIgnoreCase("Full-Time") ? "10%" : " 0%", da);
        System.out.println("+------------------------------+------------------------------+");
        System.out.printf("| Gross Salary:  %12.2f  | Total Deductions:   %8.2f |\n", grossSalary, totalDeductions);
        System.out.println("+------------------------------+------------------------------+");
        System.out.printf("| NET PAYABLE SALARY:                   Rs. %17.2f |\n", netSalary);
        System.out.println("+-------------------------------------------------------------+");
    }

    @Override
    public int compareTo(Payslip other) {
        if (other == null) return 1;
        int cmp = this.payPeriodDate.compareTo(other.payPeriodDate);
        if (cmp != 0) return cmp;
        return this.payslipId.compareTo(other.payslipId);
    }

    @Override
    public String toString() {
        return String.format("[%s] ID: %d | Name: %s | Gross: Rs. %.2f | Deductions: Rs. %.2f | Net: Rs. %.2f | Date: %s",
                payslipId, employeeId, employeeName, grossSalary, totalDeductions, netSalary, generationDate);
    }
}
