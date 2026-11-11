
package com.cashmanagement.gui;

import com.cashmanagement.dao.BudgetDAO;
import com.cashmanagement.dao.TransactionDAO;
import com.cashmanagement.model.Budget;
import com.cashmanagement.model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Locale;

public class BudgetPanel extends JPanel {

    private final BudgetDAO budgetDAO = new BudgetDAO();

    private final TransactionDAO transactionDAO =
        new TransactionDAO();

    private JTextField monthField;
    private JTextField limitField;

    private JComboBox<String> categoryBox;

    private JTable table;
    private DefaultTableModel tableModel;

    private int selectedId = -1;

    public BudgetPanel() {

        setLayout(new BorderLayout(15, 15));

        setBackground(new Color(245, 247, 250));

        setBorder(BorderFactory.createEmptyBorder(
            20, 20, 20, 20
        ));

        createForm();
        createTable();

        loadBudgets();
    }

    // =========================
    // FORM
    // =========================

    private void createForm() {

        JPanel form = new JPanel(new GridLayout(2, 4, 10, 10));

        form.setBackground(Color.WHITE);

        form.setBorder(BorderFactory.createTitledBorder(
            "Monthly Budget Management"
        ));

        monthField = new JTextField(
            LocalDate.now().toString().substring(0, 7)
        );

        categoryBox = new JComboBox<>(new String[]{
            "Food",
            "Transport",
            "Shopping",
            "Bills",
            "Entertainment",
            "Health",
            "Education",
            "Other"
        });

        limitField = new JTextField();

        JButton addButton = createButton(
            "Add Budget", new Color(22, 163, 74)
        );

        JButton updateButton = createButton(
            "Update", new Color(37, 99, 235)
        );

        JButton deleteButton = createButton(
            "Delete", new Color(220, 38, 38)
        );

        JButton clearButton = createButton(
            "Clear", new Color(100, 116, 139)
        );

        form.add(new JLabel("Month (YYYY-MM)"));
        form.add(monthField);

        form.add(new JLabel("Category"));
        form.add(categoryBox);

        form.add(new JLabel("Budget Limit (IDR)"));
        form.add(limitField);

        form.add(addButton);
        form.add(updateButton);

        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        buttons.setOpaque(false);

        buttons.add(deleteButton);
        buttons.add(clearButton);

        JPanel container = new JPanel(new BorderLayout());

        container.setOpaque(false);

        container.add(form, BorderLayout.CENTER);

        container.add(buttons, BorderLayout.SOUTH);

        add(container, BorderLayout.NORTH);

        addButton.addActionListener(e -> addBudget());

        updateButton.addActionListener(e -> updateBudget());

        deleteButton.addActionListener(e -> deleteBudget());

        clearButton.addActionListener(e -> clearForm());
    }

    // =========================
    // TABLE
    // =========================

