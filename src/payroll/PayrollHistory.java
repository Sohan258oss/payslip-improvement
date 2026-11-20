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
