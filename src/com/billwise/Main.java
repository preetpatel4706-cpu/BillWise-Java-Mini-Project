package com.billwise;

import java.awt.GraphicsEnvironment;
import javax.swing.SwingUtilities;

/**
 * Main application entry point for BillWise — Electricity Bill Estimator.
 * 
 * Supports both Graphical User Interface (Swing) mode and Command-Line / Test modes:
 *  - Default (no args): Launches the Swing desktop application.
 *  - "--test" or "-t" : Executes automated test case verification.
 *  - "<units>"        : Prints an immediate CLI calculation breakdown.
 */
public class Main {

    public static void main(String[] args) {
        // Mode 1: Automated Test Suite Verification
        if (args.length > 0 && (args[0].equalsIgnoreCase("--test") || args[0].equalsIgnoreCase("-t"))) {
            runAutomatedTests();
            return;
        }

        // Mode 2: Quick Command-Line Calculation
        if (args.length > 0 && !args[0].startsWith("-")) {
            runCliCalculation(args[0]);
            return;
        }

        if (args.length > 0 && (args[0].equalsIgnoreCase("--help") || args[0].equalsIgnoreCase("-h"))) {
            printHelp();
            return;
        }

        // Mode 3: Desktop Swing Application
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("====================================================");
            System.out.println("  BillWise — Electricity Bill Estimator (Headless)  ");
            System.out.println("====================================================");
            System.out.println("A graphical desktop environment is not detected in this shell session.");
            System.out.println("Running automated calculation verification suite instead:\n");
            runAutomatedTests();
            return;
        }

        // Launch Desktop GUI on Swing's Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            BillUI ui = new BillUI();
            ui.setVisible(true);
        });
    }

    /**
     * Executes and prints verification results for the 5 illustrative test cases.
     */
    public static boolean runAutomatedTests() {
        System.out.println("==============================================================");
        System.out.println("       BILLWISE — PROGRESSIVE TARIFF VERIFICATION TESTS       ");
        System.out.println("==============================================================");
        System.out.println("Tariff Model:");
        System.out.println("  - Fixed Charge       : ₹100.00");
        System.out.println("  - Slab 1 (1–100 units): ₹3.50/unit");
        System.out.println("  - Slab 2 (101–300)   : ₹5.00/unit");
        System.out.println("  - Slab 3 (>300 units): ₹8.00/unit");
        System.out.println("--------------------------------------------------------------");

        BillCalculator calculator = new BillCalculator();

        // Required Test Cases from Project Specification
        // Note on 400 units: Under the specified tariff (Fixed ₹100 + 100@₹3.50 + 200@₹5.00 + 100@₹8.00),
        // the exact progressive sum is ₹100 + ₹350 + ₹1,000 + ₹800 = ₹2,250.00.
        TestCase[] testCases = {
                new TestCase(0.0, 100.00, "0 units (Fixed charge only: ₹100)"),
                new TestCase(50.0, 275.00, "50 units (Within Slab 1: 100 + 50*3.50 = ₹275.00)"),
                new TestCase(100.0, 450.00, "100 units (Full Slab 1: 100 + 100*3.50 = ₹450.00)"),
                new TestCase(250.0, 1200.00, "250 units (Slab 1 + partial Slab 2: 100 + 350 + 150*5.00 = ₹1,200.00)"),
                new TestCase(400.0, 2250.00, "400 units (Fixed ₹100 + Slab 1 ₹350 + Slab 2 ₹1,000 + Slab 3 100*₹8 = ₹2,250.00)")
        };

        boolean allPassed = true;

        for (int i = 0; i < testCases.length; i++) {
            TestCase tc = testCases[i];
            BillResult result = calculator.calculateBill(tc.units);
            boolean passed = Math.abs(result.getTotalBill() - tc.expectedBill) < 0.001;

            if (!passed) {
                allPassed = false;
            }

            System.out.printf("Test #%d: Input = %6.1f units | Expected = ₹%,8.2f | Actual = ₹%,8.2f | Status: %s\n",
                    i + 1, tc.units, tc.expectedBill, result.getTotalBill(), (passed ? "[PASS]" : "[FAIL]"));
            System.out.println("   Details: " + tc.explanation);
        }

        // Test Negative Input Handling
        System.out.println("--------------------------------------------------------------");
        System.out.print("Test #6: Negative Input (-50 units) rejection  ... ");
        try {
            calculator.calculateBill(-50.0);
            System.out.println("[FAIL] (Expected IllegalArgumentException)");
            allPassed = false;
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] (Correctly rejected: " + e.getMessage() + ")");
        }

        // Test Non-numeric Input Handling
        System.out.print("Test #7: Non-numeric Input ('abc') rejection   ... ");
        try {
            calculator.parseAndValidateInput("abc");
            System.out.println("[FAIL] (Expected IllegalArgumentException)");
            allPassed = false;
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] (Correctly rejected: " + e.getMessage() + ")");
        }

        // Test Blank Input Handling
        System.out.print("Test #8: Blank Input ('   ') rejection         ... ");
        try {
            calculator.parseAndValidateInput("   ");
            System.out.println("[FAIL] (Expected IllegalArgumentException)");
            allPassed = false;
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS] (Correctly rejected: " + e.getMessage() + ")");
        }

        System.out.println("==============================================================");
        if (allPassed) {
            System.out.println(">>> ALL VERIFICATION TESTS PASSED SUCCESSFULLY (8/8) <<<");
        } else {
            System.out.println(">>> SOME TESTS FAILED <<<");
        }
        System.out.println("==============================================================");

        return allPassed;
    }

    private static void runCliCalculation(String input) {
        BillCalculator calculator = new BillCalculator();
        try {
            double units = calculator.parseAndValidateInput(input);
            BillResult result = calculator.calculateBill(units);
            System.out.println(result.toDetailedSummary());
        } catch (IllegalArgumentException e) {
            System.err.println("Validation Error: " + e.getMessage());
        }
    }

    private static void printHelp() {
        System.out.println("BillWise — Electricity Bill Estimator");
        System.out.println("Usage:");
        System.out.println("  java com.billwise.Main            : Launch Swing Desktop Application");
        System.out.println("  java com.billwise.Main --test     : Run automated calculation test suite");
        System.out.println("  java com.billwise.Main <units>    : Calculate bill directly in terminal");
        System.out.println("  java com.billwise.Main --help     : Display this help message");
    }

    private static class TestCase {
        final double units;
        final double expectedBill;
        final String explanation;

        TestCase(double units, double expectedBill, String explanation) {
            this.units = units;
            this.expectedBill = expectedBill;
            this.explanation = explanation;
        }
    }
}
