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
     * Generates payslips for all registered employees in the EmployeeManager using current date.
     *
     * @return List of newly generated payslips.
     */
    public java.util.List<Payslip> generatePayrollForAll() {
        return generatePayrollForAll(LocalDate.now());
    }

    /**
     * Generates payslips for all registered employees for a specific pay period date.
     *
     * @param payPeriodDate The explicit pay period date.
     * @return List of newly generated payslips.
     */
    public java.util.List<Payslip> generatePayrollForAll(LocalDate payPeriodDate) {
        java.util.List<Employee> employees = employeeManager.getAllEmployees();
        if (employees.isEmpty()) {
            System.out.println("\n[!] No employees registered in the system for bulk payroll generation.");
            return java.util.Collections.emptyList();
        }

        System.out.println("\n>>> Initiating Batch Payroll Processing for " + employees.size() + " Employees <<<");
        java.util.List<Payslip> generated = new java.util.ArrayList<>();
        double totalDisbursed = 0.0;

        for (Employee emp : employees) {
            try {
                Payslip slip = PayslipGenerator.generate(emp, payPeriodDate);
                payrollHistory.addPayslip(slip);
                generated.add(slip);
                totalDisbursed += slip.getNetSalary();
            } catch (Exception e) {
                System.err.printf("[!] Error generating payslip for Employee ID %d (%s): %s\n",
                        emp.getId(), emp.getName(), e.getMessage());
            }
        }

        System.out.printf("\n[+] Batch Payroll Completed: %d/%d payslips generated successfully.\n",
                generated.size(), employees.size());
        System.out.printf("[+] Total Batch Net Payout: Rs. %.2f\n", totalDisbursed);

        return java.util.Collections.unmodifiableList(generated);
    }

    /**
     * Generates payslips for all employees belonging to a specific department.
     */
    public java.util.List<Payslip> generatePayrollForDepartment(String department, LocalDate payPeriodDate) {
        if (department == null || department.trim().isEmpty()) {
            System.out.println("[!] Department name cannot be null or empty.");
            return java.util.Collections.emptyList();
        }

        java.util.List<Employee> allEmployees = employeeManager.getAllEmployees();
        java.util.List<Employee> deptEmployees = new java.util.ArrayList<>();
        for (Employee e : allEmployees) {
            if (e.getDepartment().equalsIgnoreCase(department.trim())) {
                deptEmployees.add(e);
            }
        }

        if (deptEmployees.isEmpty()) {
            System.out.println("\n[!] No employees found in department: " + department);
            return java.util.Collections.emptyList();
        }

        System.out.println("\n>>> Generating Department Payroll for '" + department + "' (" + deptEmployees.size() + " employees) <<<");
        java.util.List<Payslip> generated = new java.util.ArrayList<>();
        for (Employee emp : deptEmployees) {
            Payslip slip = PayslipGenerator.generate(emp, payPeriodDate);
            payrollHistory.addPayslip(slip);
            generated.add(slip);
        }
        System.out.printf("[+] Department '%s' Payroll Completed: %d payslips generated.\n", department, generated.size());
        return java.util.Collections.unmodifiableList(generated);
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
