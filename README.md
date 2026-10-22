# ⚡ BillWise — Electricity Bill Estimator

![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk\&logoColor=white)
![Java Swing](https://img.shields.io/badge/UI-Java%20Swing-blue)
![Platform](https://img.shields.io/badge/Platform-Windows-lightgrey?logo=windows)
![Project](https://img.shields.io/badge/Project-Academic%20Mini%20Project-6366f1)

**BillWise** is a Java desktop application that estimates electricity bills using a progressive tariff calculation system. It provides a graphical interface for entering electricity consumption, calculating charges across different tariff slabs, and viewing an itemized bill breakdown.

The project demonstrates Java programming, GUI development, input validation, and the separation of calculation logic from the user interface.

---

## ✨ Features

* **Graphical User Interface:** Interactive desktop application developed using Java Swing.
* **Progressive Tariff Calculation:** Calculates charges according to consumption-based slabs.
* **Detailed Bill Breakdown:** Displays the consumption, rate, and charge for each slab.
* **Fixed Monthly Charge:** Includes a fixed charge of ₹100.
* **Input Validation:** Handles blank, non-numeric, and negative inputs.
* **Quick Presets:** Provides sample consumption values for convenient calculations.
* **Reset Functionality:** Restores the application to its initial state.
* **Automated Testing:** Includes test cases for bill calculations and invalid inputs.

---

## 🛠️ Technologies Used

| Technology | Purpose                                       |
| ---------- | --------------------------------------------- |
| Java       | Application development and calculation logic |
| Java Swing | Desktop graphical user interface              |
| Java AWT   | GUI components, graphics, and event handling  |
| Git        | Version control                               |
| GitHub     | Source code hosting                           |

---

## ⚡ Tariff Configuration

BillWise uses the following illustrative electricity tariff rates:

| Consumption Slab     |           Rate |
| -------------------- | -------------: |
| First 100 units      | ₹3.50 per unit |
| Next 200 units       | ₹5.00 per unit |
| Above 300 units      | ₹8.00 per unit |
| Fixed monthly charge |        ₹100.00 |

The calculation follows a **progressive slab system**. Each rate applies only to the units consumed within its respective slab.

### Example Calculation

For a monthly consumption of **250 units**:

| Component                | Calculation |        Amount |
| ------------------------ | ----------- | ------------: |
| Slab 1                   | 100 × ₹3.50 |       ₹350.00 |
| Slab 2                   | 150 × ₹5.00 |       ₹750.00 |
| Slab 3                   | 0 × ₹8.00   |         ₹0.00 |
| Energy Charges           | ₹350 + ₹750 |     ₹1,100.00 |
| Fixed Monthly Charge     | Fixed       |       ₹100.00 |
| **Total Estimated Bill** |             | **₹1,200.00** |

---

## 🏗️ Project Structure

```text
BillWise/
│
├── src/
│   └── com/
│       └── billwise/
│           ├── Main.java
│           ├── BillUI.java
│           ├── BillCalculator.java
│           ├── TariffSlab.java
│           └── BillResult.java
│
├── .gitignore
├── README.md
│
├── compile.bat
├── run.bat
├── test.bat
│
├── compile.ps1
├── run.ps1
└── test.ps1
```

### Main Components

| File                  | Responsibility                                               |
| --------------------- | ------------------------------------------------------------ |
| `Main.java`           | Application entry point                                      |
| `BillUI.java`         | Builds the graphical interface and handles user interactions |
| `BillCalculator.java` | Performs bill calculations and input validation              |
| `TariffSlab.java`     | Represents an individual tariff slab                         |
| `BillResult.java`     | Stores the calculated bill and its breakdown                 |

---

## 🚀 Getting Started

### Prerequisites

* Java Development Kit (JDK) 17 or later
* Windows for the supplied batch scripts

Verify that Java is installed:

```bash
java -version
javac -version
```

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/preetpatel4706-cpu/BillWise-Java-Mini-Project.git
```

**2. Navigate to the project directory**

```bash
cd BillWise-Java-Mini-Project
```

**3. Compile the application**

Run the following command in Command Prompt:

```bat
compile.bat
```

**4. Launch BillWise**

```bat
run.bat
```

**5. Run the automated tests**

```bat
test.bat
```

The project also includes PowerShell scripts for compiling, running, and testing the application.

---

## 🧪 Testing

The automated test suite covers the following scenarios:

| Test Case          |     Input | Expected Bill / Result |
| ------------------ | --------: | ---------------------- |
| Zero consumption   |   0 units | ₹100.00                |
| Slab 1 calculation |  50 units | ₹275.00                |
| Slab boundary      | 100 units | ₹450.00                |
| Multiple slabs     | 250 units | ₹1,200.00              |
| Slab 3 calculation | 400 units | ₹2,250.00              |
| Negative input     |       -50 | Rejected               |
| Non-numeric input  |     `abc` | Rejected               |
| Blank input        |     Empty | Rejected               |

The test suite is designed to verify both the calculation logic and basic input validation.

---

## 🔮 Future Enhancements

* Support for configurable tariff rates.
* Monthly electricity consumption history.
* Bill comparison and graphical usage charts.
* Exporting estimated bills as PDF documents.
* Additional billing components, such as taxes and surcharges.

---

## 📌 Disclaimer

The tariff rates and fixed monthly charge used in BillWise are illustrative values intended for academic demonstration. They do not represent the official electricity tariffs of any particular state or electricity distribution utility.

---

## 👨‍💻 Author

**Preet Patel**

GitHub: [@preetpatel4706-cpu](https://github.com/preetpatel4706-cpu)

---

⭐ *If you find this project useful, consider giving the repository a star!*
