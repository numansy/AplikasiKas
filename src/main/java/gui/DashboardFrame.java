
package com.cashmanagement.gui;

import com.cashmanagement.gui.BudgetPanel;
import com.cashmanagement.gui.DashboardPanel;
import com.cashmanagement.gui.ReportsPanel;
import com.cashmanagement.gui.SavingsPanel;
import com.cashmanagement.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame {

    private JPanel contentPanel;
    private CardLayout cardLayout;

    private DashboardPanel dashboardPanel;
    private TransactionPanel transactionPanel;
    private BudgetPanel budgetPanel;
    private SavingsPanel savingsPanel;
    private ReportsPanel reportsPanel;

    private final Color SIDEBAR =
        new Color(24, 35, 52);

    private final Color SIDEBAR_HOVER =
        new Color(45, 60, 80);

    public DashboardFrame() {

        setTitle("CashFlow - Personal Finance Manager");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setSize(1200, 760);

        setMinimumSize(new Dimension(950, 600));

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        dashboardPanel = new DashboardPanel();

        transactionPanel = new TransactionPanel(
            () -> dashboardPanel.refreshData()
        );

        budgetPanel = new BudgetPanel();

        savingsPanel = new SavingsPanel();

        reportsPanel = new ReportsPanel();

        createSidebar();
        createContent();
    }

    private void createSidebar() {

        JPanel sidebar = new JPanel();

        sidebar.setBackground(SIDEBAR);

        sidebar.setPreferredSize(
            new Dimension(225, 0)
        );

        sidebar.setLayout(
            new BoxLayout(sidebar, BoxLayout.Y_AXIS)
        );

        sidebar.setBorder(
            new EmptyBorder(20, 12, 20, 12)
        );

        JLabel logo = new JLabel("  CASHFLOW");

        logo.setFont(
            new Font("Segoe UI", Font.BOLD, 23)
        );

        logo.setForeground(Color.WHITE);

        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        logo.setBorder(
            new EmptyBorder(10, 8, 35, 5)
        );

        sidebar.add(logo);

        addMenuButton(sidebar, "Dashboard", "dashboard");
        addMenuButton(sidebar, "Transactions", "transactions");
        addMenuButton(sidebar, "Budget Management", "budget");
        addMenuButton(sidebar, "Savings Management", "savings");
        addMenuButton(sidebar, "Monthly Reports", "reports");

        sidebar.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("Personal Finance");

        footer.setForeground(new Color(180, 190, 205));

        footer.setBorder(
            new EmptyBorder(10, 8, 5, 5)
        );

        sidebar.add(footer);

        add(sidebar, BorderLayout.WEST);
    }

    private void addMenuButton(
            JPanel sidebar,
            String text,
            String page) {

        JButton button = new JButton(text);

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.setMaximumSize(
            new Dimension(Integer.MAX_VALUE, 48)
        );

        button.setPreferredSize(
            new Dimension(200, 48)
        );

        button.setHorizontalAlignment(
            SwingConstants.LEFT
        );

        button.setForeground(Color.WHITE);

        button.setBackground(SIDEBAR);

        button.setFont(
            new Font("Segoe UI", Font.PLAIN, 14)
        );

        button.setFocusPainted(false);

        button.setBorderPainted(false);

        button.setCursor(
            Cursor.getPredefinedCursor(
                Cursor.HAND_CURSOR
            )
        );

        button.setBorder(
            new EmptyBorder(10, 15, 10, 10)
        );

        button.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(
                    java.awt.event.MouseEvent e) {

                button.setBackground(SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(
                    java.awt.event.MouseEvent e) {

                button.setBackground(SIDEBAR);
            }
        });

        button.addActionListener(e -> showPage(page));

        sidebar.add(button);

        sidebar.add(Box.createVerticalStrut(7));
    }

    private void createContent() {

        cardLayout = new CardLayout();

        contentPanel = new JPanel(cardLayout);

        contentPanel.setBackground(UITheme.BACKGROUND);

        contentPanel.add(
            dashboardPanel, "dashboard"
        );

        contentPanel.add(
            transactionPanel, "transactions"
        );

        contentPanel.add(
            budgetPanel, "budget"
        );

        contentPanel.add(
            savingsPanel, "savings"
        );

        contentPanel.add(
            reportsPanel, "reports"
        );

        add(contentPanel, BorderLayout.CENTER);

        showPage("dashboard");
    }

    private void showPage(String page) {

        switch (page) {

            case "dashboard":
                dashboardPanel.refreshData();
                break;

            case "transactions":
                transactionPanel.loadTransactions();
                break;

            case "budget":
                budgetPanel.loadBudgets();
                break;

            case "savings":
                savingsPanel.loadSavings();
                break;

            case "reports":
                reportsPanel.refreshReport();
                break;

            default:
                return;
        }

        cardLayout.show(contentPanel, page);
    }
}
