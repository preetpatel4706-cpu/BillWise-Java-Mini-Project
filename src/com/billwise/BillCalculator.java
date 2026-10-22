package com.billwise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Core business logic engine for calculating progressive electricity bills.
 * 
 * Illustrative Tariff Model:
 *  - Fixed Monthly Charge : ₹100.00
 *  - Slab 1 (1–100 units) : ₹3.50 per unit
 *  - Slab 2 (101–300 units): ₹5.00 per unit
 *  - Slab 3 (Above 300)   : ₹8.00 per unit
 */
public class BillCalculator {

    public static final double DEFAULT_FIXED_CHARGE = 100.00;

    private final double fixedMonthlyCharge;
    private final List<TariffSlab> slabs;

    /**
     * Default constructor initializing the standard illustrative tariff slabs:
     * - Fixed charge: ₹100.00
     * - Slab 1: 0 – 100 units @ ₹3.50
     * - Slab 2: 100 – 300 units @ ₹5.00
     * - Slab 3: Above 300 units @ ₹8.00
     */
    public BillCalculator() {
        this.fixedMonthlyCharge = DEFAULT_FIXED_CHARGE;
        List<TariffSlab> defaultSlabs = new ArrayList<>();
        defaultSlabs.add(new TariffSlab("Slab 1 (1 – 100 units)", 0.0, 100.0, 3.50));
        defaultSlabs.add(new TariffSlab("Slab 2 (101 – 300 units)", 100.0, 300.0, 5.00));
        defaultSlabs.add(new TariffSlab("Slab 3 (Above 300 units)", 300.0, Double.POSITIVE_INFINITY, 8.00));
        this.slabs = Collections.unmodifiableList(defaultSlabs);
    }

    /**
     * Parameterized constructor allowing configurable slabs and fixed charge.
     */
    public BillCalculator(double fixedMonthlyCharge, List<TariffSlab> slabs) {
        if (fixedMonthlyCharge < 0) {
            throw new IllegalArgumentException("Fixed charge cannot be negative: " + fixedMonthlyCharge);
        }
        if (slabs == null || slabs.isEmpty()) {
            throw new IllegalArgumentException("Tariff slabs list cannot be null or empty.");
        }
        this.fixedMonthlyCharge = fixedMonthlyCharge;
        this.slabs = Collections.unmodifiableList(new ArrayList<>(slabs));
    }

    /**
     * Parses and strictly validates a string representation of units consumed.
     * Rejects empty, blank, non-numeric, or negative inputs with user-friendly messages.
     * Accepts zero units as valid.
     *
     * @param rawInput String entered by the user.
     * @return Validated double value of units consumed.
     * @throws IllegalArgumentException if the input is blank, non-numeric, or negative.
     */
    public double parseAndValidateInput(String rawInput) {
        if (rawInput == null || rawInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter the number of units consumed.");
        }

        String cleaned = rawInput.trim();
        double units;
        try {
            units = Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Invalid input '" + cleaned + "'. Please enter numeric digits only (e.g., 0, 50, 250).");
        }

        validateUnits(units);
        return units;
    }

    /**
     * Validates that the units numeric value is non-negative and finite.
     *
     * @param units Consumed units.
     * @throws IllegalArgumentException if negative or non-finite.
     */
    public void validateUnits(double units) {
        if (Double.isNaN(units) || Double.isInfinite(units)) {
            throw new IllegalArgumentException("Units consumed must be a finite numerical value.");
        }
        if (units < 0.0) {
            throw new IllegalArgumentException(
                    "Units consumed cannot be negative (" + units + "). Please enter 0 or a positive value.");
        }
    }

    /**
     * Calculates the estimated electricity bill based on progressive tariff slabs.
     *
     * @param units Total units consumed (must be >= 0).
     * @return BillResult object containing breakdown and final amounts.
     */
    public BillResult calculateBill(double units) {
        validateUnits(units);

        List<BillResult.SlabCharge> breakdown = new ArrayList<>();
        double energyChargesSubtotal = 0.0;

        for (TariffSlab slab : slabs) {
            double unitsInSlab = slab.calculateUnitsInSlab(units);
            double charge = unitsInSlab * slab.getRatePerUnit();

            breakdown.add(new BillResult.SlabCharge(
                    slab.getSlabName(),
                    slab.getRangeDescription(),
                    unitsInSlab,
                    slab.getRatePerUnit(),
                    charge
            ));

            energyChargesSubtotal += charge;
        }

        double totalBill = energyChargesSubtotal + fixedMonthlyCharge;

        return new BillResult(
                units,
                breakdown,
                energyChargesSubtotal,
                fixedMonthlyCharge,
                totalBill
        );
    }

    public double getFixedMonthlyCharge() {
        return fixedMonthlyCharge;
    }

    public List<TariffSlab> getSlabs() {
        return slabs;
    }
}
