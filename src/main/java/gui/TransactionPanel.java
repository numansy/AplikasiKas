
package com.cashmanagement.gui;

import com.cashmanagement.dao.TransactionDAO;
import com.cashmanagement.model.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TransactionPanel extends JPanel {

    private final TransactionDAO dao = new TransactionDAO();
    private final Runnable onDataChanged;

    private JTextField dateField;
    private JTextField descriptionField;
    private JTextField amountField;

    private JComboBox<String> typeBox;
    private JComboBox<String> categoryBox;

    private JTable table;
    private DefaultTableModel tableModel;

    private int selectedId = -1;

    public TransactionPanel() {
        this(() -> {});
    }

    public TransactionPanel(Runnable onDataChanged) {

        this.onDataChanged = onDataChanged;

        setLayout(new BorderLayout(15, 15));

        setBackground(new Color(245, 247, 250));

        setBorder(BorderFactory.createEmptyBorder(
            20, 20, 20, 20
        ));

        createForm();
        createTable();

        loadTransactions();
    }

    // =========================
    // FORM
    // =========================

    private void createForm() {

        JPanel form = new JPanel(new GridLayout(3, 4, 10, 10));

        form.setBackground(Color.WHITE);

        form.setBorder(BorderFactory.createTitledBorder(
            "Transaction Management"
        ));

        dateField = new JTextField(LocalDate.now().toString());

        descriptionField = new JTextField();

        amountField = new JTextField();

        typeBox = new JComboBox<>(new String[]{
            "INCOME",
            "EXPENSE"
        });

        categoryBox = new JComboBox<>(new String[]{
            "Salary",
            "Business",
            "Food",
            "Transport",
            "Shopping",
            "Bills",
            "Entertainment",
            "Health",
            "Education",
            "Other"
        });

        JButton addButton = createButton(
            "Add Transaction", new Color(22, 163, 74)
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

        form.add(new JLabel("Date (YYYY-MM-DD)"));
        form.add(dateField);

        form.add(new JLabel("Type"));
        form.add(typeBox);

        form.add(new JLabel("Category"));
        form.add(categoryBox);

        form.add(new JLabel("Description"));
        form.add(descriptionField);

        form.add(new JLabel("Amount (IDR)"));
        form.add(amountField);

        form.add(addButton);
        form.add(updateButton);

        JPanel container = new JPanel(new BorderLayout(5, 5));

        container.setOpaque(false);

        container.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(
            new FlowLayout(FlowLayout.RIGHT)
        );

        buttons.setOpaque(false);

        buttons.add(deleteButton);
        buttons.add(clearButton);

        container.add(buttons, BorderLayout.SOUTH);

        add(container, BorderLayout.NORTH);

        addButton.addActionListener(e -> addTransaction());

        updateButton.addActionListener(e -> updateTransaction());

        deleteButton.addActionListener(e -> deleteTransaction());

        clearButton.addActionListener(e -> clearForm());
    }

    // =========================
    // TABLE
    // =========================

    private void createTable() {

        tableModel = new DefaultTableModel(
            new String[]{
                "ID",
                "Date",
                "Type",
                "Category",
                "Description",
                "Amount (IDR)"
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

        JLabel title = new JLabel("Transaction History");

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

    private LocalDate validateDate(String value) {

        try {

            return LocalDate.parse(value.trim());

        } catch (DateTimeParseException e) {

            throw new IllegalArgumentException(
                "Date must use YYYY-MM-DD format."
            );
        }
    }

    private Transaction getFormTransaction(int id) {

        LocalDate date = validateDate(dateField.getText());

        BigDecimal amount = validateAmount(
            amountField.getText()
        );

        String type = typeBox.getSelectedItem().toString();

        String category = categoryBox.getSelectedItem().toString();

        String description = descriptionField.getText().trim();

        if (description.isEmpty()) {
            throw new IllegalArgumentException(
                "Description cannot be empty."
            );
        }

        return new Transaction(
            id,
            date,
            type,
            category,
            description,
            amount
        );
    }

    // =========================
    // ADD
    // =========================

    private void addTransaction() {

        try {

            Transaction transaction = getFormTransaction(
                dao.nextId()
            );

            dao.add(transaction);

            JOptionPane.showMessageDialog(
                this,
                "Transaction added successfully!"
            );

            clearForm();
            loadTransactions();
            notifyDataChanged();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // UPDATE
    // =========================

    private void updateTransaction() {

        if (selectedId == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a transaction first."
            );

            return;
        }

        try {

            Transaction transaction = getFormTransaction(
                selectedId
            );

            dao.update(transaction);

            JOptionPane.showMessageDialog(
                this,
                "Transaction updated successfully!"
            );

            clearForm();
            loadTransactions();
            notifyDataChanged();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // DELETE
    // =========================

    private void deleteTransaction() {

        if (selectedId == -1) {

            JOptionPane.showMessageDialog(
                this,
                "Please select a transaction first."
            );

            return;
        }

        int answer = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this transaction?",
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
                "Transaction deleted successfully!"
            );

            clearForm();
            loadTransactions();
            notifyDataChanged();

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // LOAD DATA
    // =========================

    public void loadTransactions() {

        try {

            tableModel.setRowCount(0);

            List<Transaction> transactions = dao.findAll();

            for (Transaction t : transactions) {

                tableModel.addRow(new Object[]{
                    t.getId(),
                    t.getDate(),
                    t.getType(),
                    t.getCategory(),
                    t.getDescription(),
                    formatCurrency(t.getAmount())
                });
            }

        } catch (Exception e) {
            showError(e);
        }
    }

    // =========================
    // SELECT TABLE ROW
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

        dateField.setText(
            tableModel.getValueAt(row, 1).toString()
        );

        typeBox.setSelectedItem(
            tableModel.getValueAt(row, 2).toString()
        );

        categoryBox.setSelectedItem(
            tableModel.getValueAt(row, 3).toString()
        );

        descriptionField.setText(
            tableModel.getValueAt(row, 4).toString()
        );

        try {

            for (Transaction t : dao.findAll()) {

                if (t.getId() == selectedId) {

                    amountField.setText(
                        t.getAmount().toPlainString()
                    );

                    break;
                }
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

        dateField.setText(LocalDate.now().toString());

        typeBox.setSelectedIndex(0);

        categoryBox.setSelectedIndex(0);

        descriptionField.setText("");

        amountField.setText("");

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

    private void notifyDataChanged() {

        if (onDataChanged != null) {
            onDataChanged.run();
        }
    }

    private void showError(Exception e) {

        JOptionPane.showMessageDialog(
            this,
            e.getMessage(),
            "Transaction Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
}
