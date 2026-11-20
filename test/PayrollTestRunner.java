package test;

import employee.Employee;
import employee.EmployeeManager;
import employee.FullTimeEmployee;
import employee.PartTimeEmployee;
import exceptions.EmployeeNotFoundException;
import payroll.PayrollHistory;
import payroll.PayrollManager;
import payroll.Payslip;
import payroll.PayslipGenerator;

import java.util.List;

/**
 * Standalone Test Suite for Payroll, Payslip, and History functionality.
 * Runs without external testing frameworks (JUnit-free Core Java test harness).
 */
public class PayrollTestRunner {
    private static int totalTests = 0;
    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("       RUNNING EMPLOYEE PAYROLL SYSTEM TEST SUITE             ");
        System.out.println("===============================================================");

        testFullTimeEmployeePayslipCalculations();
        testPartTimeEmployeePayslipCalculations();
        testHighBracketPartTimeEmployeePayslip();
        testPayslipGeneratorSequencing();
        testPayrollHistoryStorageAndRetrieval();
        testPayrollHistoryFilteringByEmployeeId();
        testPayrollManagerIntegration();
        testPayrollManagerEmployeeNotFound();

        System.out.println("\n===============================================================");
        System.out.printf("TEST SUMMARY: Total: %d | Passed: %d | Failed: %d\n",
                totalTests, passedTests, failedTests);
        System.out.println("===============================================================");

        if (failedTests > 0) {
            System.err.println("[!] Some tests failed!");
            System.exit(1);
        } else {
            System.out.println("[+] ALL TESTS PASSED SUCCESSFULLY!");
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        totalTests++;
        if (condition) {
            passedTests++;
            System.out.println(" [PASS] " + testName);
        } else {
            failedTests++;
            System.err.println(" [FAIL] " + testName);
        }
    }

    private static void assertEquals(String testName, double expected, double actual) {
        totalTests++;
        boolean match = Math.abs(expected - actual) < 0.001;
        if (match) {
            passedTests++;
            System.out.printf(" [PASS] %s (Expected: %.2f, Got: %.2f)\n", testName, expected, actual);
        } else {
            failedTests++;
            System.err.printf(" [FAIL] %s (Expected: %.2f, Got: %.2f)\n", testName, expected, actual);
        }
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        totalTests++;
        boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
        if (match) {
            passedTests++;
            System.out.printf(" [PASS] %s (Expected: %s, Got: %s)\n", testName, expected, actual);
        } else {
            failedTests++;
            System.err.printf(" [FAIL] %s (Expected: %s, Got: %s)\n", testName, expected, actual);
        }
    }

    private static void testFullTimeEmployeePayslipCalculations() {
        System.out.println("\n--- Testing Full-Time Employee Calculations ---");
        // Basic: 60,000 -> HRA (20%): 12,000, DA (10%): 6,000, Gross: 78,000
        // PF (12% of 60,000): 7,200, Tax (10% of 78,000): 7,800, Total Deductions: 15,000, Net: 63,000
        Employee ft = new FullTimeEmployee(101, "Alice Johnson", "Engineering", 60000.0);
        Payslip slip = PayslipGenerator.generate(ft);

        assertEquals("FT Employee ID", 101, slip.getEmployeeId());
        assertEquals("FT Employee Name", "Alice Johnson", slip.getEmployeeName());
        assertEquals("FT Employee Dept", "Engineering", slip.getDepartment());
        assertEquals("FT Employee Type", "Full-Time", slip.getEmployeeType());
        assertEquals("FT Basic Salary", 60000.0, slip.getBasicSalary());
        assertEquals("FT HRA (20%)", 12000.0, slip.getHra());
        assertEquals("FT DA (10%)", 6000.0, slip.getDa());
        assertEquals("FT Gross Salary", 78000.0, slip.getGrossSalary());
        assertEquals("FT PF (12%)", 7200.0, slip.getPf());
        assertEquals("FT Tax (10% on Gross 78k)", 7800.0, slip.getTax());
        assertEquals("FT Total Deductions", 15000.0, slip.getTotalDeductions());
        assertEquals("FT Net Salary", 63000.0, slip.getNetSalary());
    }

    private static void testPartTimeEmployeePayslipCalculations() {
        System.out.println("\n--- Testing Part-Time Employee Calculations (<= 50k Gross) ---");
        // Basic: 40,000 -> HRA (20%): 8,000, DA (0%): 0, Gross: 48,000
        // PF (12% of 40,000): 4,800, Tax (0% since Gross <= 50,000): 0.0, Total Deductions: 4,800, Net: 43,200
        Employee pt = new PartTimeEmployee(102, "Bob Smith", "Support", 40000.0);
        Payslip slip = PayslipGenerator.generate(pt);

        assertEquals("PT Basic Salary", 40000.0, slip.getBasicSalary());
        assertEquals("PT HRA (20%)", 8000.0, slip.getHra());
        assertEquals("PT DA (0%)", 0.0, slip.getDa());
        assertEquals("PT Gross Salary", 48000.0, slip.getGrossSalary());
        assertEquals("PT PF (12%)", 4800.0, slip.getPf());
        assertEquals("PT Tax (0%)", 0.0, slip.getTax());
        assertEquals("PT Total Deductions", 4800.0, slip.getTotalDeductions());
        assertEquals("PT Net Salary", 43200.0, slip.getNetSalary());
    }

