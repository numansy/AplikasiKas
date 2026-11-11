
package com.cashmanagement.dao;

import com.cashmanagement.model.Budget;
import com.cashmanagement.util.CSVUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    private static final String FILE = "data/budgets.csv";

    private static final String HEADER =
        "id,month,category,limit";

    public BudgetDAO() {
        try {
            CSVUtil.initialize(FILE, HEADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(Budget budget) throws IOException {

        CSVUtil.append(FILE, new String[]{
            String.valueOf(budget.getId()),
            budget.getMonth(),
            budget.getCategory(),
            budget.getLimit().toPlainString()
        });
    }

    public List<Budget> findAll() throws IOException {

        List<Budget> result = new ArrayList<>();

        List<String[]> rows = CSVUtil.read(FILE);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length != 4) continue;

            result.add(new Budget(
                Integer.parseInt(row[0]),
                row[1],
                row[2],
                new BigDecimal(row[3])
            ));
        }

        return result;
    }

    public void update(Budget budget) throws IOException {

        List<String[]> rows = CSVUtil.read(FILE);

        for (int i = 1; i < rows.size(); i++) {

            if (Integer.parseInt(rows.get(i)[0])
                    == budget.getId()) {

                rows.set(i, new String[]{
                    String.valueOf(budget.getId()),
                    budget.getMonth(),
                    budget.getCategory(),
                    budget.getLimit().toPlainString()
                });

                break;
            }
        }

        CSVUtil.overwrite(FILE, rows);
    }

    public void deleteById(int id) throws IOException {

        List<String[]> rows = CSVUtil.read(FILE);

        List<String[]> updated = new ArrayList<>();

        if (!rows.isEmpty()) {
            updated.add(rows.get(0));
        }

        for (int i = 1; i < rows.size(); i++) {

            if (Integer.parseInt(rows.get(i)[0]) != id) {
                updated.add(rows.get(i));
            }
        }

        CSVUtil.overwrite(FILE, updated);
    }

    public int nextId() throws IOException {

        int max = 0;

        for (Budget budget : findAll()) {
            max = Math.max(max, budget.getId());
        }

        return max + 1;
    }
}
