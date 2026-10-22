package com.billwise;

/**
 * Represents a single progressive electricity tariff slab.
 * 
 * In progressive tariff systems, electricity consumption is divided into tiers
 * (slabs). Each slab defines a consumption range (minUnits to maxUnits) and a
 * specific rate per unit.
 * 
 * Example:
 *   Slab 1: 0 to 100 units @ ₹3.50/unit
 *   Slab 2: 100 to 300 units @ ₹5.00/unit
 *   Slab 3: Above 300 units @ ₹8.00/unit
 */
public class TariffSlab {

    private final String slabName;
    private final double minUnits;
    private final double maxUnits;
    private final double ratePerUnit;

    /**
     * Constructs a TariffSlab instance.
     *
     * @param slabName    Human-readable name or label (e.g., "Slab 1 (1 – 100 units)")
     * @param minUnits    Lower boundary of the slab in units (exclusive, except for base 0)
     * @param maxUnits    Upper boundary of the slab in units (inclusive, Double.POSITIVE_INFINITY for unbounded)
     * @param ratePerUnit Price charged per unit within this slab in Indian Rupees (₹)
     */
    public TariffSlab(String slabName, double minUnits, double maxUnits, double ratePerUnit) {
        if (slabName == null || slabName.trim().isEmpty()) {
            throw new IllegalArgumentException("Slab name cannot be null or empty.");
        }
        if (minUnits < 0) {
            throw new IllegalArgumentException("Minimum units cannot be negative: " + minUnits);
        }
        if (maxUnits <= minUnits) {
            throw new IllegalArgumentException("Maximum units (" + maxUnits + ") must be greater than minimum units (" + minUnits + ").");
        }
        if (ratePerUnit < 0) {
            throw new IllegalArgumentException("Rate per unit cannot be negative: " + ratePerUnit);
        }

        this.slabName = slabName;
        this.minUnits = minUnits;
        this.maxUnits = maxUnits;
        this.ratePerUnit = ratePerUnit;
    }

    /**
     * Calculates how many units out of total consumption fall specifically inside this slab.
     *
     * @param totalUnits Total electricity units consumed by the consumer.
     * @return The number of units falling inside this slab range (0.0 if consumption is below minUnits).
     */
    public double calculateUnitsInSlab(double totalUnits) {
        if (totalUnits <= minUnits) {
            return 0.0;
        }

        double applicableMax = Math.min(totalUnits, maxUnits);
        return applicableMax - minUnits;
    }

    /**
     * Calculates the charge in Rupees for the units consumed in this slab.
     *
     * @param totalUnits Total electricity units consumed.
     * @return Charge in INR (₹) = (units in slab) * (rate per unit).
     */
    public double calculateCharge(double totalUnits) {
        double unitsInSlab = calculateUnitsInSlab(totalUnits);
        return unitsInSlab * ratePerUnit;
    }

    // --- Getters ---

    public String getSlabName() {
        return slabName;
    }

    public double getMinUnits() {
        return minUnits;
    }

    public double getMaxUnits() {
        return maxUnits;
    }

    public double getRatePerUnit() {
        return ratePerUnit;
    }

    /**
     * Formatted string description of the slab boundaries.
     */
    public String getRangeDescription() {
        if (Double.isInfinite(maxUnits)) {
            return String.format("> %.0f units", minUnits);
        }
        return String.format("%.0f – %.0f units", minUnits + 1, maxUnits);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] @ ₹%.2f/unit", slabName, getRangeDescription(), ratePerUnit);
    }
}