    private static void testHighBracketPartTimeEmployeePayslip() {
        System.out.println("\n--- Testing Part-Time High Bracket (> 100k Gross) ---");
        // Basic: 90,000 -> HRA (20%): 18,000, DA: 0, Gross: 108,000
        // PF (12% of 90,000): 10,800, Tax (20% of 108,000): 21,600, Total Deductions: 32,400, Net: 75,600
        Employee ptHigh = new PartTimeEmployee(103, "Charlie Davis", "Consulting", 90000.0);
        Payslip slip = PayslipGenerator.generate(ptHigh);

        assertEquals("PT High Gross", 108000.0, slip.getGrossSalary());
        assertEquals("PT High Tax (20%)", 21600.0, slip.getTax());
        assertEquals("PT High Net Salary", 75600.0, slip.getNetSalary());
    }

    private static void testPayslipGeneratorSequencing() {
        System.out.println("\n--- Testing Payslip ID Generation ---");
        Employee e1 = new FullTimeEmployee(104, "Dan White", "Marketing", 50000.0);
        Employee e2 = new FullTimeEmployee(105, "Eva Green", "HR", 52000.0);

        Payslip s1 = PayslipGenerator.generate(e1);
        Payslip s2 = PayslipGenerator.generate(e2);

        assertTrue("Payslip 1 ID not null", s1.getPayslipId() != null);
        assertTrue("Payslip 2 ID not null", s2.getPayslipId() != null);
        assertTrue("Sequential ID generation", !s1.getPayslipId().equals(s2.getPayslipId()));
    }

    private static void testPayrollHistoryStorageAndRetrieval() {
        System.out.println("\n--- Testing Payroll History Storage and Retrieval ---");
        PayrollHistory history = new PayrollHistory();
        assertTrue("Initial history is empty", history.isEmpty());
        assertEquals("Initial record count is 0", 0, history.getRecordCount());

        Employee e = new FullTimeEmployee(106, "Frank Brown", "Finance", 55000.0);
        Payslip slip = PayslipGenerator.generate(e);
        history.addPayslip(slip);

        assertTrue("History is not empty after add", !history.isEmpty());
        assertEquals("Record count is 1", 1, history.getRecordCount());
        assertEquals("First slip matches", slip, history.getHistory().get(0));
    }

    private static void testPayrollHistoryFilteringByEmployeeId() {
        System.out.println("\n--- Testing History Filtering by Employee ID ---");
        PayrollHistory history = new PayrollHistory();
        Employee e1 = new FullTimeEmployee(201, "George Clark", "IT", 60000.0);
        Employee e2 = new PartTimeEmployee(202, "Hannah Lee", "Operations", 35000.0);

        Payslip s1 = PayslipGenerator.generate(e1);
        Payslip s2 = PayslipGenerator.generate(e2);
        Payslip s3 = PayslipGenerator.generate(e1); // Second payslip for e1

        history.addPayslip(s1);
        history.addPayslip(s2);
        history.addPayslip(s3);

        List<Payslip> e1History = history.getHistoryByEmployeeId(201);
        List<Payslip> e2History = history.getHistoryByEmployeeId(202);
        List<Payslip> e3History = history.getHistoryByEmployeeId(999);

        assertEquals("Employee 201 has 2 payslips", 2, e1History.size());
        assertEquals("Employee 202 has 1 payslip", 1, e2History.size());
        assertEquals("Employee 999 has 0 payslips", 0, e3History.size());
    }

    private static void testPayrollManagerIntegration() {
        System.out.println("\n--- Testing Payroll Manager Integration ---");
        EmployeeManager empMgr = new EmployeeManager();
        PayrollHistory history = new PayrollHistory();
        PayrollManager payrollMgr = new PayrollManager(empMgr, history);

        try {
            Employee emp = new FullTimeEmployee(301, "Ian Scott", "Security", 45000.0);
            empMgr.addEmployee(emp);

            Payslip generated = payrollMgr.generatePayslip(301);
            assertEquals("Generated slip employee ID", 301, generated.getEmployeeId());
            assertEquals("History contains recorded slip", 1, history.getRecordCount());
        } catch (Exception e) {
            assertTrue("PayrollManager threw unexpected exception: " + e.getMessage(), false);
        }
    }

    private static void testPayrollManagerEmployeeNotFound() {
        System.out.println("\n--- Testing Payroll Manager Error Handling ---");
        EmployeeManager empMgr = new EmployeeManager();
        PayrollHistory history = new PayrollHistory();
        PayrollManager payrollMgr = new PayrollManager(empMgr, history);

        boolean caught = false;
        try {
            payrollMgr.generatePayslip(9999);
        } catch (EmployeeNotFoundException e) {
            caught = true;
        } catch (Exception e) {
            caught = false;
        }
        assertTrue("EmployeeNotFoundException caught for invalid ID", caught);
    }
}
