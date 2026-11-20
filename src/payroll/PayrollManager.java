package payroll;

import employee.Employee;
import employee.EmployeeManager;
import exceptions.EmployeeNotFoundException;
import java.time.LocalDate;

/**
 * High-level manager coordinating payroll operations between EmployeeManager,
 * PayslipGenerator, and PayrollHistory.
 *
 * Implements:
 * - Robust Dependency Injection: Guarantees non-null core dependencies.
 * - Defensive Input Validation: Checks employee identifiers and period dates.
 * - Temporal Flexibility: Supports current and retroactive pay period generation.
 * - Cohesive Orchestration: Manages calculation, logging, and reporting workflows.
 */
public class PayrollManager {
    private final EmployeeManager employeeManager;
    private final PayrollHistory payrollHistory;

    /**
     * Constructs a PayrollManager with validated dependencies.
     */
    public PayrollManager(EmployeeManager employeeManager, PayrollHistory payrollHistory) {
        if (employeeManager == null) {
            throw new IllegalArgumentException("EmployeeManager dependency cannot be null.");
        }
        if (payrollHistory == null) {
            throw new IllegalArgumentException("PayrollHistory dependency cannot be null.");
        }
        this.employeeManager = employeeManager;
        this.payrollHistory = payrollHistory;
    }

    /**
     * Generates a payslip for the specified employee ID using the current date,
     * archives it in history, and prints the formatted payslip.
     */
    public Payslip generatePayslip(int employeeId) throws EmployeeNotFoundException {
        return generatePayslip(employeeId, LocalDate.now());
    }

    /**
     * Generates a payslip for the specified employee ID and pay period date,
     * archives it in history, and prints the formatted payslip.
     */
    public Payslip generatePayslip(int employeeId, LocalDate payPeriodDate) throws EmployeeNotFoundException {
        if (employeeId <= 0) {
            throw new IllegalArgumentException("Invalid Employee ID: " + employeeId + ". ID must be strictly positive (> 0).");
        }
        Employee employee = employeeManager.getEmployeeById(employeeId);
        return generatePayslip(employee, payPeriodDate);
    }

    /**
     * Generates a payslip directly from an Employee reference, archives it in history,
     * and prints the formatted payslip.
     */
    public Payslip generatePayslip(Employee employee) {
        return generatePayslip(employee, LocalDate.now());
    }

    /**
     * Generates a payslip directly from an Employee reference for an explicit pay period date,
     * archives it in history, and prints the formatted payslip.
     */
    public Payslip generatePayslip(Employee employee, LocalDate payPeriodDate) {
        if (employee == null) {
            throw new IllegalArgumentException("Employee reference cannot be null.");
        }
        Payslip payslip = PayslipGenerator.generate(employee, payPeriodDate);
        payrollHistory.addPayslip(payslip);
        payslip.printPayslip();
        return payslip;
    }

    /**
     * Displays complete payroll history accompanied by cumulative financial metrics.
     */
    public void viewPayrollHistory() {
        payrollHistory.displayHistory();
        if (!payrollHistory.isEmpty()) {
            payrollHistory.displayFinancialSummary();
        }
    }

    /**
     * Displays payroll transaction history for a specific employee.
     */
    public void viewPayrollHistoryForEmployee(int employeeId) {
        if (employeeId <= 0) {
            System.out.println("[!] Invalid Employee ID: " + employeeId + ". ID must be > 0.");
            return;
        }
        payrollHistory.displayHistoryByEmployee(employeeId);
    }

    /**
     * Retrieves a payslip by its reference ID through the manager.
     */
    public Payslip findPayslip(String payslipId) {
        return payrollHistory.findPayslipById(payslipId);
    }

    public EmployeeManager getEmployeeManager() {
        return employeeManager;
    }

    public PayrollHistory getPayrollHistory() {
        return payrollHistory;
    }
}
