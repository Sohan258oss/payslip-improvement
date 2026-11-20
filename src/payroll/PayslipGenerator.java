package payroll;

import employee.Employee;
import salary.Salary;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe Generator for creating validated Payslip instances.
 * Coordinates employee salary calculations and assigns sequential reference IDs.
 *
 * Demonstrates:
 * - Thread-Safety: Employs AtomicInteger for atomic, lock-free ID sequencing.
 * - Temporal Support: Supports generating payslips for explicit pay periods.
 * - Defensive Validation: Ensures employee and salary contracts are valid prior to creation.
 */
public class PayslipGenerator {
    public static final String DEFAULT_ID_PREFIX = "PS-";
    public static final int DEFAULT_STARTING_ID = 1001;

    private static final AtomicInteger payslipCounter = new AtomicInteger(DEFAULT_STARTING_ID);

    // Private constructor to prevent instantiation of utility class
    private PayslipGenerator() {}

    /**
     * Generates a Payslip for the specified employee using current date as pay period.
     */
    public static Payslip generate(Employee employee) {
        return generate(employee, LocalDate.now());
    }

    /**
     * Generates a Payslip for the specified employee and explicit pay period date.
     */
    public static Payslip generate(Employee employee, LocalDate payPeriodDate) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee reference cannot be null for payslip generation.");
        }
        if (employee.getId() <= 0) {
            throw new IllegalArgumentException("Invalid Employee ID: " + employee.getId() + ". ID must be > 0.");
        }
        if (employee.getBasicSalary() <= 0.0) {
            throw new IllegalArgumentException("Employee basic salary must be > 0 to generate a payslip. Given: " + employee.getBasicSalary());
        }

        Salary salary = employee.getSalaryBreakdown();
        if (salary == null) {
            throw new IllegalStateException("Failed to calculate salary breakdown for employee: " + employee.getName());
        }

        String payslipId = generateNextId();

        return new Payslip(
                payslipId,
                employee.getId(),
                employee.getName(),
                employee.getDepartment(),
                employee.getEmployeeType(),
                salary.getBasicSalary(),
                salary.getAllowance().getHra(),
                salary.getAllowance().getDa(),
                salary.getGrossSalary(),
                salary.getDeduction().getPf(),
                salary.getDeduction().getTax(),
                salary.getDeduction().getTotalDeduction(),
                salary.getNetSalary(),
                payPeriodDate
        );
    }

    /**
     * Atomically generates and increments the next payslip reference ID.
     */
    public static String generateNextId() {
        return DEFAULT_ID_PREFIX + payslipCounter.getAndIncrement();
    }

    /**
     * Peeks at the next payslip ID that will be issued without incrementing.
     */
    public static String peekNextId() {
        return DEFAULT_ID_PREFIX + payslipCounter.get();
    }

    /**
     * Returns the current counter sequence value.
     */
    public static int getCurrentCounter() {
        return payslipCounter.get();
    }

    /**
     * Resets the counter sequence to the default starting number (1001).
     */
    public static void resetCounter() {
        payslipCounter.set(DEFAULT_STARTING_ID);
    }

    /**
     * Sets the counter sequence to a specified positive value.
     */
    public static void setCounter(int startingNumber) {
        if (startingNumber <= 0) {
            throw new IllegalArgumentException("Starting ID counter must be positive (> 0). Given: " + startingNumber);
        }
        payslipCounter.set(startingNumber);
    }
}
