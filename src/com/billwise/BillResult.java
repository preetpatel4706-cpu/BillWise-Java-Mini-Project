package com.billwise;

import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the complete result of an electricity bill calculation.
 * 
 * Contains:
 * - Units consumed
 * - Itemized breakdown for each tariff slab
 * - Energy charges subtotal
 * - Fixed monthly charge
 * - Final estimated bill amount in Indian Rupees (₹)
 */
public class BillResult {

    /**
     * Represents the charge breakdown for a single tariff slab.
     */
    public static class SlabCharge {
        private final String slabName;
        private final String rangeDescription;
        private final double unitsInSlab;
        private final double ratePerUnit;
        private final double chargeAmount;

        public SlabCharge(String slabName, String rangeDescription, double unitsInSlab, double ratePerUnit, double chargeAmount) {
            this.slabName = slabName;
            this.rangeDescription = rangeDescription;
            this.unitsInSlab = unitsInSlab;
            this.ratePerUnit = ratePerUnit;
            this.chargeAmount = chargeAmount;
        }

        public String getSlabName() {
            return slabName;
        }

        public String getRangeDescription() {
            return rangeDescription;
        }

        public double getUnitsInSlab() {
            return unitsInSlab;
        }

        public double getRatePerUnit() {
            return ratePerUnit;
        }

        public double getChargeAmount() {
            return chargeAmount;
        }

        @Override
        public String toString() {
            return String.format("%-25s | %8.2f units @ ₹%-5.2f/unit | ₹%10.2f",
                    slabName, unitsInSlab, ratePerUnit, chargeAmount);
        }
    }

    private final double unitsConsumed;
    private final List<SlabCharge> slabBreakdown;
    private final double energyChargesSubtotal;
    private final double fixedCharge;
    private final double totalBill;

    public BillResult(double unitsConsumed, List<SlabCharge> slabBreakdown,
                      double energyChargesSubtotal, double fixedCharge, double totalBill) {
        this.unitsConsumed = unitsConsumed;
        this.slabBreakdown = Collections.unmodifiableList(slabBreakdown);
        this.energyChargesSubtotal = energyChargesSubtotal;
        this.fixedCharge = fixedCharge;
        this.totalBill = totalBill;
    }

    public double getUnitsConsumed() {
        return unitsConsumed;
    }

    public List<SlabCharge> getSlabBreakdown() {
        return slabBreakdown;
    }

    public double getEnergyChargesSubtotal() {
        return energyChargesSubtotal;
    }

    public double getFixedCharge() {
        return fixedCharge;
    }

    public double getTotalBill() {
        return totalBill;
    }

    /**
     * Returns a formatted Indian Rupee currency string, e.g., "₹1,200.00".
     */
    public static String formatCurrency(double amount) {
        return String.format("₹%,.2f", amount);
    }

    /**
     * Generates a neat text summary of the bill calculation.
     */
    public String toDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("====================================================\n");
        sb.append("              BILLWISE ESTIMATION SUMMARY           \n");
        sb.append("====================================================\n");
        sb.append(String.format("Units Consumed: %.2f kWh\n", unitsConsumed));
        sb.append("----------------------------------------------------\n");
        sb.append("SLAB BREAKDOWN:\n");
        for (SlabCharge slab : slabBreakdown) {
            sb.append(String.format("  * %-24s: %6.2f units x ₹%-4.2f = ₹%,.2f\n",
                    slab.getSlabName(), slab.getUnitsInSlab(), slab.getRatePerUnit(), slab.getChargeAmount()));
        }
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("Energy Charges Subtotal : %s\n", formatCurrency(energyChargesSubtotal)));
        sb.append(String.format("Fixed Monthly Charge    : %s\n", formatCurrency(fixedCharge)));
        sb.append("----------------------------------------------------\n");
        sb.append(String.format("FINAL ESTIMATED BILL    : %s\n", formatCurrency(totalBill)));
        sb.append("====================================================\n");
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("BillResult[Units=%.2f, Total=%s]", unitsConsumed, formatCurrency(totalBill));
    }
}
