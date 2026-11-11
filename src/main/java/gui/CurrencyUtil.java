
package gui;

import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

    private static final NumberFormat FORMAT =
        NumberFormat.getCurrencyInstance(
            Locale.forLanguageTag("id-ID")
        );

    public static String format(double amount) {
        return FORMAT.format(amount);
    }

    public static String format(java.math.BigDecimal amount) {
        return FORMAT.format(amount);
    }
}
