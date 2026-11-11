
package com.cashmanagement.gui;

import com.cashmanagement.service.ReportService;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;
import java.util.LinkedHashMap;
import java.text.NumberFormat;
import java.util.Locale;

public class ReportsPanel extends JPanel {

    private final ReportService service =
        new ReportService();

    private JTextField monthField;

    private JLabel incomeLabel;
    private JLabel expenseLabel;
    private JLabel netLabel;
    private JLabel savingsLabel;

    private JPanel chartsPanel;

    private Map<String, BigDecimal> categoryData =
        new LinkedHashMap<>();

    private BigDecimal currentIncome = BigDecimal.ZERO;
    private BigDecimal currentExpenses = BigDecimal.ZERO;
    private BigDecimal currentNet = BigDecimal.ZERO;
    private BigDecimal currentSavings = BigDecimal.ZERO;

    public ReportsPanel() {

        setLayout(new BorderLayout(15, 15));

        setBackground(new Color(245, 247, 250));

        setBorder(BorderFactory.createEmptyBorder(
            20, 20, 20, 20
        ));

        createHeader();
        createSummaryCards();

        chartsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        chartsPanel.setBackground(new Color(245, 247, 250));

        add(chartsPanel, BorderLayout.CENTER);

        refreshReport();
    }

    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout(10, 10));

        header.setOpaque(false);

        JLabel title = new JLabel("Monthly Financial Reports");

        title.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        controls.setOpaque(false);

        monthField = new JTextField(
            LocalDate.now().toString().substring(0, 7), 8
        );

        JButton viewButton = new JButton("Generate Report");

        JButton exportButton = new JButton("Export CSV");

        viewButton.addActionListener(e -> refreshReport());

        exportButton.addActionListener(e -> exportReport());

        controls.add(new JLabel("Month (YYYY-MM):"));
        controls.add(monthField);
        controls.add(viewButton);
        controls.add(exportButton);

        header.add(title, BorderLayout.NORTH);
        header.add(controls, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);
    }

    private void createSummaryCards() {

        JPanel cards = new JPanel(new GridLayout(1, 4, 12, 12));

        cards.setOpaque(false);

        incomeLabel = createCard(cards, "TOTAL INCOME",
            new Color(22, 163, 74));

        expenseLabel = createCard(cards, "TOTAL EXPENSES",
            new Color(220, 38, 38));

        netLabel = createCard(cards, "NET CASH FLOW",
            new Color(37, 99, 235));

        savingsLabel = createCard(cards, "TOTAL SAVINGS",
            new Color(147, 51, 234));

        add(cards, BorderLayout.SOUTH);
    }

    private JLabel createCard(JPanel parent,
            String title, Color color) {

        JPanel card = new JPanel(new BorderLayout(5, 10));

        card.setBackground(Color.WHITE);

        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(225, 229, 235)
            ),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);

        titleLabel.setForeground(color);

        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueLabel = new JLabel("Rp0");

        valueLabel.setFont(new Font("Arial", Font.BOLD, 19));

        card.add(titleLabel, BorderLayout.NORTH);

        card.add(valueLabel, BorderLayout.CENTER);

        parent.add(card);

        return valueLabel;
    }

    public void refreshReport() {

        try {

            String month = monthField.getText().trim();

            if (!month.matches("\\d{4}-\\d{2}")) {
                throw new IllegalArgumentException(
                    "Month must use YYYY-MM format."
                );
            }

            int monthNumber = Integer.parseInt(
                month.substring(5)
            );

            if (monthNumber < 1 || monthNumber > 12) {
                throw new IllegalArgumentException(
                    "Invalid month."
                );
            }

            currentIncome = service.getIncome(month);

            currentExpenses = service.getExpenses(month);

            currentNet = currentIncome.subtract(currentExpenses);

            currentSavings = service.getTotalSavings();

            categoryData =
                service.getExpensesByCategory(month);

            incomeLabel.setText(format(currentIncome));

            expenseLabel.setText(format(currentExpenses));

            netLabel.setText(format(currentNet));

            savingsLabel.setText(format(currentSavings));

            chartsPanel.removeAll();

            chartsPanel.add(wrapChart(createMonthlyBarChart()));

            chartsPanel.add(wrapChart(createExpensePieChart()));

            chartsPanel.add(wrapChart(createTrendChart()));

            chartsPanel.add(wrapChart(createSavingsChart()));

            chartsPanel.revalidate();

            chartsPanel.repaint();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Report Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private JPanel wrapChart(JFreeChart chart) {

        JPanel panel = new JPanel(new BorderLayout());

        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createLineBorder(
            new Color(225, 229, 235)
        ));

        ChartPanel chartPanel = new ChartPanel(chart);

        chartPanel.setMouseWheelEnabled(true);

        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JFreeChart createMonthlyBarChart() {

        DefaultCategoryDataset dataset =
            new DefaultCategoryDataset();

        dataset.addValue(currentIncome.doubleValue(),
            "Income", "Selected Month");

        dataset.addValue(currentExpenses.doubleValue(),
            "Expenses", "Selected Month");

        return ChartFactory.createBarChart(
            "Income vs Expenses",
            "Period",
            "Amount (IDR)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
    }

    private JFreeChart createExpensePieChart() {

        DefaultPieDataset<String> dataset =
            new DefaultPieDataset<>();

        for (Map.Entry<String, BigDecimal> entry :
                categoryData.entrySet()) {

            if (entry.getValue().signum() > 0) {
                dataset.setValue(
                    entry.getKey(),
                    entry.getValue().doubleValue()
                );
            }
        }

        return ChartFactory.createPieChart(
            "Expenses by Category",
            dataset,
            true,
            true,
            false
        );
    }

    private JFreeChart createTrendChart() {

        DefaultCategoryDataset dataset =
            new DefaultCategoryDataset();

        Map<String, BigDecimal[]> trends;

        try {
            trends = service.getMonthlyTrends(6);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Map.Entry<String, BigDecimal[]> entry :
                trends.entrySet()) {

            dataset.addValue(
                entry.getValue()[0].doubleValue(),
                "Income",
                entry.getKey()
            );

            dataset.addValue(
                entry.getValue()[1].doubleValue(),
                "Expenses",
                entry.getKey()
            );
        }

        return ChartFactory.createLineChart(
            "Six-Month Financial Trend",
            "Month",
            "Amount (IDR)",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
    }

    private JFreeChart createSavingsChart() {

        DefaultPieDataset<String> dataset =
            new DefaultPieDataset<>();

        try {

            BigDecimal target =
                service.getTotalSavingsTarget();

            BigDecimal remaining =
                target.subtract(currentSavings);

            if (currentSavings.signum() > 0) {
                dataset.setValue(
                    "Saved",
                    currentSavings.doubleValue()
                );
            }

            if (remaining.signum() > 0) {
                dataset.setValue(
                    "Remaining",
                    remaining.doubleValue()
                );
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ChartFactory.createPieChart(
            "Savings Goal Progress",
            dataset,
            true,
            true,
            false
        );
    }

    private String format(BigDecimal amount) {

        NumberFormat formatter =
            NumberFormat.getCurrencyInstance(
                Locale.forLanguageTag("id-ID")
            );

        formatter.setMaximumFractionDigits(0);

        return formatter.format(amount);
    }

    private void exportReport() {

        JFileChooser chooser = new JFileChooser();

        chooser.setDialogTitle("Export Monthly Report");

        chooser.setSelectedFile(
            new java.io.File(
                "MonthlyReport-" + monthField.getText() + ".csv"
            )
        );

        if (chooser.showSaveDialog(this)
                != JFileChooser.APPROVE_OPTION) {
            return;
        }

        try {

            String month = monthField.getText().trim();

            StringBuilder csv = new StringBuilder();

            csv.append("PERSONAL CASH MANAGEMENT REPORT\n");
            csv.append("Month,").append(csvEscape(month)).append("\n\n");

            csv.append("Metric,Amount (IDR)\n");

            csv.append("Income,")
                .append(currentIncome.toPlainString()).append("\n");

            csv.append("Expenses,")
                .append(currentExpenses.toPlainString()).append("\n");

            csv.append("Net Cash Flow,")
                .append(currentNet.toPlainString()).append("\n");

            csv.append("Total Savings,")
                .append(currentSavings.toPlainString()).append("\n\n");

            csv.append("Expense Category,Amount (IDR)\n");

            for (Map.Entry<String, BigDecimal> entry :
                    categoryData.entrySet()) {

                csv.append(csvEscape(entry.getKey()))
                    .append(",")
                    .append(entry.getValue().toPlainString())
                    .append("\n");
            }

            Files.writeString(
                chooser.getSelectedFile().toPath(),
                csv.toString(),
                StandardCharsets.UTF_8
            );

            JOptionPane.showMessageDialog(this,
                "Report exported successfully!");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                this,
                e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private String csvEscape(String value) {

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
