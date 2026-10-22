# BillWise — Java Electricity Bill Estimator

A clean, beginner-friendly desktop application built in Java Swing that calculates progressive electricity bills based on units consumed and marginal tariff slabs.

Designed for:
* **Fifth-Semester B.Tech College Mini Project**
* **Seminar Presentation & Live Demonstration**
* **GitHub Submission**
* **Java Viva Voce Examination**

---

## Table of Contents
1. [Project Overview](#project-overview)
2. [Key Features](#key-features)
3. [Tariff Calculation Model & Examples](#tariff-calculation-model--examples)
4. [Project Architecture](#project-architecture)
5. [Prerequisites & Java Setup](#prerequisites--java-setup)
6. [Compilation & Execution](#compilation--execution)
7. [Testing & Verification Guide](#testing--verification-guide)
8. [Java OOP Concepts Demonstrated](#java-oop-concepts-demonstrated)
9. [Viva Voce Preparation Sheet](#viva-voce-preparation-sheet)
10. [Academic Disclaimer](#academic-disclaimer)

---

## 1. Project Overview

**BillWise** simulates a modern utility billing calculator. Unlike flat-rate billing (where all units are billed at a single rate), real-world electrical utilities use a **progressive tariff slab system**. This means consumers pay lower rates for initial basic consumption, while higher consumption tiers are charged higher rates to encourage energy conservation.

The application provides:
* A graphical desktop interface built with pure Java Swing.
* A strict separation between user interface and mathematical business logic.
* An itemized breakdown of charges across applicable slabs.
* A fixed monthly customer service fee.
* Built-in input validation for negative numbers, empty strings, and non-numeric characters.

---

## 2. Key Features

* **Desktop Graphical Interface**: Clean, modern Swing UI styled using native system Look & Feel.
* **Progressive Slab Computation**: Calculates consumption tier by tier accurately.
* **Itemized Tabular Breakdown**: Shows units consumed, rate per unit, and amount charged per slab.
* **Fixed Monthly Charge**: Standard ₹100.00 base charge included in all bills.
* **Quick-Test Presets**: One-click test buttons for sample inputs (`0`, `50`, `100`, `250`, `400` units) for live seminar demonstrations.
* **Friendly Validation**:
  * Blank input rejected with guidance.
  * Non-numeric text rejected with helpful hints.
  * Negative units rejected (must be $\ge 0$).
  * Zero (`0`) units accepted and accurately bills just the fixed charge.
* **Dual Execution Modes**: Can run as a Swing desktop GUI or headless CLI/automated test runner.

---

## 3. Tariff Calculation Model & Examples

### Illustrative Tariff Configuration
* **Fixed Monthly Charge**: ₹100.00
* **Slab 1 (1 – 100 units)**: ₹3.50 per unit
* **Slab 2 (101 – 300 units)**: ₹5.00 per unit (for the next 200 units)
* **Slab 3 (Above 300 units)**: ₹8.00 per unit

Each rate applies **only** to the units falling within its specific slab.

### Calculation Walkthrough

| Input Units | Slab 1 (1–100 @ ₹3.50) | Slab 2 (101–300 @ ₹5.00) | Slab 3 (>300 @ ₹8.00) | Energy Subtotal | Fixed Charge | Final Bill (INR) |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **0 units** | $0 \times 3.50 = \text{₹}0$ | $0 \times 5.00 = \text{₹}0$ | $0 \times 8.00 = \text{₹}0$ | ₹0.00 | ₹100.00 | **₹100.00** |
| **50 units** | $50 \times 3.50 = \text{₹}175$ | $0 \times 5.00 = \text{₹}0$ | $0 \times 8.00 = \text{₹}0$ | ₹175.00 | ₹100.00 | **₹275.00** |
| **100 units** | $100 \times 3.50 = \text{₹}350$ | $0 \times 5.00 = \text{₹}0$ | $0 \times 8.00 = \text{₹}0$ | ₹350.00 | ₹100.00 | **₹450.00** |
| **250 units** | $100 \times 3.50 = \text{₹}350$ | $150 \times 5.00 = \text{₹}750$ | $0 \times 8.00 = \text{₹}0$ | ₹1,100.00 | ₹100.00 | **₹1,200.00** |
| **400 units** | $100 \times 3.50 = \text{₹}350$ | $200 \times 5.00 = \text{₹}1,000$ | $100 \times 8.00 = \text{₹}800$ | ₹2,150.00 | ₹100.00 | **₹2,250.00** |

> [!NOTE]
> **Mathematical Verification for 400 Units (₹2,250 vs ₹2,450)**:  
> Under the specified rules (Fixed ₹100 + First 100 @ ₹3.50 + Next 200 @ ₹5.00 + Units above 300 @ ₹8.00):
> * Slab 1: $100 \times 3.50 = \text{₹}350.00$
> * Slab 2: $200 \times 5.00 = \text{₹}1,000.00$
> * Slab 3: $100 \times 8.00 = \text{₹}800.00$
> * Fixed Monthly Fee: $\text{₹}100.00$
> * **Exact Sum**: $100 + 350 + 1,000 + 800 = \mathbf{\text{₹}2,250.00}$.  
> *Note for Viva/Seminar*: Some problem sheets list ₹2,450 due to an illustrative arithmetic slip where $1,450 + 1,000 = 2,450$ was mistakenly added instead of $1,450 + 800$, or assuming ₹10.00/unit for Slab 3. BillWise computes the mathematically exact ₹2,250.00.

---

## 4. Project Architecture

```
BillWise/
│
├── src/
│   └── com/
│       └── billwise/
│           ├── Main.java           # Entry point; CLI, test runner & GUI launcher
│           ├── BillUI.java         # Desktop GUI (Swing JFrame, layout, table & event listeners)
│           ├── BillCalculator.java # Core calculation engine & tariff configuration
│           ├── TariffSlab.java     # OOP Model representing an individual tariff slab
│           └── BillResult.java      # Result DTO with itemized breakdown & currency formatting
│
├── bin/                            # Output directory for compiled .class files
├── compile.bat                     # Windows batch compilation script
├── run.bat                         # Windows batch GUI launcher
├── test.bat                        # Windows batch test suite runner
├── compile.ps1                     # PowerShell compilation script
├── run.ps1                         # PowerShell GUI launcher
├── test.ps1                        # PowerShell test runner
├── .gitignore                      # Git ignore file for Java projects
└── README.md                       # Comprehensive project documentation
```

### File Responsibilities
1. **`TariffSlab.java`**:
   * Encapsulates the bounds (`minUnits`, `maxUnits`), rate (`ratePerUnit`), and name for a single progressive tier.
   * Calculates how many units of a total consumption fall within its bounds.
2. **`BillResult.java`**:
   * Immutable data-transfer object (DTO) that holds the calculated bill breakdown, subtotal, fixed charge, and total.
3. **`BillCalculator.java`**:
   * Pure calculation logic decoupled from any GUI code.
   * Validates inputs, iterates over configured slabs, and returns a `BillResult`.
4. **`BillUI.java`**:
   * Swing presentation layer (`JFrame`).
   * Handles user interaction, input parsing, error alerts, table population, and reset logic.
5. **`Main.java`**:
   * Application bootstrap.
   * Launches GUI when run normally, or runs automated tests when supplied with `--test`.

---

## 5. Prerequisites & Java Setup

To compile and run this project, you need the **Java Development Kit (JDK 17 or JDK 21 LTS)** installed on your machine.

### Checking for Java
Open Windows Command Prompt or PowerShell:
```cmd
javac -version
java -version
```

### If Java is Missing
If Windows reports `'javac' is not recognized as an internal or external command`, install the free and open-source **Eclipse Temurin JDK 21 (LTS)** using either method below:

#### Method A: Using Windows Package Manager (Fastest)
Open PowerShell or Command Prompt and run:
```cmd
winget install EclipseAdoptium.Temurin.21.JDK
```
*After installation completes, close and reopen your terminal.*

#### Method B: Official Installer Download
1. Visit [Adoptium Temurin Releases](https://adoptium.net/temurin/releases/).
2. Select **Version 21 (LTS)** $\rightarrow$ **Windows** $\rightarrow$ **x64** $\rightarrow$ **.msi**.
3. Run the installer and check the box **"Set JAVA_HOME variable"** and **"Add to PATH"**.

---

## 6. Compilation & Execution

### Option 1: One-Click Batch Scripts (Recommended for Windows)
1. **To Compile**: Double-click `compile.bat` (or run `./compile.bat` in Command Prompt).
2. **To Run GUI**: Double-click `run.bat` (or run `./run.bat`).
3. **To Run Tests**: Double-click `test.bat` (or run `./test.bat`).

### Option 2: Command Line (javac & java)
Navigate to the project root directory:

```cmd
cd C:\Users\preet\.gemini\antigravity\scratch\BillWise
```

**1. Create bin directory and compile:**
```cmd
if not exist bin mkdir bin
javac -d bin -sourcepath src src/com/billwise/*.java
```

**2. Launch Desktop GUI:**
```cmd
java -cp bin com.billwise.Main
```

**3. Run in Command-Line Mode (Immediate calculation):**
```cmd
java -cp bin com.billwise.Main 250
```

**4. Run Automated Test Verification Suite:**
```cmd
java -cp bin com.billwise.Main --test
```

---

## 7. Testing & Verification Guide

### Automated Verification
Running `test.bat` or `java -cp bin com.billwise.Main --test` runs 8 automated assertions:
1. `0 units` $\rightarrow$ ₹100.00 (Fixed charge only)
2. `50 units` $\rightarrow$ ₹275.00 (Slab 1)
3. `100 units` $\rightarrow$ ₹450.00 (Slab 1 boundary)
4. `250 units` $\rightarrow$ ₹1,200.00 (Slab 1 + Slab 2)
5. `400 units` $\rightarrow$ ₹2,250.00 (Fixed ₹100 + Slab 1 ₹350 + Slab 2 ₹1,000 + Slab 3 ₹800)
6. Negative input (`-50`) rejection
7. Non-numeric input (`abc`) rejection
8. Blank input (`   `) rejection

### Manual UI Test Cases for Viva / Demo

| Test Case | Input | Expected Behavior |
| :--- | :--- | :--- |
| **Zero Consumption** | `0` | Displays ₹100.00 in final bill; slabs show 0.00 units. |
| **Slab 1 Midpoint** | `50` | Displays ₹275.00 ($100 + 50 \times 3.50$). |
| **Slab 1 Boundary** | `100` | Displays ₹450.00 ($100 + 100 \times 3.50$). |
| **Slab 2 Midpoint** | `250` | Displays ₹1,200.00 ($100 + 350 + 150 \times 5.00$). |
| **Slab 3 Entry** | `400` | Displays ₹2,250.00 ($100 + 350 + 1,000 + 100 \times 8.00$). |
| **Decimal / Fractional** | `125.5` | Computes decimal units smoothly without rounding errors. |
| **Negative Number** | `-10` | Error alert: *"Units consumed cannot be negative."* |
| **Blank Input** | *(empty)* | Error alert: *"Please enter the number of units consumed."* |
| **Non-Numeric** | `xyz` | Error alert: *"Invalid input 'xyz'. Please enter numeric digits only."* |
| **Reset Button** | Click Reset | Clears field, resets table, resets total to ₹0.00, restores focus. |
| **Quick Presets** | Click `[250 Units]` | Automatically fills 250 and recalculates in 1 click. |

---

## 8. Java OOP Concepts Demonstrated

* **Encapsulation**: Private fields with accessor methods in `TariffSlab`, `BillCalculator`, and `BillResult`. Data validation prevents invalid states.
* **Separation of Concerns (SoC / MVC)**:
  * Model: `TariffSlab.java`, `BillResult.java`
  * Controller / Service: `BillCalculator.java`
  * View: `BillUI.java`
* **Single Responsibility Principle (SRP)**: `BillCalculator` is unaware of Swing; `BillUI` does not contain mathematical formulas.
* **Exception Handling**: Usage of `try-catch` blocks, `IllegalArgumentException`, and `NumberFormatException` to guarantee robustness against user error.
* **Event-Driven Programming**: `ActionListener` on buttons and `KeyAdapter` on textfields handling asynchronous UI events.
* **Thread Safety in Swing**: GUI initialized safely on Swing's **Event Dispatch Thread (EDT)** via `SwingUtilities.invokeLater()`.

---

## 9. Viva Voce Preparation Sheet

### Q1: What is a progressive electricity tariff?
> **Answer**: A progressive tariff divides electricity consumption into tiers (slabs). Lower consumption blocks are priced lower, and as consumption crosses higher thresholds, subsequent units are charged at higher rates. Each higher rate applies only to units within that specific bracket, not to the entire consumption.

### Q2: Why did you separate `BillCalculator` from `BillUI`?
> **Answer**: To follow the **Separation of Concerns** principle. Keeping business logic independent of UI makes the calculation engine testable (e.g., via CLI or unit tests), reusable in other interfaces (like mobile or web), and easier to maintain.

### Q3: What is the Event Dispatch Thread (EDT) in Java Swing?
> **Answer**: Swing components are not thread-safe. All UI creation and updates must occur on a dedicated thread called the **Event Dispatch Thread (EDT)**. We use `SwingUtilities.invokeLater()` to post the UI startup runnable onto the EDT queue.

### Q4: How does `TariffSlab.calculateUnitsInSlab()` work mathematically?
> **Answer**:
> - If `totalUnits <= minUnits`, 0 units fall into this slab.
> - Otherwise, the applicable ceiling is `Math.min(totalUnits, maxUnits)`.
> - Units charged in this slab $= \text{applicableCeiling} - \text{minUnits}$.

### Q5: How do you handle non-numeric or negative inputs?
> **Answer**: `BillCalculator.parseAndValidateInput()` strips whitespace, checks for empty strings, attempts parsing via `Double.parseDouble()`, catches `NumberFormatException`, and checks if `units < 0`. If invalid, it throws an `IllegalArgumentException` with a user-friendly message displayed via `JOptionPane`.

---

## 10. Academic Disclaimer

> **Note**: Rates and slab thresholds used in this software (₹100 fixed charge, ₹3.50, ₹5.00, and ₹8.00 per unit) are **illustrative examples** designed for academic evaluation, project demonstration, and viva examinations. They do not represent official electricity tariffs of any specific state or power distribution utility.
