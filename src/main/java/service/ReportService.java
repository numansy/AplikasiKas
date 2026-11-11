
package com.cashmanagement.service;

import com.cashmanagement.dao.TransactionDAO;
import com.cashmanagement.dao.SavingsDAO;
import com.cashmanagement.model.Transaction;
import com.cashmanagement.model.Savings;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private final TransactionDAO transactionDAO;
    private final SavingsDAO savingsDAO;

    public ReportService() {
        transactionDAO = new TransactionDAO();
        savingsDAO = new SavingsDAO();
    }

    public BigDecimal getIncome(String month) throws IOException {

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction t : transactionDAO.findAll()) {

            if (t.getDate().toString().startsWith(month)
                    && t.getType().equalsIgnoreCase("INCOME")) {

                total = total.add(t.getAmount());
            }
        }

        return total;
    }

    public BigDecimal getExpenses(String month)
            throws IOException {

        BigDecimal total = BigDecimal.ZERO;

        for (Transaction t : transactionDAO.findAll()) {

            if (t.getDate().toString().startsWith(month)
                    && t.getType().equalsIgnoreCase("EXPENSE")) {

                total = total.add(t.getAmount());
            }
        }

        return total;
    }

    public BigDecimal getNetCashFlow(String month)
            throws IOException {

        return getIncome(month).subtract(getExpenses(month));
    }

    public Map<String, BigDecimal> getExpensesByCategory(
            String month) throws IOException {

        Map<String, BigDecimal> result =
            new LinkedHashMap<>();

        for (Transaction t : transactionDAO.findAll()) {

            if (t.getDate().toString().startsWith(month)
                    && t.getType().equalsIgnoreCase("EXPENSE")) {

                result.merge(
                    t.getCategory(),
                    t.getAmount(),
                    BigDecimal::add
                );
            }
        }

        return result;
    }

    public Map<String, BigDecimal[]> getMonthlyTrends(
            int numberOfMonths) throws IOException {

        Map<String, BigDecimal[]> result =
            new LinkedHashMap<>();

        YearMonth current = YearMonth.now();

        for (int i = numberOfMonths - 1; i >= 0; i--) {

            YearMonth month = current.minusMonths(i);

            String key = month.toString();

            result.put(key, new BigDecimal[]{
                getIncome(key),
                getExpenses(key)
            });
        }

        return result;
    }

    public BigDecimal getTotalSavings() throws IOException {

        BigDecimal total = BigDecimal.ZERO;

        for (Savings savings : savingsDAO.findAll()) {

            total = total.add(
                savingsDAO.getBalance(savings.getId())
            );
        }

        return total;
    }

    public BigDecimal getTotalSavingsTarget()
            throws IOException {

        BigDecimal total = BigDecimal.ZERO;

        for (Savings savings : savingsDAO.findAll()) {

            total = total.add(savings.getTarget());
        }

        return total;
    }
}
