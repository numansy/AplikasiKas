
package com.cashmanagement.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

    private static final Locale INDONESIA =
        new Locale("id", "ID");

    private CurrencyUtil() {
    }

    public static String format(BigDecimal amount) {

        NumberFormat formatter =
            NumberFormat.getCurrencyInstance(INDONESIA);

        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);

        return formatter.format(amount);
    }

    public static String format(double amount) {
        return format(BigDecimal.valueOf(amount));
    }

    public static BigDecimal parse(String value) {

        String cleaned = value
            .replace("Rp", "")
            .replace(".", "")
            .replace(",", "")
            .trim();

        return new BigDecimal(cleaned);
    }
}
