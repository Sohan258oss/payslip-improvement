# Employee Payroll Management System

A console-based Core Java application designed to manage employee records, compute dynamic salaries with real-world allowances and tax deductions, generate itemized payslips, and maintain payroll transaction history.

Developed with pure Core Java (no external frameworks or databases) adhering strictly to Object-Oriented Programming (OOP) principles, a custom Data Structure implemented from scratch, Java Collections, and custom exception handling.

---

## 1. Project Overview

The Employee Payroll Management System provides a clean, robust, and beginner-friendly solution for organizations to manage full-time and part-time staff. It features automated salary calculations incorporating:
- House Rent Allowance (HRA)
- Dearness Allowance (DA)
- Provident Fund (PF)
- Slab-based Income Tax deductions
- Detailed printable Payslips with ASCII box-drawing
- Historical transaction logging, searching, and financial summary analytics
- Batch payroll processing for organizations and departments

---

## 2. Key Features

1. **Add Employee**: Register new Full-Time or Part-Time employees with automated validation for unique IDs and positive salary figures.
2. **View Employees**: Display all registered employees in a clean tabular view directly from the custom singly linked list.
3. **Search Employee**: Search for an employee by ID and view complete profile details and current salary breakdown.
4. **Remove Employee**: Unlink and delete an employee record by ID with proper exception handling if not found.
5. **Generate Payslip**: Calculate gross earnings, deductions, and net salary, printing a formatted ASCII payslip card and saving the transaction to payroll history.
6. **View Payroll History**: View all generated payslips across sessions using Java Collections (`ArrayList`), accompanied by cumulative financial metrics.
7. **Multi-Criteria History Search**: Query transaction history by Payslip ID, employee ID, employee name substring, department, employee type, or year/month.
8. **Batch Payroll Generation**: Generate payroll for all registered employees or specific departments in one unified operation.
9. **Input Validation**: Clean handling of invalid numeric inputs, negative salaries, duplicate records, and non-existent IDs.

---

## 3. Project Structure

```
EmployeePayrollManagement/
├── src/
│   ├── employee/
│   │   ├── Employee.java
│   │   ├── FullTimeEmployee.java
│   │   ├── PartTimeEmployee.java
│   │   └── EmployeeManager.java
│   ├── salary/
│   │   ├── Salary.java
│   │   ├── SalaryCalculator.java
│   │   └── Allowance.java
│   ├── deduction/
│   │   ├── Deduction.java
│   │   ├── TaxCalculator.java
│   │   └── NetSalaryCalculator.java
│   ├── payroll/
│   │   ├── Payslip.java
│   │   ├── PayslipGenerator.java
│   │   ├── PayrollHistory.java
│   │   └── PayrollManager.java
│   ├── exceptions/
│   │   ├── DuplicateEmployeeException.java
│   │   ├── InvalidSalaryException.java
│   │   └── EmployeeNotFoundException.java
│   └── Main.java
├── test/
│   └── PayrollTestRunner.java
└── README.md
```

---

## 4. Object-Oriented Programming (OOP) Concepts Used

- **Abstraction**:
  - `Employee` is an abstract base class defining the contract for employee behavior (`calculateAllowance()`, `calculateDeduction()`, `calculateGrossSalary()`, `calculateNetSalary()`, `getEmployeeType()`).
  - Implementation details are hidden from callers like `EmployeeManager`, `PayrollManager`, and `Main`.
- **Encapsulation & Immutability**:
  - All domain fields across `Employee`, `Salary`, `Allowance`, `Deduction`, and `Payslip` are declared `private` or `private final`.
  - Controlled read and write access is exposed via public getter and setter methods.
  - `Payslip` is designed as an immutable Value Object where all fields are `private final` to ensure historical records cannot be modified after generation.
- **Inheritance**:
  - `FullTimeEmployee` and `PartTimeEmployee` inherit core attributes and common methods from the `Employee` base class using the `extends` keyword.
  - Custom exceptions inherit from `java.lang.Exception`.
- **Polymorphism**:
  - Dynamic Method Dispatch (Runtime Polymorphism): `FullTimeEmployee` and `PartTimeEmployee` provide specialized implementations for `calculateAllowance()` and `getEmployeeType()`.
  - Calling `employee.calculateNetSalary()` executes the appropriate subclass logic dynamically.
  - `Payslip` implements `Comparable<Payslip>` to provide natural chronological ordering.

---

## 5. Custom Data Structure (DSA from Scratch)

To fulfill academic and faculty requirements, employee records are stored in a **Custom Singly Linked List** implemented inside `EmployeeManager.java`:

