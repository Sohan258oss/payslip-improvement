package payroll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the chronological archive of generated employee payslips.
 * Employs standard Java Collections (ArrayList) to provide dynamic storage,
 * unmodifiable views, multi-criteria searching, and financial summary analytics.
 *
 * Demonstrates:
 * - Java Collections Framework: Utilizes ArrayList for dynamic sequential storage.
 * - Defensive Encapsulation: Exposes unmodifiable collection views to prevent unauthorized mutations.
 * - Financial Analytics: Computes cumulative payroll metrics (gross, taxes, PF, net, averages).
 */
public class PayrollHistory {
    private static final int TABLE_WIDTH = 102;

    private final List<Payslip> historyList;

    public PayrollHistory() {
        this.historyList = new ArrayList<>();
    }

    /**
     * Records a payslip into history after defensive integrity checks.
     */
    public void addPayslip(Payslip payslip) {
        if (payslip == null) {
            throw new IllegalArgumentException("Cannot record a null payslip into payroll history.");
        }
        if (hasPayslip(payslip.getPayslipId())) {
            throw new IllegalArgumentException("Duplicate payslip ID: " + payslip.getPayslipId() + " is already recorded in history.");
        }
        historyList.add(payslip);
    }

    /**
     * Checks if a payslip with the specified ID already exists in history.
     */
    public boolean hasPayslip(String payslipId) {
        if (payslipId == null || payslipId.trim().isEmpty()) {
            return false;
        }
        for (Payslip p : historyList) {
            if (p.getPayslipId().equalsIgnoreCase(payslipId.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns an unmodifiable view of all recorded payslips.
     */
    public List<Payslip> getHistory() {
        return Collections.unmodifiableList(historyList);
    }

    /**
     * Returns a detached copy of the payslip history list.
     */
    public List<Payslip> getHistoryCopy() {
        return new ArrayList<>(historyList);
    }

    /**
     * Retrieves all payslips for a specific employee ID.
     */
    public List<Payslip> getHistoryByEmployeeId(int employeeId) {
        if (employeeId <= 0) {
            return Collections.emptyList();
        }
        List<Payslip> result = new ArrayList<>();
        for (Payslip p : historyList) {
            if (p.getEmployeeId() == employeeId) {
                result.add(p);
            }
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * Finds a single payslip by its reference ID (case-insensitive).
     */
    public Payslip findPayslipById(String payslipId) {
        if (payslipId == null || payslipId.trim().isEmpty()) {
            return null;
        }
        for (Payslip p : historyList) {
            if (p.getPayslipId().equalsIgnoreCase(payslipId.trim())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Searches for payslips by employee name substring (case-insensitive).
     */
    public List<Payslip> searchByEmployeeName(String nameQuery) {
        if (nameQuery == null || nameQuery.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String query = nameQuery.trim().toLowerCase();
        List<Payslip> matches = new ArrayList<>();
        for (Payslip p : historyList) {
            if (p.getEmployeeName().toLowerCase().contains(query)) {
                matches.add(p);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    /**
     * Filters payslips by department name (case-insensitive).
     */
    public List<Payslip> getHistoryByDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String deptQuery = department.trim().toLowerCase();
        List<Payslip> matches = new ArrayList<>();
        for (Payslip p : historyList) {
            if (p.getDepartment().toLowerCase().equalsIgnoreCase(deptQuery)) {
                matches.add(p);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    /**
     * Filters payslips by employee type (Full-Time / Part-Time).
     */
    public List<Payslip> getHistoryByEmployeeType(String employeeType) {
        if (employeeType == null || employeeType.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String typeQuery = employeeType.trim().toLowerCase();
        List<Payslip> matches = new ArrayList<>();
        for (Payslip p : historyList) {
            if (p.getEmployeeType().toLowerCase().equalsIgnoreCase(typeQuery)) {
                matches.add(p);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    /**
     * Filters payslips for a specific year and month.
     */
    public List<Payslip> getHistoryByYearAndMonth(int year, int month) {
        if (year <= 0 || month < 1 || month > 12) {
            return Collections.emptyList();
        }
        List<Payslip> matches = new ArrayList<>();
        for (Payslip p : historyList) {
            if (p.getPayPeriodDate().getYear() == year && p.getPayPeriodDate().getMonthValue() == month) {
                matches.add(p);
            }
        }
        return Collections.unmodifiableList(matches);
    }

    /**
     * Returns the most recent N payslips in reverse chronological order.
     */
    public List<Payslip> getRecentPayslips(int limit) {
        if (limit <= 0 || historyList.isEmpty()) {
            return Collections.emptyList();
        }
        int count = Math.min(limit, historyList.size());
        List<Payslip> recent = new ArrayList<>(count);
        for (int i = historyList.size() - 1; i >= historyList.size() - count; i--) {
            recent.add(historyList.get(i));
        }
        return Collections.unmodifiableList(recent);
    }

    /**
     * Retrieves the most recently recorded payslip, or null if history is empty.
     */
    public Payslip getLatestPayslip() {
        if (historyList.isEmpty()) {
            return null;
        }
        return historyList.get(historyList.size() - 1);
    }

    /**
     * Removes a payslip record by ID.
     */
    public boolean removePayslip(String payslipId) {
        if (payslipId == null || payslipId.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < historyList.size(); i++) {
            if (historyList.get(i).getPayslipId().equalsIgnoreCase(payslipId.trim())) {
                historyList.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Clears all recorded payslips from history.
     */
    public void clearHistory() {
        historyList.clear();
    }

    // Financial Analytics Methods
    public double getTotalGrossDisbursed() {
        double total = 0.0;
        for (Payslip p : historyList) {
            total += p.getGrossSalary();
        }
        return total;
    }

    public double getTotalPfCollected() {
        double total = 0.0;
        for (Payslip p : historyList) {
            total += p.getPf();
        }
        return total;
    }

    public double getTotalTaxCollected() {
        double total = 0.0;
        for (Payslip p : historyList) {
            total += p.getTax();
        }
        return total;
    }

    public double getTotalDeductionsCollected() {
        double total = 0.0;
        for (Payslip p : historyList) {
            total += p.getTotalDeductions();
        }
        return total;
    }

    public double getTotalNetPaid() {
        double total = 0.0;
        for (Payslip p : historyList) {
            total += p.getNetSalary();
        }
        return total;
    }

    public double getAverageNetSalary() {
        if (historyList.isEmpty()) {
            return 0.0;
        }
        return getTotalNetPaid() / historyList.size();
    }

    /**
     * Displays complete transaction history with precise table borders.
     */
    public void displayHistory() {
        if (historyList.isEmpty()) {
            System.out.println("\n[!] No payroll history available yet. Generate payslips first.");
            return;
        }

        printTable(historyList, "PAYROLL TRANSACTION HISTORY");
    }

    /**
     * Displays transaction history filtered for a specific employee ID.
     */
    public void displayHistoryByEmployee(int employeeId) {
        List<Payslip> employeeSlips = getHistoryByEmployeeId(employeeId);
        if (employeeSlips.isEmpty()) {
            System.out.println("\n[!] No payroll records found for Employee ID: " + employeeId);
            return;
        }
        printTable(employeeSlips, "PAYROLL HISTORY FOR EMPLOYEE ID: " + employeeId);
    }

    /**
     * Displays cumulative financial metrics summary.
     */
    public void displayFinancialSummary() {
        if (historyList.isEmpty()) {
            System.out.println("\n[!] No financial summary available. History is empty.");
            return;
        }

        System.out.println("\n+-------------------------------------------------------------+");
        System.out.println("|               CUMULATIVE PAYROLL FINANCIAL SUMMARY          |");
        System.out.println("+-------------------------------------------------------------+");
        System.out.printf("| Total Payslips Issued:             %24d |\n", historyList.size());
        System.out.printf("| Total Gross Disbursed:             Rs. %20.2f |\n", getTotalGrossDisbursed());
        System.out.printf("| Total Provident Fund (PF):         Rs. %20.2f |\n", getTotalPfCollected());
        System.out.printf("| Total Income Tax Withheld:         Rs. %20.2f |\n", getTotalTaxCollected());
        System.out.printf("| Total Deductions Applied:          Rs. %20.2f |\n", getTotalDeductionsCollected());
        System.out.println("+-------------------------------------------------------------+");
        System.out.printf("| TOTAL NET DISBURSED:               Rs. %20.2f |\n", getTotalNetPaid());
        System.out.printf("| AVERAGE NET SALARY PER SLIP:       Rs. %20.2f |\n", getAverageNetSalary());
        System.out.println("+-------------------------------------------------------------+");
    }

    private void printTable(List<Payslip> records, String title) {
        System.out.println("\n" + "=".repeat(TABLE_WIDTH));
        int padLeft = (TABLE_WIDTH - title.length()) / 2;
        System.out.println(" ".repeat(Math.max(0, padLeft)) + title);
        System.out.println("=".repeat(TABLE_WIDTH));
        System.out.printf("%-9s | %-5s | %-16s | %-10s | %10s | %10s | %10s | %-11s\n",
                "Slip ID", "EmpID", "Employee Name", "Type", "Gross", "Deductions", "Net Pay", "Date");
        System.out.println("-".repeat(TABLE_WIDTH));

        for (Payslip p : records) {
            String truncName = p.getEmployeeName();
            if (truncName.length() > 16) {
                truncName = truncName.substring(0, 14) + "..";
            }
            System.out.printf("%-9s | %-5d | %-16s | %-10s | %10.2f | %10.2f | %10.2f | %-11s\n",
                    p.getPayslipId(), p.getEmployeeId(), truncName,
                    p.getEmployeeType(), p.getGrossSalary(), p.getTotalDeductions(),
                    p.getNetSalary(), p.getGenerationDate());
        }
        System.out.println("=".repeat(TABLE_WIDTH));
        System.out.println("Total Records: " + records.size());
    }

    public boolean isEmpty() {
        return historyList.isEmpty();
    }

    public int getRecordCount() {
        return historyList.size();
    }
}
