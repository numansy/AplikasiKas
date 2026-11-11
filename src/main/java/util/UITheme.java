
package com.cashmanagement.util;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class UITheme {

    public static final Color PRIMARY =
        new Color(31, 41, 55);

    public static final Color PRIMARY_LIGHT =
        new Color(55, 65, 81);

    public static final Color BACKGROUND =
        new Color(245, 247, 250);

    public static final Color WHITE =
        Color.WHITE;

    public static final Color GREEN =
        new Color(22, 163, 74);

    public static final Color RED =
        new Color(220, 38, 38);

    public static final Color BLUE =
        new Color(37, 99, 235);

    public static final Color PURPLE =
        new Color(147, 51, 234);

    public static final Color BORDER =
        new Color(226, 232, 240);

    public static final Font TITLE_FONT =
        new Font("Segoe UI", Font.BOLD, 25);

    public static final Font SUBTITLE_FONT =
        new Font("Segoe UI", Font.BOLD, 17);

    public static final Font NORMAL_FONT =
        new Font("Segoe UI", Font.PLAIN, 13);

    public static final Font BUTTON_FONT =
        new Font("Segoe UI", Font.BOLD, 13);

    private UITheme() {
    }

    public static void apply() {

        try {
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName()
            );
        } catch (Exception e) {
            System.err.println(
                "Could not apply system look and feel."
            );
        }

        UIManager.put("Label.font", NORMAL_FONT);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("TextField.font", NORMAL_FONT);
        UIManager.put("ComboBox.font", NORMAL_FONT);
        UIManager.put("Table.font", NORMAL_FONT);

        UIManager.put(
            "Table.selectionBackground",
            new Color(219, 234, 254)
        );

        UIManager.put(
            "Table.selectionForeground",
            PRIMARY
        );

        UIManager.put(
            "Table.gridColor",
            BORDER
        );

        UIManager.put(
            "Panel.background",
            BACKGROUND
        );
    }

    public static JButton createButton(
            String text, Color background) {

        JButton button = new JButton(text);

        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(
            Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        button.setBorder(
            new EmptyBorder(10, 16, 10, 16)
        );

        return button;
    }

    public static void styleTable(JTable table) {

        table.setFont(NORMAL_FONT);
        table.setRowHeight(32);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(BORDER);
        table.setSelectionBackground(
            new Color(219, 234, 254)
        );

        JTableHeader header = table.getTableHeader();

        header.setFont(
            new Font("Segoe UI", Font.BOLD, 13)
        );

        header.setBackground(
            new Color(241, 245, 249)
        );

        header.setForeground(PRIMARY);

        header.setPreferredSize(
            new Dimension(100, 38)
        );
    }

    public static JPanel createCard(
            String title, String value, Color accent) {

        JPanel card = new JPanel(new BorderLayout(5, 12));

        card.setBackground(WHITE);

        card.setBorder(
            new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(18, 18, 18, 18)
            )
        );

        JLabel titleLabel = new JLabel(title);

        titleLabel.setForeground(
            new Color(100, 116, 139)
        );

        JLabel valueLabel = new JLabel(value);

        valueLabel.setFont(
            new Font("Segoe UI", Font.BOLD, 21)
        );

        valueLabel.setForeground(accent);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }
}