- **Node Structure**:
  ```java
  private static class Node {
      Employee data;
      Node next;
  }
  ```
- **Operations Implemented**:
  - **Insertion**: Appends new nodes to the tail of the linked list ($O(n)$ traversal, preserving insertion order).
  - **Search**: Linear traversal through nodes comparing employee IDs ($O(n)$ time complexity).
  - **Deletion**: Linear traversal updating pointers to bypass and unlink target nodes ($O(n)$ time complexity).
  - **Display / Traversal**: Iterating from `head` through `next` pointers to display formatted output.

---

## 6. Java Collections Framework

- **`ArrayList<Payslip>`** in `PayrollHistory.java`:
  - Used meaningfully to maintain chronological records of all generated payslips.
  - Provides dynamic array resizing, $O(1)$ random access, and indexed iteration.
  - Protected by `Collections.unmodifiableList(historyList)` to return read-only collection views and prevent external mutation.
  - Implements duplicate ID guard checking before insertion to maintain relational integrity.
- **`Comparable<Payslip>`** & Sorting:
  - `Payslip` implements `Comparable<Payslip>` enabling natural sorting by pay period date and payslip ID.
- **`Set` & `Map` Compatibility**:
  - `Payslip` overrides `equals(Object o)` and `hashCode()` based on `payslipId` and `employeeId`, allowing seamless integration with `HashSet`, `HashMap`, and collections algorithms.

---

## 7. Custom Exceptions

Three domain-specific checked exceptions are defined and handled with `try-catch` blocks:

1. **`DuplicateEmployeeException`**: Thrown when attempting to register an employee whose ID already exists in the singly linked list.
2. **`InvalidSalaryException`**: Thrown when an entered basic salary is less than or equal to 0.
3. **`EmployeeNotFoundException`**: Thrown when searching, removing, or generating a payslip for a non-existent employee ID.

---

## 8. Salary Calculation Rules

The financial logic is strictly consistent across the entire application:

| Component | Rule / Rate |
| :--- | :--- |
| **HRA (House Rent Allowance)** | 20% of Basic Salary (Both Full-Time and Part-Time) |
| **DA (Dearness Allowance)** | 10% of Basic Salary for **Full-Time**; **0%** for **Part-Time** |
| **Gross Salary** | `Basic Salary + HRA + DA` |
| **PF (Provident Fund)** | 12% of Basic Salary |
| **Income Tax** | Calculated on **Gross Salary**: <br>• Gross ≤ 50,000: **0%** <br>• 50,000 < Gross ≤ 100,000: **10%** <br>• Gross > 100,000: **20%** |
| **Net Salary** | `Gross Salary - PF - Income Tax` |

### Calculation Example:
- **Full-Time Employee** with Basic Salary = ₹60,000:
  - HRA (20%) = ₹12,000
  - DA (10%) = ₹6,000
  - Gross Salary = ₹60,000 + ₹12,000 + ₹6,000 = **₹78,000**
  - PF (12% of basic) = ₹7,200
  - Tax (10% of gross ₹78,000) = ₹7,800
  - Total Deductions = ₹7,200 + ₹7,800 = ₹15,000
  - Net Payable Salary = ₹78,000 - ₹15,000 = **₹63,000**

- **Part-Time Employee** with Basic Salary = ₹40,000:
  - HRA (20%) = ₹8,000
  - DA (0%) = ₹0
  - Gross Salary = ₹40,000 + ₹8,000 + ₹0 = **₹48,000**
  - PF (12% of basic) = ₹4,800
  - Tax (0% since Gross ≤ 50,000) = ₹0
  - Total Deductions = ₹4,800
  - Net Payable Salary = ₹48,000 - ₹4,800 = **₹43,200**

---

## 9. Member 4 Module Deep-Dive: Payslip, Payroll Management & History

Member 4 owns and maintains the payroll generation, formatting, and history tracking infrastructure:

### 1. `Payslip.java` (Domain Entity & Value Object)
- **Immutability**: All fields are `private final`. Once generated, a payslip cannot be altered, preserving an immutable audit record.
- **Defensive Invariant Validation**: Constructor validates that:
  - `employeeId > 0`, non-empty names, non-empty department and type.
  - All financial amounts are non-negative, finite (no `NaN` or `Infinity`).
  - Mathematical invariants hold true within 0.05 tolerance:
    - $\text{Gross} = \text{Basic} + \text{HRA} + \text{DA}$
    - $\text{Total Deductions} = \text{PF} + \text{Tax}$
    - $\text{Net Salary} = \text{Gross} - \text{Total Deductions}$
