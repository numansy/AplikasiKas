
package com.cashmanagement;

import com.cashmanagement.gui.DashboardFrame;
import com.cashmanagement.util.UITheme;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            UITheme.apply();

            DashboardFrame frame =
                new DashboardFrame();

            frame.setVisible(true);
        });
    }
}

