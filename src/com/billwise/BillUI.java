package com.billwise;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Modern, responsive Graphical User Interface for BillWise — Electricity Bill Estimator.
 * 
 * Architecture & Responsive Design:
 * - Dynamic centering container: Prevents controls and table columns from stretching
 *   excessively when the window is maximized on wide (1080p+) monitors.
 * - Perfectly proportioned for normal windowed mode (~750x660) without clipping or scrollbars.
 * - Anti-aliased high-contrast custom buttons (Calculate Bill & Reset) guaranteed visible.
 * - Cohesive pill-shaped quick preset buttons.
 * - High-impact itemized table with distinct styling for Subtotal and TOTAL ESTIMATED BILL rows.
 * - Preserves all existing business logic, validation, and test harness hooks.
 */
public class BillUI extends JFrame {

    private final BillCalculator calculator;

    // Interactive Components
    private JTextField txtUnits;
    private JButton btnCalculate;
    private JButton btnReset;
    private JLabel lblStatusMessage;
    private JLabel lblTotalBillValue;
    private JLabel lblTotalSubtitle;
    private JTable breakdownTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;

    // Responsive Layout Containers
    private JPanel contentCard;
    private GridBagConstraints contentCardGbc;
    private JPanel centerWrapper;

    // Theme Color Palette
    private static final Color COLOR_PRIMARY_DARK = new Color(24, 43, 73);      // Deep Midnight Navy
    private static final Color COLOR_PRIMARY_HEADER = new Color(30, 58, 138);    // Classic Utility Navy
    private static final Color COLOR_ACCENT_BLUE = new Color(37, 99, 235);      // Vibrant Action Blue
    private static final Color COLOR_ACCENT_BLUE_HOVER = new Color(29, 78, 216);
    private static final Color COLOR_ACCENT_BLUE_PRESSED = new Color(30, 64, 175);

    private static final Color COLOR_RESET_RED = new Color(220, 38, 38);        // Vibrant Crimson
    private static final Color COLOR_RESET_RED_HOVER = new Color(185, 28, 28);
    private static final Color COLOR_RESET_RED_PRESSED = new Color(153, 27, 27);

    private static final Color COLOR_BG = new Color(241, 245, 249);             // Clean Slate Background
    private static final Color COLOR_CARD_BG = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(226, 232, 240);         // Slate 200
    private static final Color COLOR_TEXT_MAIN = new Color(15, 23, 42);         // Slate 900
    private static final Color COLOR_TEXT_MUTED = new Color(71, 85, 105);       // Slate 600
    private static final Color COLOR_ESTIMATE_BG = new Color(238, 246, 255);    // Soft Ice Blue
    private static final Color COLOR_ESTIMATE_BORDER = new Color(191, 219, 254);

    public BillUI() {
        this(new BillCalculator());
    }

