
package com.cashmanagement.gui;

import com.cashmanagement.dao.TransactionDAO;
import com.cashmanagement.model.Transaction;
import com.cashmanagement.util.CurrencyUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

public class DashboardPanel extends JPanel {

    private final TransactionDAO dao = new TransactionDAO();

    private JLabel balanceLabel;
    private JLabel incomeLabel;
    private JLabel expenseLabel;

    private DefaultTableModel tableModel;

    public DashboardPanel() {

        setLayout(new BorderLayout(15, 15));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(
                25, 25, 25, 25));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 20));
        center.setOpaque(false);

        JPanel cards = new JPanel(new GridLayout(1, 3, 15, 15));
        cards.setOpaque(false);

        balanceLabel = new JLabel("Rp0");
        incomeLabel = new JLabel("Rp0");
        expenseLabel = new JLabel("Rp0");

        cards.add(createCard("TOTAL BALANCE", balanceLabel,
                new Color(37, 99, 235)));

        cards.add(createCard("TOTAL INCOME", incomeLabel,
                new Color(22, 163, 74)));

        cards.add(createCard("TOTAL EXPENSES", expenseLabel,
                new Color(220, 38, 38)));

        center.add(cards, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
            new String[]{"ID", "Date", "Type",
                "Category", "Description", "Amount"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(28);

        JScrollPane scroll = new JScrollPane(table);

        JPanel history = new JPanel(new BorderLayout(5, 10));
        history.setOpaque(false);

        JLabel historyTitle = new JLabel("Recent Transactions");
        historyTitle.setFont(new Font("Arial", Font.BOLD, 18));

        history.add(historyTitle, BorderLayout.NORTH);
        history.add(scroll, BorderLayout.CENTER);

        center.add(history, BorderLayout.CENTER);

        add(center, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createCard(String title,
            JLabel value, Color color) {

        JPanel card = new JPanel(new GridLayout(2, 1, 5, 5));

        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                new Color(225, 229, 235)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel heading = new JLabel(title);
        heading.setForeground(Color.GRAY);
        heading.setFont(new Font("Arial", Font.BOLD, 12));

        value.setForeground(color);
        value.setFont(new Font("Arial", Font.BOLD, 20));

        card.add(heading);
        card.add(value);

        return card;
    }
    
public void refreshData() {

    try {

        List<Transaction> transactions = dao.findAll();

        // Sort newest transactions first
        transactions.sort(
            Comparator.comparing(Transaction::getDate).reversed()
        );

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        tableModel.setRowCount(0);

        for (Transaction t : transactions) {

            if (t.getType().equalsIgnoreCase("INCOME")) {

                income = income.add(t.getAmount());

            } else if (t.getType().equalsIgnoreCase("EXPENSE")) {

                expense = expense.add(t.getAmount());
            }
        }

        BigDecimal balance = income.subtract(expense);

        balanceLabel.setText(
            CurrencyUtil.format(balance)
        );

        incomeLabel.setText(
            CurrencyUtil.format(income)
        );

        expenseLabel.setText(
            CurrencyUtil.format(expense)
        );

        // Display latest 10 transactions
        int end = Math.min(10, transactions.size());

        for (int i = 0; i < end; i++) {

            Transaction t = transactions.get(i);

            tableModel.addRow(new Object[]{
                t.getId(),
                t.getDate(),
                t.getType(),
                t.getCategory(),
                t.getDescription(),
                CurrencyUtil.format(t.getAmount())
            });
        }

    } catch (Exception e) {

        JOptionPane.showMessageDialog(
            this,
            "Error loading dashboard: " + e.getMessage(),
            "Dashboard Error",
            JOptionPane.ERROR_MESSAGE
        );
    }
    }
}
