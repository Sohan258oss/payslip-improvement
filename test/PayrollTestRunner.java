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
        testPayslipTemporalAndOrdering();
        testPayslipDataValidationAndInvariants();
        testPayslipEqualsAndHashCode();
        testPayslipFormattingAndAlignment();
        testPayslipGeneratorSequencing();
        testPayslipGeneratorConcurrencyAndIdManagement();
        testPayrollHistoryStorageAndRetrieval();
        testPayrollHistoryDefensiveGuardsAndRemoval();
        testPayrollHistoryFilteringByEmployeeId();
        testPayrollHistoryMultiCriteriaSearch();
        testPayrollHistoryAnalyticsAndFinancialSummary();
        testPayrollManagerIntegration();
        testPayrollManagerRobustValidationAndPeriodSupport();
        testPayrollManagerBatchPayrollProcessing();
        testTaxBoundarySlabsAndDecimalPrecision();
        testHighVolumeStressAndHistoryIntegrity();
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

    private static void testPayslipTemporalAndOrdering() {
        System.out.println("\n--- Testing Payslip Temporal & Ordering Features ---");
        java.time.LocalDate d1 = java.time.LocalDate.of(2026, 1, 15);
        java.time.LocalDate d2 = java.time.LocalDate.of(2026, 2, 15);

        Payslip s1 = new Payslip("PS-100", 1, "John", "IT", "Full-Time", 50000, 10000, 5000, 65000, 6000, 6500, 12500, 52500, d1);
        Payslip s2 = new Payslip("PS-101", 1, "John", "IT", "Full-Time", 50000, 10000, 5000, 65000, 6000, 6500, 12500, 52500, d2);

        assertEquals("Pay period date preserved", d1, s1.getPayPeriodDate());
        assertTrue("Generated timestamp present", s1.getGeneratedAt() != null);
        assertEquals("Formatted pay period", "January 2026", s1.getFormattedPayPeriod());
        assertTrue("Natural ordering s1 < s2", s1.compareTo(s2) < 0);
        assertTrue("Natural ordering s2 > s1", s2.compareTo(s1) > 0);
        assertTrue("Self comparison s1 == s1", s1.compareTo(s1) == 0);
    }

    private static void testPayslipDataValidationAndInvariants() {
        System.out.println("\n--- Testing Payslip Validation & Invariant Enforcement ---");

        // Test 1: Zero / Negative Employee ID
        boolean caughtInvalidId = false;
        try {
            new Payslip("PS-1", 0, "Test", "IT", "Full-Time", 50000, 10000, 5000, 65000, 6000, 6500, 12500, 52500);
        } catch (IllegalArgumentException e) {
            caughtInvalidId = true;
        }
        assertTrue("Reject zero employee ID", caughtInvalidId);

        // Test 2: Null / Empty Employee Name
        boolean caughtEmptyName = false;
        try {
            new Payslip("PS-1", 10, "   ", "IT", "Full-Time", 50000, 10000, 5000, 65000, 6000, 6500, 12500, 52500);
        } catch (IllegalArgumentException e) {
            caughtEmptyName = true;
        }
        assertTrue("Reject empty employee name", caughtEmptyName);

        // Test 3: Negative Salary
        boolean caughtNegativeSalary = false;
        try {
            new Payslip("PS-1", 10, "Test", "IT", "Full-Time", -50000, 10000, 5000, 65000, 6000, 6500, 12500, 52500);
        } catch (IllegalArgumentException e) {
            caughtNegativeSalary = true;
        }
        assertTrue("Reject negative salary", caughtNegativeSalary);

        // Test 4: Broken Gross Invariant (Basic + HRA + DA != Gross)
        boolean caughtGrossMismatch = false;
        try {
            // Gross specified as 99999 instead of 65000
            new Payslip("PS-1", 10, "Test", "IT", "Full-Time", 50000, 10000, 5000, 99999, 6000, 6500, 12500, 52500);
        } catch (IllegalArgumentException e) {
            caughtGrossMismatch = true;
        }
        assertTrue("Reject gross invariant mismatch", caughtGrossMismatch);

        // Test 5: Broken Deductions Invariant (PF + Tax != Deductions)
        boolean caughtDeductionsMismatch = false;
        try {
            // Deductions specified as 5000 instead of 12500
            new Payslip("PS-1", 10, "Test", "IT", "Full-Time", 50000, 10000, 5000, 65000, 6000, 6500, 5000, 52500);
        } catch (IllegalArgumentException e) {
            caughtDeductionsMismatch = true;
        }
        assertTrue("Reject deductions invariant mismatch", caughtDeductionsMismatch);

        // Test 6: Broken Net Invariant (Gross - Deductions != Net)
        boolean caughtNetMismatch = false;
        try {
            // Net specified as 40000 instead of 52500
            new Payslip("PS-1", 10, "Test", "IT", "Full-Time", 50000, 10000, 5000, 65000, 6000, 6500, 12500, 40000);
        } catch (IllegalArgumentException e) {
            caughtNetMismatch = true;
        }
        assertTrue("Reject net salary invariant mismatch", caughtNetMismatch);
    }

    private static void testPayslipEqualsAndHashCode() {
        System.out.println("\n--- Testing Payslip Equals and HashCode Contracts ---");
        Payslip p1 = new Payslip("PS-500", 10, "Sarah Connor", "Defense", "Full-Time",
                50000, 10000, 5000, 65000, 6000, 6500, 12500, 52500);
        Payslip p1Same = new Payslip("PS-500", 10, "Sarah Connor", "Defense", "Full-Time",
                50000, 10000, 5000, 65000, 6000, 6500, 12500, 52500);
        Payslip p2DifferentId = new Payslip("PS-501", 10, "Sarah Connor", "Defense", "Full-Time",
                50000, 10000, 5000, 65000, 6000, 6500, 12500, 52500);

        // Reflexive
        assertTrue("Reflexive equals", p1.equals(p1));
        // Symmetric
        assertTrue("Symmetric equals", p1.equals(p1Same) && p1Same.equals(p1));
        // Hashcode consistency
        assertEquals("Consistent hashCode for equal objects", p1.hashCode(), p1Same.hashCode());
        // Inequality
        assertTrue("Different ID not equal", !p1.equals(p2DifferentId));
        assertTrue("Not equal to null", !p1.equals(null));
        assertTrue("Not equal to other type", !p1.equals("Some String"));

        // Set membership
        java.util.Set<Payslip> set = new java.util.HashSet<>();
        set.add(p1);
        set.add(p1Same); // should be deduplicated
        set.add(p2DifferentId);
        assertEquals("HashSet size is 2 after adding duplicate", 2, set.size());
    }

    private static void testPayslipFormattingAndAlignment() {
        System.out.println("\n--- Testing Payslip Box-Drawing Alignment & Formatting ---");
        Payslip normal = new Payslip("PS-1001", 101, "Alice Johnson", "Engineering", "Full-Time",
                60000.0, 12000.0, 6000.0, 78000.0, 7200.0, 7800.0, 15000.0, 63000.0);
        String formatted = normal.getFormattedPayslip();

        assertTrue("Formatted slip contains header", formatted.contains("EMPLOYEE PAYSLIP"));
        assertTrue("Formatted slip contains basic salary", formatted.contains("Basic Salary:"));
        assertTrue("Formatted slip contains net payable", formatted.contains("NET PAYABLE SALARY:"));

        // Verify every line in the formatted slip is exactly 63 characters wide
        String[] lines = formatted.trim().split("\n");
        boolean all63 = true;
        for (String line : lines) {
            if (line.length() != 63) {
                all63 = false;
                System.err.printf("Misaligned line (len %d): '%s'\n", line.length(), line);
            }
        }
        assertTrue("All formatted payslip lines are exactly 63 chars wide", all63);

        // Test with extraordinarily long employee name and department
        Payslip longNameSlip = new Payslip("PS-9999", 999,
                "Alexander Maximilian Bartholomew", "Department of Advanced Research and Robotics",
                "Full-Time", 80000.0, 16000.0, 8000.0, 104000.0, 9600.0, 20800.0, 30400.0, 73600.0);
        String longFormatted = longNameSlip.getFormattedPayslip();
        String[] longLines = longFormatted.trim().split("\n");
        boolean longAll63 = true;
        for (String line : longLines) {
            if (line.length() != 63) {
                longAll63 = false;
            }
        }
        assertTrue("Long metadata safely truncated without breaking 63 char border", longAll63);
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

    private static void testPayslipGeneratorConcurrencyAndIdManagement() {
        System.out.println("\n--- Testing PayslipGenerator ID Management and Concurrency ---");
        PayslipGenerator.resetCounter();
        assertEquals("Initial counter reset to 1001", 1001, PayslipGenerator.getCurrentCounter());
        assertEquals("Peek next ID is PS-1001", "PS-1001", PayslipGenerator.peekNextId());

        // Test explicit pay period generation
        Employee emp = new FullTimeEmployee(109, "Pay Period Test", "Finance", 50000.0);
        java.time.LocalDate targetPeriod = java.time.LocalDate.of(2025, 12, 1);
        Payslip periodSlip = PayslipGenerator.generate(emp, targetPeriod);
        assertEquals("Explicit pay period preserved", targetPeriod, periodSlip.getPayPeriodDate());
        assertEquals("Explicit pay period format", "December 2025", periodSlip.getFormattedPayPeriod());

        // Concurrency test: 10 threads generating 20 IDs each = 200 IDs
        final int threadCount = 10;
        final int iterationsPerThread = 20;
        final java.util.Set<String> generatedIds = java.util.Collections.synchronizedSet(new java.util.HashSet<String>());
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        generatedIds.add(PayslipGenerator.generateNextId());
                    }
                }
            });
            threads[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        assertEquals("No duplicate IDs generated concurrently (total unique: 200)",
                threadCount * iterationsPerThread, generatedIds.size());
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

    private static void testPayrollHistoryDefensiveGuardsAndRemoval() {
        System.out.println("\n--- Testing Payroll History Defensive Guards & Removal ---");
        PayrollHistory history = new PayrollHistory();
        Employee emp = new FullTimeEmployee(110, "Defensive Test", "Legal", 70000.0);
        Payslip slip = PayslipGenerator.generate(emp);

        // Test 1: Null check
        boolean caughtNull = false;
        try {
            history.addPayslip(null);
        } catch (IllegalArgumentException e) {
            caughtNull = true;
        }
        assertTrue("Reject null payslip in history", caughtNull);

        // Test 2: Add valid slip
        history.addPayslip(slip);
        assertEquals("Latest payslip matches added slip", slip, history.getLatestPayslip());
        assertTrue("hasPayslip returns true for existing ID", history.hasPayslip(slip.getPayslipId()));

        // Test 3: Duplicate ID check
        boolean caughtDuplicate = false;
        try {
            history.addPayslip(slip);
        } catch (IllegalArgumentException e) {
            caughtDuplicate = true;
        }
        assertTrue("Reject duplicate payslip ID in history", caughtDuplicate);

        // Test 4: Unmodifiable list check
        boolean caughtMutation = false;
        try {
            history.getHistory().clear();
        } catch (UnsupportedOperationException e) {
            caughtMutation = true;
        }
        assertTrue("External mutation blocked via unmodifiable list", caughtMutation);

        // Test 5: Removal
        boolean removed = history.removePayslip(slip.getPayslipId());
        assertTrue("Payslip removed successfully", removed);
        assertEquals("History size 0 after removal", 0, history.getRecordCount());
        assertTrue("History is empty after removal", history.isEmpty());

        // Test 6: Clear history
        history.addPayslip(slip);
        assertEquals("History count 1", 1, history.getRecordCount());
        history.clearHistory();
        assertEquals("History count 0 after clear", 0, history.getRecordCount());
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

    private static void testPayrollHistoryMultiCriteriaSearch() {
        System.out.println("\n--- Testing Payroll History Multi-Criteria Search Engine ---");
        PayrollHistory history = new PayrollHistory();

        Employee e1 = new FullTimeEmployee(401, "Diana Prince", "Security", 60000.0);
        Employee e2 = new PartTimeEmployee(402, "Bruce Wayne", "Management", 80000.0);
        Employee e3 = new FullTimeEmployee(403, "Barry Allen", "Security", 45000.0);

        java.time.LocalDate nov2026 = java.time.LocalDate.of(2026, 11, 1);
        java.time.LocalDate oct2026 = java.time.LocalDate.of(2026, 10, 1);

        Payslip s1 = PayslipGenerator.generate(e1, nov2026);
        Payslip s2 = PayslipGenerator.generate(e2, nov2026);
        Payslip s3 = PayslipGenerator.generate(e3, oct2026);

        history.addPayslip(s1);
        history.addPayslip(s2);
        history.addPayslip(s3);

        // 1. Search by exact ID
        assertEquals("findPayslipById found", s1, history.findPayslipById(s1.getPayslipId()));
        assertEquals("findPayslipById case-insensitive", s1, history.findPayslipById(s1.getPayslipId().toLowerCase()));
        assertTrue("findPayslipById missing returns null", history.findPayslipById("PS-NONEXISTENT") == null);

        // 2. Search by employee name substring
        assertEquals("Search by name 'Wayne'", 1, history.searchByEmployeeName("Wayne").size());
        assertEquals("Search by name case-insensitive 'prince'", 1, history.searchByEmployeeName("prince").size());
        assertEquals("Search by name partial 'a' matches all 3", 3, history.searchByEmployeeName("a").size());
        assertEquals("Search by non-matching name returns 0", 0, history.searchByEmployeeName("NotFound").size());

        // 3. Search by department
        assertEquals("Security department has 2 slips", 2, history.getHistoryByDepartment("Security").size());
        assertEquals("Management department has 1 slip", 1, history.getHistoryByDepartment("management").size());
        assertEquals("Finance department has 0 slips", 0, history.getHistoryByDepartment("Finance").size());

        // 4. Search by employee type
        assertEquals("Full-Time has 2 slips", 2, history.getHistoryByEmployeeType("Full-Time").size());
        assertEquals("Part-Time has 1 slip", 1, history.getHistoryByEmployeeType("Part-Time").size());

        // 5. Search by Year and Month
        assertEquals("Nov 2026 has 2 slips", 2, history.getHistoryByYearAndMonth(2026, 11).size());
        assertEquals("Oct 2026 has 1 slip", 1, history.getHistoryByYearAndMonth(2026, 10).size());
        assertEquals("Dec 2026 has 0 slips", 0, history.getHistoryByYearAndMonth(2026, 12).size());

        // 6. Recent payslips
        List<Payslip> recent2 = history.getRecentPayslips(2);
        assertEquals("Recent 2 returned", 2, recent2.size());
        assertEquals("Most recent is s3", s3, recent2.get(0));
        assertEquals("Second most recent is s2", s2, recent2.get(1));
    }

    private static void testPayrollHistoryAnalyticsAndFinancialSummary() {
        System.out.println("\n--- Testing Payroll History Analytics & Financial Summaries ---");
        PayrollHistory history = new PayrollHistory();

        // Check empty state averages
        assertEquals("Empty history average net is 0", 0.0, history.getAverageNetSalary());
        assertEquals("Empty history total gross is 0", 0.0, history.getTotalGrossDisbursed());

        // Slip 1: FT Basic 60,000 -> Gross: 78,000, PF: 7,200, Tax: 7,800, Total Ded: 15,000, Net: 63,000
        Employee e1 = new FullTimeEmployee(501, "Edward Nygma", "Analytics", 60000.0);
        Payslip s1 = PayslipGenerator.generate(e1);
        history.addPayslip(s1);

        // Slip 2: PT Basic 40,000 -> Gross: 48,000, PF: 4,800, Tax: 0, Total Ded: 4,800, Net: 43,200
        Employee e2 = new PartTimeEmployee(502, "Oswald Cobblepot", "Logistics", 40000.0);
        Payslip s2 = PayslipGenerator.generate(e2);
        history.addPayslip(s2);

        // Expected Aggregates:
        // Total Gross: 78,000 + 48,000 = 126,000.0
        // Total PF: 7,200 + 4,800 = 12,000.0
        // Total Tax: 7,800 + 0 = 7,800.0
        // Total Deductions: 15,000 + 4,800 = 19,800.0
        // Total Net: 63,000 + 43,200 = 106,200.0
        // Average Net: 106,200 / 2 = 53,100.0

        assertEquals("Total Gross Disbursed", 126000.0, history.getTotalGrossDisbursed());
        assertEquals("Total PF Collected", 12000.0, history.getTotalPfCollected());
        assertEquals("Total Tax Collected", 7800.0, history.getTotalTaxCollected());
        assertEquals("Total Deductions Collected", 19800.0, history.getTotalDeductionsCollected());
        assertEquals("Total Net Paid", 106200.0, history.getTotalNetPaid());
        assertEquals("Average Net Salary", 53100.0, history.getAverageNetSalary());

        // Display summary should run cleanly without exception
        history.displayFinancialSummary();
        history.displayHistoryByEmployee(501);
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

    private static void testPayrollManagerRobustValidationAndPeriodSupport() {
        System.out.println("\n--- Testing Payroll Manager Robust Validation & Period Support ---");
        EmployeeManager empMgr = new EmployeeManager();
        PayrollHistory history = new PayrollHistory();

        // 1. Dependency checks
        boolean caughtNullEmpMgr = false;
        try {
            new PayrollManager(null, history);
        } catch (IllegalArgumentException e) {
            caughtNullEmpMgr = true;
        }
        assertTrue("Reject null EmployeeManager in PayrollManager", caughtNullEmpMgr);

        boolean caughtNullHistory = false;
        try {
            new PayrollManager(empMgr, null);
        } catch (IllegalArgumentException e) {
            caughtNullHistory = true;
        }
        assertTrue("Reject null PayrollHistory in PayrollManager", caughtNullHistory);

        PayrollManager manager = new PayrollManager(empMgr, history);

        // 2. Invalid employee ID check (<= 0)
        boolean caughtInvalidId = false;
        try {
            manager.generatePayslip(0);
        } catch (IllegalArgumentException e) {
            caughtInvalidId = true;
        } catch (Exception e) {
            caughtInvalidId = false;
        }
        assertTrue("Reject zero ID in generatePayslip", caughtInvalidId);

        // 3. Generate with explicit period date
        try {
            Employee emp = new FullTimeEmployee(601, "Selina Kyle", "Operations", 65000.0);
            empMgr.addEmployee(emp);

            java.time.LocalDate pastPeriod = java.time.LocalDate.of(2026, 5, 1);
            Payslip slip = manager.generatePayslip(601, pastPeriod);

            assertEquals("Explicit period preserved via manager", pastPeriod, slip.getPayPeriodDate());
            assertEquals("Slip findable via manager", slip, manager.findPayslip(slip.getPayslipId()));
        } catch (Exception e) {
            assertTrue("Failed to generate with explicit period: " + e.getMessage(), false);
        }

        // 4. Generate directly from Employee object
        try {
            Employee empDirect = new PartTimeEmployee(602, "Harvey Dent", "Legal", 42000.0);
            Payslip directSlip = manager.generatePayslip(empDirect);
            assertEquals("Direct generation preserves ID", 602, directSlip.getEmployeeId());
        } catch (Exception e) {
            assertTrue("Failed direct employee generation: " + e.getMessage(), false);
        }
    }

    private static void testPayrollManagerBatchPayrollProcessing() {
        System.out.println("\n--- Testing Payroll Manager Batch Payroll Processing ---");
        EmployeeManager empMgr = new EmployeeManager();
        PayrollHistory history = new PayrollHistory();
        PayrollManager manager = new PayrollManager(empMgr, history);

        // Test bulk on empty employee registry
        List<Payslip> emptyBatch = manager.generatePayrollForAll();
        assertEquals("Empty registry yields empty batch", 0, emptyBatch.size());

        try {
            // Register 3 employees across 2 departments
            empMgr.addEmployee(new FullTimeEmployee(701, "Clark Kent", "Journalism", 70000.0));
            empMgr.addEmployee(new PartTimeEmployee(702, "Lois Lane", "Journalism", 45000.0));
            empMgr.addEmployee(new FullTimeEmployee(703, "Jimmy Olsen", "Photography", 35000.0));

            // Run batch for all
            java.time.LocalDate novPeriod = java.time.LocalDate.of(2026, 11, 30);
            List<Payslip> batch = manager.generatePayrollForAll(novPeriod);

            assertEquals("Batch generated 3 payslips", 3, batch.size());
            assertEquals("History holds all 3 batch slips", 3, history.getRecordCount());

            // Run department specific batch for Journalism with different month
            java.time.LocalDate decPeriod = java.time.LocalDate.of(2026, 12, 31);
            List<Payslip> deptBatch = manager.generatePayrollForDepartment("Journalism", decPeriod);
            assertEquals("Department batch generated 2 slips", 2, deptBatch.size());
            assertEquals("History now holds 5 slips", 5, history.getRecordCount());

            // Non-existent department
            List<Payslip> noDept = manager.generatePayrollForDepartment("Aviation", decPeriod);
            assertEquals("Non-existent department returns 0 slips", 0, noDept.size());
        } catch (Exception e) {
            assertTrue("Batch processing failed unexpectedly: " + e.getMessage(), false);
        }
    }

    private static void testTaxBoundarySlabsAndDecimalPrecision() {
        System.out.println("\n--- Testing Tax Boundary Slabs & Decimal Precision ---");

        // Boundary Case 1: Gross exactly <= 50,000 -> 0% Tax
        // Part-time with basic 35,000 -> HRA (20%): 7,000 -> Gross: 42,000 <= 50,000 -> Tax = 0
        Employee ptLow = new PartTimeEmployee(801, "Low Earner", "Support", 35000.0);
        Payslip sLow = PayslipGenerator.generate(ptLow);
        assertEquals("Gross <= 50k has 0 tax", 0.0, sLow.getTax());

        // Boundary Case 2: Gross in 50,001 to 100,000 -> 10% Tax
        // Full-time with basic 50,000 -> HRA 10k, DA 5k -> Gross: 65,000 -> Tax 10% = 6,500
        Employee ftMid = new FullTimeEmployee(802, "Mid Earner", "Sales", 50000.0);
        Payslip sMid = PayslipGenerator.generate(ftMid);
        assertEquals("Gross 65k has 10% tax", 6500.0, sMid.getTax());

        // Boundary Case 3: Gross > 100,000 -> 20% Tax
        // Full-time with basic 80,000 -> HRA 16k, DA 8k -> Gross: 104,000 -> Tax 20% = 20,800
        Employee ftHigh = new FullTimeEmployee(803, "High Earner", "Executive", 80000.0);
        Payslip sHigh = PayslipGenerator.generate(ftHigh);
        assertEquals("Gross 104k has 20% tax", 20800.0, sHigh.getTax());

        // Decimal Precision Case: cents and uneven numbers
        // Basic = 54321.50 -> FT HRA: 10864.30, DA: 5432.15, Gross: 70617.95
        // PF (12%): 6518.58, Tax (10% of 70617.95): 7061.795 -> Total Ded: 13580.375, Net: 57037.575
        Employee ftCents = new FullTimeEmployee(804, "Cents Earner", "Finance", 54321.50);
        Payslip sCents = PayslipGenerator.generate(ftCents);
        assertEquals("Basic salary with cents", 54321.50, sCents.getBasicSalary());
        assertEquals("Gross with cents", 70617.95, sCents.getGrossSalary());
        assertEquals("PF with cents", 6518.58, sCents.getPf());
        assertTrue("Net salary calculation valid within invariant tolerance",
                Math.abs(sCents.getNetSalary() - (sCents.getGrossSalary() - sCents.getTotalDeductions())) < 0.05);
    }

    private static void testHighVolumeStressAndHistoryIntegrity() {
        System.out.println("\n--- Testing High-Volume Stress & History Integrity ---");
        PayrollHistory stressHistory = new PayrollHistory();
        final int volume = 500;

        for (int i = 1; i <= volume; i++) {
            Employee e = (i % 2 == 0)
                    ? new FullTimeEmployee(1000 + i, "Emp " + i, "Dept " + (i % 5), 50000.0 + (i * 10))
                    : new PartTimeEmployee(1000 + i, "Emp " + i, "Dept " + (i % 5), 40000.0 + (i * 10));
            Payslip slip = PayslipGenerator.generate(e);
            stressHistory.addPayslip(slip);
        }

        assertEquals("History recorded all 500 records", volume, stressHistory.getRecordCount());
        assertTrue("Total gross disbursed is positive", stressHistory.getTotalGrossDisbursed() > 0);
        assertTrue("Total net paid is positive", stressHistory.getTotalNetPaid() > 0);
        assertTrue("Average net salary is positive", stressHistory.getAverageNetSalary() > 0);

        // Verify retrieval performance and accuracy
        List<Payslip> dept0 = stressHistory.getHistoryByDepartment("Dept 0");
        assertEquals("Dept 0 has exactly 100 records (500 / 5)", 100, dept0.size());

        List<Payslip> recent10 = stressHistory.getRecentPayslips(10);
        assertEquals("Recent 10 retrieved correctly", 10, recent10.size());
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