    public BillUI(BillCalculator calculator) {
        super("BillWise — Electricity Bill Estimator");
        this.calculator = calculator;

        initLookAndFeel();
        initComponents();
        setupResponsiveLayout();
        registerEventHandlers();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 660);
        setMinimumSize(new Dimension(640, 580));
        setLocationRelativeTo(null); // Center on screen
    }

    private void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Graceful fallback
        }
    }

    /**
     * Initializes all UI components with custom modern styling.
     */
    private void initComponents() {
        // 1. Units Input Field
        txtUnits = new JTextField(9);
        txtUnits.setFont(new Font("Segoe UI", Font.BOLD, 17));
        txtUnits.setForeground(COLOR_TEXT_MAIN);
        txtUnits.setBackground(Color.WHITE);
        txtUnits.setHorizontalAlignment(JTextField.CENTER);
        txtUnits.setToolTipText("Enter electricity consumption in units (e.g., 0, 50, 100, 250, 400)");
        txtUnits.setBorder(new CompoundBorder(
                new LineBorder(new Color(203, 213, 225), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        // 2. High-Visibility Custom Action Buttons
        btnCalculate = new ModernButton(
                "Calculate Bill",
                COLOR_ACCENT_BLUE,
                COLOR_ACCENT_BLUE_HOVER,
                COLOR_ACCENT_BLUE_PRESSED,
                Color.WHITE,
                new Dimension(170, 40),
                14,
                8,
                null
        );
        btnCalculate.setToolTipText("Calculate progressive electricity charges (or press Enter)");

        btnReset = new ModernButton(
                "Reset",
                COLOR_RESET_RED,
                COLOR_RESET_RED_HOVER,
                COLOR_RESET_RED_PRESSED,
                Color.WHITE,
                new Dimension(115, 40),
                14,
                8,
                null
        );
        btnReset.setToolTipText("Clear all inputs and reset bill calculation");

        // 3. Status / Inline Validation Message
        lblStatusMessage = new JLabel(" ");
        lblStatusMessage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatusMessage.setForeground(COLOR_RESET_RED);
        lblStatusMessage.setHorizontalAlignment(SwingConstants.CENTER);
        lblStatusMessage.setPreferredSize(new Dimension(0, 18));

        // 4. Prominent Estimated Bill Value Display
        lblTotalBillValue = new JLabel("₹0.00", SwingConstants.CENTER);
        lblTotalBillValue.setFont(new Font("Segoe UI", Font.BOLD, 30));
        lblTotalBillValue.setForeground(COLOR_PRIMARY_HEADER);

        lblTotalSubtitle = new JLabel("Ready for calculation — enter units above", SwingConstants.CENTER);
        lblTotalSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTotalSubtitle.setForeground(COLOR_TEXT_MUTED);

        // 5. Itemized Breakdown Table
        String[] columnNames = {"Tariff Slab / Component", "Consumption", "Rate (₹/Unit)", "Amount (₹)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        breakdownTable = new JTable(tableModel);
        breakdownTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        breakdownTable.setRowHeight(31);
        breakdownTable.setShowGrid(true);
        breakdownTable.setGridColor(new Color(241, 245, 249));
        breakdownTable.setFillsViewportHeight(true);
        breakdownTable.setSelectionBackground(new Color(224, 231, 255));
        breakdownTable.setSelectionForeground(COLOR_TEXT_MAIN);

        // Custom Table Header
        JTableHeader tableHeader = breakdownTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(241, 245, 249));
        tableHeader.setForeground(COLOR_TEXT_MAIN);
        tableHeader.setPreferredSize(new Dimension(0, 34));
        tableHeader.setReorderingAllowed(false);

        // Column Widths to guarantee no label truncation
        breakdownTable.getColumnModel().getColumn(0).setPreferredWidth(240);
        breakdownTable.getColumnModel().getColumn(0).setMinWidth(200);
        breakdownTable.getColumnModel().getColumn(1).setPreferredWidth(110);
        breakdownTable.getColumnModel().getColumn(2).setPreferredWidth(105);
        breakdownTable.getColumnModel().getColumn(3).setPreferredWidth(115);

        // Custom Cell Renderers
        setupTableRenderers();

        // Load empty table state
        loadEmptyTableGuide();
    }

    /**
     * Builds the responsive layout structure with horizontal centering on wide monitors.
     */
    private void setupResponsiveLayout() {
        JPanel rootPanel = new JPanel(new BorderLayout(0, 0));
        rootPanel.setBackground(COLOR_BG);

        // 1. Full-Width Top Header
        rootPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Responsive Center Wrapper (GridBagLayout centers contentCard horizontally)
        centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);
        centerWrapper.setBorder(new EmptyBorder(8, 16, 6, 16));

        contentCard = new JPanel();
        contentCard.setLayout(new BoxLayout(contentCard, BoxLayout.Y_AXIS));
        contentCard.setOpaque(false);

        // A. Compact Input Card
        contentCard.add(createInputCard());
        contentCard.add(Box.createVerticalStrut(7));

        // B. Quick Presets Bar
        contentCard.add(createPresetsBar());
        contentCard.add(Box.createVerticalStrut(7));

        // C. Results Card (Total Callout Box + Breakdown Table)
        contentCard.add(createResultsCard());
        contentCard.add(Box.createVerticalStrut(7));

        // D. Footer (Tariff rules + Academic disclaimer)
        contentCard.add(createFooterPanel());

        contentCardGbc = new GridBagConstraints();
        contentCardGbc.gridx = 0;
        contentCardGbc.gridy = 0;
        contentCardGbc.weighty = 1.0;
        contentCardGbc.anchor = GridBagConstraints.CENTER;
        contentCardGbc.fill = GridBagConstraints.HORIZONTAL;
        contentCardGbc.weightx = 1.0;

        centerWrapper.add(contentCard, contentCardGbc);

        // Outer ScrollPane as graceful fallback for small laptop screens
        JScrollPane outerScrollPane = new JScrollPane(centerWrapper);
        outerScrollPane.setBorder(null);
        outerScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        outerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        rootPanel.add(outerScrollPane, BorderLayout.CENTER);
        setContentPane(rootPanel);

        // Responsive Resizing Handler
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateResponsiveConstraints();
            }
        });
    }

    /**
     * Dynamically adjusts constraints based on window width:
     * - On wide/maximized screens: Constrains card width to 800px and centers it.
     * - In normal windowed mode: Fills available width naturally.
     */
    public void updateResponsiveConstraints() {
        int width = getWidth();
        int height = getHeight();

        GridBagLayout gbl = (GridBagLayout) centerWrapper.getLayout();
        if (width > 860) {
            contentCardGbc.fill = GridBagConstraints.NONE;
            contentCardGbc.weightx = 0.0;
            int cardW = Math.min(800, width - 64);
            int cardH = (height > 720) ? 550 : 520;
            contentCard.setPreferredSize(new Dimension(cardW, cardH));
            contentCard.setMaximumSize(new Dimension(cardW, cardH));
            tableScrollPane.setPreferredSize(new Dimension(cardW - 32, (height > 720) ? 245 : 215));
            gbl.setConstraints(contentCard, contentCardGbc);
        } else {
            contentCardGbc.fill = GridBagConstraints.HORIZONTAL;
            contentCardGbc.weightx = 1.0;
            contentCard.setPreferredSize(null);
            contentCard.setMaximumSize(null);
            tableScrollPane.setPreferredSize(new Dimension(520, 215));
            gbl.setConstraints(contentCard, contentCardGbc);
        }

        centerWrapper.revalidate();
        centerWrapper.repaint();
    }

    /**
     * Header banner with crisp typography and project subtitle.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_PRIMARY_HEADER);
        headerPanel.setBorder(new EmptyBorder(11, 24, 11, 24));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("BillWise — Electricity Bill Estimator");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Progressive Slab-Based Tariff Calculator | B.Tech Academic Project");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(224, 231, 255));

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(lblSubtitle);

        headerPanel.add(textPanel, BorderLayout.WEST);
        return headerPanel;
    }

    /**
     * Compact Input Card featuring the consumption input field,
     * high-contrast action buttons, and inline validation message.
     */
    private JPanel createInputCard() {
        JPanel card = new JPanel(new BorderLayout(0, 3));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(9, 16, 7, 16)
        ));

        // Top Row: Label, Centered Input Field, and Unit Badge
        JPanel inputRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 2));
        inputRow.setOpaque(false);

        JLabel lblUnits = new JLabel("Units Consumed:");
        lblUnits.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUnits.setForeground(COLOR_TEXT_MAIN);
        inputRow.add(lblUnits);

        inputRow.add(txtUnits);

        JLabel lblUnitBadge = new JLabel("kWh");
        lblUnitBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUnitBadge.setForeground(COLOR_TEXT_MUTED);
        inputRow.add(lblUnitBadge);

        card.add(inputRow, BorderLayout.NORTH);

        // Center Area: Action Buttons Bar with comfortable spacing
        JPanel actionBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 3));
        actionBar.setOpaque(false);
        actionBar.add(btnCalculate);
        actionBar.add(btnReset);

        card.add(actionBar, BorderLayout.CENTER);

        // Bottom Area: Inline Status/Validation message
        card.add(lblStatusMessage, BorderLayout.SOUTH);

        return card;
    }

    /**
     * Modernized Quick-Sample buttons matching the action button design system
     * while remaining visually distinct secondary actions.
     */
    private JPanel createPresetsBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 0));
        bar.setOpaque(false);

        JLabel lblQuick = new JLabel("Quick Presets:");
        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQuick.setForeground(COLOR_TEXT_MUTED);
        bar.add(lblQuick);

        int[] presets = {0, 50, 100, 250, 400};
        for (int units : presets) {
            ModernButton chip = new ModernButton(
                    units + " Units",
                    new Color(241, 245, 249),              // Soft Slate 100
                    new Color(224, 231, 255),              // Light Blue 100 hover
                    new Color(199, 210, 254),              // Blue 200 pressed
                    COLOR_PRIMARY_HEADER,                  // Utility Navy text
                    new Dimension(92, 28),
                    11,
                    14,                                    // Rounded pill radius
                    new Color(203, 213, 225)               // Crisp border
            );
            chip.setToolTipText("Estimate bill for " + units + " units");
            chip.addActionListener(e -> {
                txtUnits.setText(String.valueOf(units));
                performCalculation();
            });
            bar.add(chip);
        }

        return bar;
    }

    /**
     * Results Card containing the Total Amount Callout and Itemized Table Breakdown.
     */
    private JPanel createResultsCard() {
        JPanel card = new JPanel(new BorderLayout(0, 6));
        card.setBackground(COLOR_CARD_BG);
        card.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));

        // Prominent Total Estimated Bill Callout Box (Optimized height)
        JPanel totalBox = new JPanel();
        totalBox.setLayout(new BoxLayout(totalBox, BoxLayout.Y_AXIS));
        totalBox.setBackground(COLOR_ESTIMATE_BG);
        totalBox.setBorder(new CompoundBorder(
                new LineBorder(COLOR_ESTIMATE_BORDER, 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));

        JLabel lblTotalTitle = new JLabel("TOTAL ESTIMATED BILL", SwingConstants.CENTER);
        lblTotalTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTotalTitle.setForeground(COLOR_PRIMARY_HEADER);
        lblTotalTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblTotalBillValue.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTotalSubtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        totalBox.add(lblTotalTitle);
        totalBox.add(Box.createVerticalStrut(1));
        totalBox.add(lblTotalBillValue);
        totalBox.add(Box.createVerticalStrut(1));
        totalBox.add(lblTotalSubtitle);

        card.add(totalBox, BorderLayout.NORTH);

        // Detailed Slab Breakdown Table
        tableScrollPane = new JScrollPane(breakdownTable);
        tableScrollPane.setPreferredSize(new Dimension(520, 215));
        tableScrollPane.setBorder(new LineBorder(COLOR_BORDER, 1));
        card.add(tableScrollPane, BorderLayout.CENTER);

        return card;
    }

    /**
     * Footer containing academic disclaimer and tariff configuration legend.
     */
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel();
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(3, 4, 3, 4));

        JLabel lblTariffRules = new JLabel(
                "Tariff Rules: Fixed Charge: ₹100 | First 100 units @ ₹3.50 | 101–300 units @ ₹5.00 | >300 units @ ₹8.00");
        lblTariffRules.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTariffRules.setForeground(COLOR_TEXT_MUTED);
        lblTariffRules.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDisclaimer = new JLabel(
                "Disclaimer: Rates used are illustrative for academic demonstration and do not represent official utility tariffs.");
        lblDisclaimer.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblDisclaimer.setForeground(new Color(180, 83, 9)); // Amber-700
        lblDisclaimer.setAlignmentX(Component.CENTER_ALIGNMENT);

        footer.add(lblTariffRules);
        footer.add(Box.createVerticalStrut(2));
        footer.add(lblDisclaimer);
        return footer;
    }

    /**
     * Configures custom cell renderers for clean tabular formatting.
     */
    private void setupTableRenderers() {
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                applyRowStyling(this, row, isSelected);
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                applyRowStyling(this, row, isSelected);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 4, 0, 10));
                return c;
            }
        };

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                applyRowStyling(this, row, isSelected);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 10, 0, 4));
                return c;
            }
        };

        breakdownTable.getColumnModel().getColumn(0).setCellRenderer(leftRenderer);
        breakdownTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        breakdownTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        breakdownTable.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
    }

    /**
     * Applies distinct visual hierarchy to rows:
     * - Subtotal & Fixed Fee: Distinct slate/gray background and bold styling
     * - TOTAL ESTIMATED BILL: Distinct soft blue background with prominent Navy bold typography
     * - Standard Slabs: Crisp alternating rows
     */
    private void applyRowStyling(JLabel label, int row, boolean isSelected) {
        if (isSelected) {
            return;
        }

        int rowCount = tableModel.getRowCount();
        if (rowCount >= 5 && row == rowCount - 1) {
            // TOTAL ESTIMATED BILL row
            label.setBackground(new Color(219, 234, 254)); // Soft Vibrant Blue 100
            label.setForeground(COLOR_PRIMARY_HEADER);
            label.setFont(label.getFont().deriveFont(Font.BOLD, 13.5f));
        } else if (rowCount >= 5 && row == rowCount - 3) {
            // Energy charges subtotal row
            label.setBackground(new Color(237, 242, 247)); // Distinct Cool Slate 150
            label.setForeground(new Color(30, 41, 59));     // Slate 800
            label.setFont(label.getFont().deriveFont(Font.BOLD, 12.5f));
        } else if (rowCount >= 5 && row == rowCount - 2) {
            // Fixed Monthly Charge row
            label.setBackground(new Color(248, 250, 252)); // Slate 50
            label.setForeground(new Color(51, 65, 85));     // Slate 700
            label.setFont(label.getFont().deriveFont(Font.BOLD, 12.5f));
        } else {
            // Standard slab rows with subtle alternation
            if (row % 2 == 0) {
                label.setBackground(Color.WHITE);
            } else {
                label.setBackground(new Color(248, 250, 252));
            }
            label.setForeground(COLOR_TEXT_MAIN);
            label.setFont(label.getFont().deriveFont(Font.PLAIN, 13f));
        }
    }

    /**
     * Registers button listeners, enter key shortcuts, and input listeners.
     */
    private void registerEventHandlers() {
        btnCalculate.addActionListener(e -> performCalculation());
        btnReset.addActionListener(e -> resetForm());

        // Pressing Enter in the textfield automatically calculates
        txtUnits.addActionListener(e -> performCalculation());

        // Clear error label when user begins typing again
        txtUnits.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                lblStatusMessage.setText(" ");
            }
        });
    }

    /**
     * Performs calculation, validates input, updates UI, and displays friendly errors on failure.
     */
    public void performCalculation() {
        lblStatusMessage.setText(" ");
        String rawInput = txtUnits.getText();

        try {
            double units = calculator.parseAndValidateInput(rawInput);
            BillResult result = calculator.calculateBill(units);
            displayResult(result);
        } catch (IllegalArgumentException ex) {
            handleValidationError(ex.getMessage());
        } catch (Exception ex) {
            handleValidationError("Unexpected error occurred: " + ex.getMessage());
        }
    }

    /**
     * Updates the UI components with calculated results.
     */
    private void displayResult(BillResult result) {
        lblTotalBillValue.setText(BillResult.formatCurrency(result.getTotalBill()));
        lblTotalSubtitle.setText(String.format("Energy Charges: %s  |  Fixed Monthly Fee: %s",
                BillResult.formatCurrency(result.getEnergyChargesSubtotal()),
                BillResult.formatCurrency(result.getFixedCharge())));

        lblStatusMessage.setForeground(new Color(22, 101, 52)); // Dark Green
        lblStatusMessage.setText(String.format("Calculated for %.2f kWh successfully.", result.getUnitsConsumed()));

        // Populate Table Breakdown
        tableModel.setRowCount(0);

        for (BillResult.SlabCharge slab : result.getSlabBreakdown()) {
            tableModel.addRow(new Object[]{
                    slab.getSlabName(),
                    String.format("%.2f units", slab.getUnitsInSlab()),
                    String.format("₹%.2f", slab.getRatePerUnit()),
                    String.format("₹%,.2f", slab.getChargeAmount())
            });
        }

        // Subtotals and Fixed Charge Rows
        tableModel.addRow(new Object[]{
                "Energy Charges Subtotal",
                "—",
                "—",
                String.format("₹%,.2f", result.getEnergyChargesSubtotal())
        });

        tableModel.addRow(new Object[]{
                "Fixed Monthly Charge",
                "—",
                "—",
                String.format("₹%,.2f", result.getFixedCharge())
        });

        tableModel.addRow(new Object[]{
                "TOTAL ESTIMATED BILL",
                "—",
                "—",
                String.format("₹%,.2f", result.getTotalBill())
        });
    }

    /**
     * Displays a friendly error message inline and via modal warning dialog.
     */
    private void handleValidationError(String errorMessage) {
        lblStatusMessage.setForeground(COLOR_RESET_RED);
        lblStatusMessage.setText(errorMessage);

        JOptionPane.showMessageDialog(
                this,
                errorMessage,
                "Input Validation Error",
                JOptionPane.WARNING_MESSAGE
        );

        txtUnits.requestFocusInWindow();
        txtUnits.selectAll();
    }

    /**
     * Clears all inputs and resets table and results to the clean starting state.
     */
    public void resetForm() {
        txtUnits.setText("");
        lblTotalBillValue.setText("₹0.00");
        lblTotalSubtitle.setText("Ready for calculation — enter units above");
        lblStatusMessage.setText(" ");
        loadEmptyTableGuide();
        txtUnits.requestFocusInWindow();
    }

    /**
     * Populates placeholder rows when no calculation has taken place yet.
     */
    private void loadEmptyTableGuide() {
        tableModel.setRowCount(0);
        for (TariffSlab slab : calculator.getSlabs()) {
            tableModel.addRow(new Object[]{
                    slab.getSlabName(),
                    "0.00 units",
                    String.format("₹%.2f", slab.getRatePerUnit()),
                    "₹0.00"
            });
        }
        tableModel.addRow(new Object[]{"Energy Charges Subtotal", "—", "—", "₹0.00"});
        tableModel.addRow(new Object[]{"Fixed Monthly Charge", "Standard", "—", String.format("₹%,.2f", calculator.getFixedMonthlyCharge())});
        tableModel.addRow(new Object[]{"TOTAL ESTIMATED BILL", "—", "—", "₹0.00"});
    }

    // --- Component Getters for Testing & External Hooks ---

    public JTextField getTxtUnits() {
        return txtUnits;
    }

    public JButton getBtnCalculate() {
        return btnCalculate;
    }

    public JButton getBtnReset() {
        return btnReset;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JLabel getLblTotalBillValue() {
        return lblTotalBillValue;
    }

    /**
     * Custom JButton with guaranteed anti-aliased solid background painting,
     * smooth hover & pressed transitions, customizable border and corner radius.
     */
    private static class ModernButton extends JButton {
        private final Color normalColor;
        private final Color hoverColor;
        private final Color pressedColor;
        private final Color textColor;
        private final int cornerRadius;
        private final Color customBorderColor;

        public ModernButton(String text, Color normalColor, Color hoverColor, Color pressedColor,
                            Color textColor, Dimension size, int fontSize, int cornerRadius, Color customBorderColor) {
            super(text);
            this.normalColor = normalColor;
            this.hoverColor = hoverColor;
            this.pressedColor = pressedColor;
            this.textColor = textColor;
            this.cornerRadius = cornerRadius;
            this.customBorderColor = customBorderColor;

            setFont(new Font("Segoe UI", Font.BOLD, fontSize));
            setForeground(textColor);
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentBg;
            if (getModel().isPressed()) {
                currentBg = pressedColor;
            } else if (getModel().isRollover()) {
                currentBg = hoverColor;
            } else {
                currentBg = normalColor;
            }

            // Draw smooth rounded rectangle background
            g2.setColor(currentBg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            // Border
            if (customBorderColor != null) {
                g2.setColor(customBorderColor);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            } else {
                g2.setColor(new Color(0, 0, 0, 25));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }

            g2.dispose();

            // Paint label text with guaranteed high contrast
            setForeground(textColor);
            super.paintComponent(g);
        }
    }
}
