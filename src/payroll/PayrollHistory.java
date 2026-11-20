package payroll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the chronological archive of generated employee payslips.
 * Employs standard Java Collections (ArrayList) to provide dynamic storage,
 * unmodifiable views, and defensive integrity checks.
 *
 * Demonstrates:
 * - Java Collections Framework: Utilizes ArrayList for dynamic sequential storage.
 * - Defensive Encapsulation: Exposes unmodifiable collection views to prevent unauthorized mutations.
 * - Integrity Protection: Guards against null records and duplicate payslip IDs.
 */
public class PayrollHistory {
    private final List<Payslip> historyList;

    public PayrollHistory() {
        this.historyList = new ArrayList<>();
    }

    /**
     * Records a payslip into history after defensive integrity checks.
     *
     * @param payslip The validated payslip to archive.
     * @throws IllegalArgumentException if payslip is null or duplicate ID exists.
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
     * Returns null if not found.
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
     * Removes a payslip record by ID. Returns true if removed, false otherwise.
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

    public void displayHistory() {
        if (historyList.isEmpty()) {
            System.out.println("\n[!] No payroll history available yet. Generate payslips first.");
            return;
        }

        System.out.println("\n" + "=".repeat(95));
        System.out.println("                             PAYROLL TRANSACTION HISTORY");
        System.out.println("=".repeat(95));
        System.out.printf("%-9s | %-5s | %-16s | %-10s | %10s | %10s | %10s | %-11s\n",
                "Slip ID", "EmpID", "Employee Name", "Type", "Gross", "Deductions", "Net Pay", "Date");
        System.out.println("-".repeat(95));

        for (Payslip p : historyList) {
            System.out.printf("%-9s | %-5d | %-16s | %-10s | %10.2f | %10.2f | %10.2f | %-11s\n",
                    p.getPayslipId(), p.getEmployeeId(), p.getEmployeeName(),
                    p.getEmployeeType(), p.getGrossSalary(), p.getTotalDeductions(),
                    p.getNetSalary(), p.getGenerationDate());
        }
        System.out.println("=".repeat(95));
        System.out.println("Total Records: " + historyList.size());
    }

    public boolean isEmpty() {
        return historyList.isEmpty();
    }

    public int getRecordCount() {
        return historyList.size();
    }
}