- **Temporal Tracking**: Stores both operational `LocalDate payPeriodDate` and system timestamp `LocalDateTime generatedAt`.
- **ASCII Box-Drawing Engine**:
  - Perfectly aligned 63-character wide card border.
  - Dynamically truncates overflowing metadata with `..` to guarantee zero border clipping.
  - Provides `getFormattedPayslip()` returning a printable `String` decoupled from console output.

### 2. `PayslipGenerator.java` (Factory & Sequence Coordinator)
- **Atomic Thread-Safety**: Replaces standard static integer increments with `java.util.concurrent.atomic.AtomicInteger` (`payslipCounter.getAndIncrement()`), ensuring lock-free concurrency safety.
- **Custom Pay Period Dispatch**: Allows generating payslips for the current month or explicit retroactive periods.
- **Monetary Precision Rounding**: Implements `roundCurrency()` to round components to 2 decimal places (paise/cents) and reconciles half-cent floating-point disparities.
- **Sequence Inspection**: Provides `peekNextId()`, `getCurrentCounter()`, and `resetCounter()`.

### 3. `PayrollHistory.java` (Archive & Analytics Engine)
- **Collection Protection**: Uses `ArrayList<Payslip>` backed by `Collections.unmodifiableList()` to prevent external caller tampering.
- **Integrity Guards**: Prohibits `null` payslips and blocks duplicate payslip IDs from being inserted.
- **Multi-Criteria Search Engine**:
  - `findPayslipById(String payslipId)`: Case-insensitive $O(n)$ search.
  - `searchByEmployeeName(String nameQuery)`: Substring match.
  - `getHistoryByDepartment(String department)`: Department cohort query.
  - `getHistoryByEmployeeType(String employeeType)`: Full-Time / Part-Time query.
  - `getHistoryByYearAndMonth(int year, int month)`: Temporal query.
  - `getRecentPayslips(int limit)`: Reverse-chronological recent records.
- **Financial Analytics**: Computes aggregate metrics across the organization:
  - `getTotalGrossDisbursed()`
  - `getTotalPfCollected()`
  - `getTotalTaxCollected()`
  - `getTotalDeductionsCollected()`
  - `getTotalNetPaid()`
  - `getAverageNetSalary()`
  - `displayFinancialSummary()`: Formatted executive summary card.
- **Precise 102-Character Table**: Aligns all column widths and headers without row overflow.

### 4. `PayrollManager.java` (High-Level Orchestrator)
- **Dependency Guarding**: Ensures non-null `EmployeeManager` and `PayrollHistory` references via constructor checks.
- **Single Payslip Generation**: Looks up employee by ID, verifies existence, delegates calculation, archives in history, and displays formatted slip.
- **Batch Payroll Generation**:
  - `generatePayrollForAll(LocalDate payPeriodDate)`: Generates payslips for all active employees in one pass.
  - `generatePayrollForDepartment(String dept, LocalDate payPeriodDate)`: Generates cohort payroll.
- **Dashboard Reporting**: Displays both itemized transaction records and cumulative financial summaries.

---

## 10. Automated Test Suite (`test/PayrollTestRunner.java`)

A standalone Core Java test suite verifies the entire payroll module without requiring external testing dependencies (JUnit-free):

### Running the Test Suite:
```bash
# Compile everything including tests
javac -d bin src/exceptions/*.java src/salary/*.java src/deduction/*.java src/employee/*.java src/payroll/*.java src/Main.java test/PayrollTestRunner.java

# Execute test suite
java -cp bin test.PayrollTestRunner
```

### Test Coverage Highlights (127 Assertions):
1. **Full-Time Calculations**: Basic, HRA (20%), DA (10%), Gross, PF (12%), Tax (10%), Total Deductions, Net Salary.
2. **Part-Time Calculations**: Basic, HRA (20%), DA (0%), Gross, PF (12%), Tax (0% for $\le$ 50k, 20% for $>$ 100k).
3. **Temporal Support**: Pay period preservation, month/year formatting, `Comparable` ordering.
4. **Data Validation & Invariants**: Rejection of zero/negative IDs, empty names, negative amounts, broken math invariants.
5. **Collection Contracts**: Reflexive/symmetric `equals()`, consistent `hashCode()`, duplicate deduplication in `HashSet`.
6. **Formatting Alignment**: Exact 63-character line width verification, long string overflow truncation.
7. **Thread Safety**: Multi-threaded concurrency test (10 threads generating 200 IDs concurrently with 0 collisions).
8. **Defensive History**: Rejection of null slips, duplicate ID prevention, unmodifiable list mutation guard, slip removal, clear history.
9. **Search Engine**: Exact ID, case-insensitive ID, name substring, department, employee type, year/month, recent limit.
10. **Financial Analytics**: Total gross, PF, tax, deductions, net, and average net calculations.
11. **Manager Batch Processing**: Organization-wide batch generation, department-specific batch, empty registry handling.
12. **Tax Boundary Slabs**: Gross $\le$ 50k (0%), Gross 50k–100k (10%), Gross $>$ 100k (20%), floating-point cents precision.
13. **High-Volume Stress**: 500-payslip stress test verifying throughput, search accuracy, and memory stability.

