
package com.cashmanagement.dao;

import com.cashmanagement.model.Transaction;
import com.cashmanagement.util.CSVUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    private static final String FILE =
        "data/transactions.csv";

    private static final String HEADER =
        "id,date,type,category,description,amount";

    public TransactionDAO() {

        try {
            CSVUtil.initialize(FILE, HEADER);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void add(Transaction t) throws IOException {

        CSVUtil.append(FILE, new String[]{
            String.valueOf(t.getId()),
            t.getDate().toString(),
            t.getType(),
            t.getCategory(),
            t.getDescription(),
            t.getAmount().toPlainString()
        });
    }

    public List<Transaction> findAll() throws IOException {

        List<Transaction> result = new ArrayList<>();

        List<String[]> rows = CSVUtil.read(FILE);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length != 6) continue;

            result.add(new Transaction(
                Integer.parseInt(row[0]),
                LocalDate.parse(row[1]),
                row[2],
                row[3],
                row[4],
                new BigDecimal(row[5])
            ));
        }

        return result;
    }

    public void update(Transaction t) throws IOException {

        List<String[]> rows = CSVUtil.read(FILE);

        for (int i = 1; i < rows.size(); i++) {

            if (Integer.parseInt(rows.get(i)[0]) == t.getId()) {

                rows.set(i, new String[]{
                    String.valueOf(t.getId()),
                    t.getDate().toString(),
                    t.getType(),
                    t.getCategory(),
                    t.getDescription(),
                    t.getAmount().toPlainString()
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

        for (Transaction t : findAll()) {
            max = Math.max(max, t.getId());
        }

        return max + 1;
    }
}