    private void createTable() {

        tableModel = new DefaultTableModel(
            new String[]{
                "ID",
                "Month",
                "Category",
                "Budget Limit",
                "Spent",
                "Remaining",
                "Status"
            }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(tableModel);

        table.setRowHeight(32);

        table.setSelectionMode(
            ListSelectionModel.SINGLE_SELECTION
        );

        table.setAutoCreateRowSorter(true);

        table.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()
                    && table.getSelectedRow() >= 0) {

                fillForm();
            }
        });

        JLabel title = new JLabel("Budget Overview");

        title.setFont(new Font("Segoe UI", Font.BOLD, 20));

        JPanel container = new JPanel(new BorderLayout(5, 10));

        container.setOpaque(false);

        container.add(title, BorderLayout.NORTH);

        container.add(new JScrollPane(table), BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);
    }

    // =========================
    // VALIDATION
    // =========================

    private BigDecimal validateAmount(String value) {

        try {

            BigDecimal amount = new BigDecimal(value.trim());

            if (amount.compareTo(BigDecimal.ZERO) <= 0) {

                throw new IllegalArgumentException(
                    "Budget must be greater than zero."
                );
            }

            return amount;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                "Please enter a valid budget amount."
            );
        }
    }

    private String validateMonth(String value) {

        try {

            return YearMonth.parse(value.trim()).toString();

        } catch (Exception e) {

            throw new IllegalArgumentException(
                "Month must use YYYY-MM format."
            );
        }
    }

    private Budget getFormBudget(int id) {

        String month = validateMonth(monthField.getText());

        String category =
            categoryBox.getSelectedItem().toString();

        BigDecimal limit = validateAmount(
            limitField.getText()
        );

        return new Budget(id, month, category, limit);
    }

    // =========================
    // DUPLICATE CHECK
    // =========================

    private void checkDuplicate(Budget budget)
            throws Exception {

        for (Budget existing : budgetDAO.findAll()) {

            if (existing.getId() != selectedId
                    && existing.getMonth().equals(budget.getMonth())
                    && existing.getCategory().equalsIgnoreCase(
                        budget.getCategory())) {

                throw new IllegalArgumentException(
                    "A budget already exists for this category and month."
                );
            }
        }
    }

    // =========================
    // ADD
    // =========================

    private void addBudget() {

        try {

            Budget budget = getFormBudget(
                budgetDAO.nextId()
            );

            // For a new budget, no existing ID should be ignored.
            for (Budget existing : budgetDAO.findAll()) {

                if (existing.getMonth().equals(budget.getMonth())
                        && existing.getCategory().equalsIgnoreCase(
                            budget.getCategory())) {

                    throw new IllegalArgumentException(
                        "A budget already exists for this category and month."
                    );
                }
            }

            budgetDAO.add(budget);

            JOptionPane.showMessageDialog(
                this,
                "Budget created successfully!"
            );

            clearForm();
            loadBudgets();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // UPDATE
    // =========================

    private void updateBudget() {

        if (selectedId == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a budget first."
            );

            return;
        }

        try {

            Budget budget = getFormBudget(selectedId);

            checkDuplicate(budget);

            budgetDAO.update(budget);

            JOptionPane.showMessageDialog(
                this,
                "Budget updated successfully!"
            );

            clearForm();
            loadBudgets();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // DELETE
    // =========================

    private void deleteBudget() {

        if (selectedId == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a budget first."
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
            this,
            "Delete this budget?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            budgetDAO.deleteById(selectedId);

            JOptionPane.showMessageDialog(
                this,
                "Budget deleted successfully!"
            );

            clearForm();
            loadBudgets();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // CALCULATE EXPENSES
    // =========================

    private BigDecimal calculateSpent(
            String month, String category) throws Exception {

        BigDecimal spent = BigDecimal.ZERO;

        for (Transaction t : transactionDAO.findAll()) {

            String transactionMonth =
                t.getDate().toString().substring(0, 7);

            if (t.getType().equalsIgnoreCase("EXPENSE")
                    && t.getCategory().equalsIgnoreCase(category)
                    && transactionMonth.equals(month)) {

                spent = spent.add(t.getAmount());
            }
        }

        return spent;
    }

    // =========================
    // LOAD BUDGETS
    // =========================

    public void loadBudgets() {

        try {

            tableModel.setRowCount(0);

            List<Budget> budgets = budgetDAO.findAll();

            for (Budget budget : budgets) {

                BigDecimal spent = calculateSpent(
                    budget.getMonth(),
                    budget.getCategory()
                );

                BigDecimal remaining =
                    budget.getLimit().subtract(spent);

                String status;

                if (remaining.signum() < 0) {

                    status = "OVER BUDGET";

                } else if (spent.compareTo(
                        budget.getLimit().multiply(
                            new BigDecimal("0.80")
                        )) >= 0) {

                    status = "WARNING";

                } else {

                    status = "NORMAL";
                }

                tableModel.addRow(new Object[]{
                    budget.getId(),
                    budget.getMonth(),
                    budget.getCategory(),
                    formatCurrency(budget.getLimit()),
                    formatCurrency(spent),
                    formatCurrency(remaining),
                    status
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // SELECT BUDGET
    // =========================

    private void fillForm() {

        int viewRow = table.getSelectedRow();

        if (viewRow < 0) {
            return;
        }

        int row = table.convertRowIndexToModel(viewRow);

        selectedId = Integer.parseInt(
            tableModel.getValueAt(row, 0).toString()
        );

        monthField.setText(
            tableModel.getValueAt(row, 1).toString()
        );

        categoryBox.setSelectedItem(
            tableModel.getValueAt(row, 2).toString()
        );

        try {

            for (Budget budget : budgetDAO.findAll()) {

                if (budget.getId() == selectedId) {

                    limitField.setText(
                        budget.getLimit().toPlainString()
                    );

                    break;
                }
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // CLEAR
    // =========================

    private void clearForm() {

        selectedId = -1;

        monthField.setText(
            LocalDate.now().toString().substring(0, 7)
        );

        categoryBox.setSelectedIndex(0);

        limitField.setText("");

        table.clearSelection();
    }

    // =========================
    // HELPERS
    // =========================

    private String formatCurrency(BigDecimal amount) {

        NumberFormat formatter =
            NumberFormat.getCurrencyInstance(
                new Locale("id", "ID")
            );

        formatter.setMaximumFractionDigits(0);

        return formatter.format(amount);
    }

    private JButton createButton(String text, Color color) {

        JButton button = new JButton(text);

        button.setBackground(color);

        button.setForeground(Color.BLACK);

        button.setFocusPainted(false);

        button.setFont(new Font("Segoe UI", Font.BOLD, 12));

        return button;
    }

    private void showError(Exception e) {

        JOptionPane.showMessageDialog(
            this,
            e.getMessage(),
            "Budget Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