---

## 11. Compilation and Execution Instructions

### Prerequisites
- Java Development Kit (JDK 8 or higher, e.g. JDK 17, 21).

### Step 1: Open Terminal
Navigate to the root directory `EmployeePayrollManagement`:
```bash
cd EmployeePayrollManagement
```

### Step 2: Compile All Sources
Compile all packages into an output directory `bin`:
```bash
javac -d bin src/exceptions/*.java src/salary/*.java src/deduction/*.java src/employee/*.java src/payroll/*.java src/Main.java test/PayrollTestRunner.java
```

### Step 3: Run the Application
```bash
java -cp bin Main
```

---

## 12. College Viva Q&A Guide (Member 4 — Payroll & History)

Use these explanations when presenting to professors, external examiners, or during code defense:

### Q1: Why is the `Payslip` class designed to be immutable?
> **Answer**: A payslip is an official financial legal document generated for a specific pay period. Once issued, its earnings, taxes, and net pay figures must never be altered in memory. By marking all attributes `private final` and not exposing setter methods, we guarantee audit trail integrity, prevent accidental data tampering, and make instances inherently thread-safe.

### Q2: How does `PayslipGenerator` ensure thread-safe ID generation without using `synchronized`?
> **Answer**: `PayslipGenerator` uses `java.util.concurrent.atomic.AtomicInteger`. The method `payslipCounter.getAndIncrement()` relies on low-level CPU Compare-And-Swap (CAS) instructions. This provides lock-free, thread-safe atomic increments with higher throughput than synchronized blocks, completely preventing duplicate payslip IDs even if multiple payroll threads run in parallel.

### Q3: Why did you implement `equals()` and `hashCode()` on `Payslip`?
> **Answer**: In Java, domain objects placed in collections (`HashSet`, `HashMap`, or searched with `List.contains()`) rely on `equals()` and `hashCode()`. Without overriding them, Java falls back to `Object` reference identity ($==$). We implemented `equals()` and `hashCode()` based on `payslipId` and `employeeId`, ensuring that business equality matches real-world document identity.

### Q4: How does `PayrollHistory` prevent external callers from corrupting its records?
> **Answer**: `PayrollHistory` uses **defensive encapsulation**. When a caller invokes `getHistory()`, the method returns `Collections.unmodifiableList(historyList)`. If an external class attempts to call `.clear()`, `.add()`, or `.remove()` on that list, Java immediately throws an `UnsupportedOperationException`.

### Q5: What is the purpose of mathematical invariant checks in the `Payslip` constructor?
> **Answer**: Defensive programming requires that internal object state always remains consistent. If an upstream bug or faulty input calculates a gross salary that does not equal basic + allowances, or deductions that do not equal PF + tax, `Payslip` throws an `IllegalArgumentException`. This prevents invalid financial calculations from ever reaching the database or history archive.

### Q6: What is the time complexity ($O$) of search operations in `PayrollHistory`?
> **Answer**: Because payslips are stored in an `ArrayList<Payslip>`, linear searches like `findPayslipById()`, `getHistoryByEmployeeId()`, and `searchByEmployeeName()` run in $O(n)$ time where $n$ is the number of payslips. Insertion via `addPayslip()` runs in amortized $O(1)$ time, and indexed access is $O(1)$.

### Q7: How are floating-point monetary discrepancies handled?
> **Answer**: In IEEE-754 floating-point arithmetic (`double`), decimal fractions can accumulate tiny rounding errors (e.g. $0.1 + 0.2 = 0.30000000000000004$). We implemented a standard `roundCurrency(amount)` method using `Math.round(amount * 100.0) / 100.0` to round all monetary figures to exactly two decimal places (cents/paise) and applied an epsilon tolerance ($\epsilon = 0.05$) during invariant validation.

### Q8: How did you solve table border misalignment in console output?
> **Answer**: Fixed-width tables often break when strings of varying lengths are printed or when borders have mismatched character counts. We calibrated exact column and separator widths (63 characters for payslip cards, 102 characters for history tables) and introduced string truncation (`fitString()`) that clips strings exceeding column limits with `..`, guaranteeing that ASCII box borders never wobble.
