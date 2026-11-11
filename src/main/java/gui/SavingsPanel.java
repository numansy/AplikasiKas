
package com.cashmanagement.gui;

import com.cashmanagement.dao.SavingsDAO;
import com.cashmanagement.model.Savings;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

public class SavingsPanel extends JPanel {

    private final SavingsDAO dao = new SavingsDAO();

    private JTextField nameField;
    private JTextField targetField;
    private JTextField amountField;
    private JTextField descriptionField;

    private JTable table;
    private DefaultTableModel tableModel;

    private int selectedId = -1;

    public SavingsPanel() {

        setLayout(new BorderLayout(15, 15));

        setBackground(new Color(245, 247, 250));

        setBorder(BorderFactory.createEmptyBorder(
            20, 20, 20, 20
        ));

        createForm();
        createTable();

        loadSavings();
    }

    // =========================
    // FORM
    // =========================

    private void createForm() {

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));

        form.setBackground(Color.lightGray);

        form.setBorder(BorderFactory.createTitledBorder(
            "Savings Management"
        ));

        nameField = new JTextField();

        targetField = new JTextField();

        amountField = new JTextField();

        descriptionField = new JTextField();

        JButton addButton = createButton(
            "Create Goal", new Color(22, 163, 74)
        );

        JButton updateButton = createButton(
            "Update Goal", new Color(37, 99, 235)
        );

        JButton depositButton = createButton(
            "Deposit", new Color(22, 163, 74)
        );

        JButton withdrawButton = createButton(
            "Withdraw", new Color(220, 38, 38)
        );

        JButton deleteButton = createButton(
            "Delete Goal", new Color(185, 28, 28)
        );

        JButton clearButton = createButton(
            "Clear", new Color(100, 116, 139)
        );

        form.add(new JLabel("Goal Name"));
        form.add(nameField);

        form.add(new JLabel("Target Amount (IDR)"));
        form.add(targetField);

        form.add(new JLabel("Transaction Amount (IDR)"));
        form.add(amountField);

        form.add(new JLabel("Description"));
        form.add(descriptionField);

        form.add(addButton);
        form.add(updateButton);

        form.add(depositButton);
        form.add(withdrawButton);

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

        addButton.addActionListener(e -> addGoal());

        updateButton.addActionListener(e -> updateGoal());

        depositButton.addActionListener(e -> deposit());

        withdrawButton.addActionListener(e -> withdraw());

        deleteButton.addActionListener(e -> deleteGoal());

        clearButton.addActionListener(e -> clearForm());
    }

    // =========================
    // TABLE
    // =========================

    private void createTable() {

        tableModel = new DefaultTableModel(
            new String[]{
                "ID",
                "Goal Name",
                "Target Amount",
                "Current Savings",
                "Remaining",
                "Progress"
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

        JLabel title = new JLabel("My Savings Goals");

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
                    "Amount must be greater than zero."
                );
            }

            return amount;

        } catch (NumberFormatException e) {

            throw new IllegalArgumentException(
                "Please enter a valid numeric amount."
            );
        }
    }

    private Savings findSelectedGoal() throws Exception {

        for (Savings savings : dao.findAll()) {

            if (savings.getId() == selectedId) {
                return savings;
            }
        }

        return null;
    }

    // =========================
    // ADD GOAL
    // =========================

    private void addGoal() {

        try {

            String name = nameField.getText().trim();

            if (name.isEmpty()) {

                throw new IllegalArgumentException(
                    "Goal name cannot be empty."
                );
            }

            BigDecimal target = validateAmount(
                targetField.getText()
            );

            Savings savings = new Savings(
                dao.nextId(),
                name,
                target,
                LocalDate.now()
            );

            dao.add(savings);

            JOptionPane.showMessageDialog(
                this,
                "Savings goal created successfully!"
            );

            clearForm();
            loadSavings();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // UPDATE GOAL
    // =========================

    private void updateGoal() {

        if (selectedId == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a savings goal first."
            );

            return;
        }

        try {

            String name = nameField.getText().trim();

            if (name.isEmpty()) {

                throw new IllegalArgumentException(
                    "Goal name cannot be empty."
                );
            }

            BigDecimal target = validateAmount(
                targetField.getText()
            );

            Savings savings = findSelectedGoal();

            if (savings == null) {

                throw new IllegalArgumentException(
                    "Savings goal not found."
                );
            }

            savings.setName(name);

            savings.setTarget(target);

            dao.update(savings);

            JOptionPane.showMessageDialog(
                this,
                "Savings goal updated successfully!"
            );

            clearForm();
            loadSavings();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // DELETE GOAL
    // =========================

    private void deleteGoal() {

        if (selectedId == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a savings goal first."
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
            this,
            "Delete this savings goal and its transaction history?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            dao.deleteById(selectedId);

            JOptionPane.showMessageDialog(
                this,
                "Savings goal deleted successfully!"
            );

            clearForm();
            loadSavings();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // DEPOSIT
    // =========================

    private void deposit() {

        recordMovement("DEPOSIT");
    }

    // =========================
    // WITHDRAWAL
    // =========================

    private void withdraw() {

        recordMovement("WITHDRAWAL");
    }

    // =========================
    // RECORD MOVEMENT
    // =========================

    private void recordMovement(String type) {

        if (selectedId == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a savings goal first."
            );

            return;
        }

        try {

            BigDecimal amount = validateAmount(
                amountField.getText()
            );

            String description =
                descriptionField.getText().trim();

            if (description.isEmpty()) {

                throw new IllegalArgumentException(
                    "Description cannot be empty."
                );
            }

            dao.addMovement(
                selectedId,
                type,
                amount,
                description
            );

            JOptionPane.showMessageDialog(
                this,
                type + " recorded successfully!"
            );

            amountField.setText("");

            descriptionField.setText("");

            loadSavings();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // LOAD SAVINGS
    // =========================

    public void loadSavings() {

        try {

            tableModel.setRowCount(0);

            List<Savings> savingsList = dao.findAll();

            for (Savings savings : savingsList) {

                BigDecimal balance =
                    dao.getBalance(savings.getId());

                BigDecimal remaining =
                    savings.getTarget().subtract(balance);

                BigDecimal progress = balance
                    .multiply(new BigDecimal("100"))
                    .divide(
                        savings.getTarget(),
                        2,
                        RoundingMode.HALF_UP
                    );

                tableModel.addRow(new Object[]{
                    savings.getId(),
                    savings.getName(),
                    formatCurrency(savings.getTarget()),
                    formatCurrency(balance),
                    formatCurrency(remaining),
                    progress.toPlainString() + "%"
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // SELECT SAVINGS GOAL
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

        nameField.setText(
            tableModel.getValueAt(row, 1).toString()
        );

        try {

            Savings savings = findSelectedGoal();

            if (savings != null) {

                targetField.setText(
                    savings.getTarget().toPlainString()
                );
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // CLEAR FORM
    // =========================

    private void clearForm() {

        selectedId = -1;

        nameField.setText("");

        targetField.setText("");

        amountField.setText("");

        descriptionField.setText("");

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
            "Savings Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
