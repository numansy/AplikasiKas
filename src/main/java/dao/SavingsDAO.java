
package com.cashmanagement.dao;

import com.cashmanagement.model.Savings;
import com.cashmanagement.util.CSVUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SavingsDAO {

    private static final String GOALS_FILE =
        "data/savings.csv";

    private static final String MOVEMENTS_FILE =
        "data/savings_transactions.csv";

    public SavingsDAO() {

        try {

            CSVUtil.initialize(
                GOALS_FILE,
                "id,name,target,createdDate"
            );

            CSVUtil.initialize(
                MOVEMENTS_FILE,
                "id,savingsId,date,type,amount,description"
            );

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // CREATE SAVINGS GOAL
    public void add(Savings savings) throws IOException {

        CSVUtil.append(GOALS_FILE, new String[]{
            String.valueOf(savings.getId()),
            savings.getName(),
            savings.getTarget().toPlainString(),
            savings.getCreatedDate().toString()
        });
    }

    // READ SAVINGS GOALS
    public List<Savings> findAll() throws IOException {

        List<Savings> result = new ArrayList<>();

        List<String[]> rows = CSVUtil.read(GOALS_FILE);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length != 4) continue;

            result.add(new Savings(
                Integer.parseInt(row[0]),
                row[1],
                new BigDecimal(row[2]),
                LocalDate.parse(row[3])
            ));
        }

        return result;
    }

    // UPDATE SAVINGS GOAL
    public void update(Savings savings) throws IOException {

        List<String[]> rows = CSVUtil.read(GOALS_FILE);

        for (int i = 1; i < rows.size(); i++) {

            if (Integer.parseInt(rows.get(i)[0])
                    == savings.getId()) {

                rows.set(i, new String[]{
                    String.valueOf(savings.getId()),
                    savings.getName(),
                    savings.getTarget().toPlainString(),
                    savings.getCreatedDate().toString()
                });

                break;
            }
        }

        CSVUtil.overwrite(GOALS_FILE, rows);
    }

    // DELETE SAVINGS GOAL
    public void deleteById(int id) throws IOException {

        List<String[]> goals = CSVUtil.read(GOALS_FILE);
        List<String[]> updatedGoals = new ArrayList<>();

        if (!goals.isEmpty()) {
            updatedGoals.add(goals.get(0));
        }

        for (int i = 1; i < goals.size(); i++) {

            if (Integer.parseInt(goals.get(i)[0]) != id) {
                updatedGoals.add(goals.get(i));
            }
        }

        List<String[]> movements =
            CSVUtil.read(MOVEMENTS_FILE);

        List<String[]> updatedMovements = new ArrayList<>();

        if (!movements.isEmpty()) {
            updatedMovements.add(movements.get(0));
        }

        for (int i = 1; i < movements.size(); i++) {

            if (Integer.parseInt(movements.get(i)[1]) != id) {
                updatedMovements.add(movements.get(i));
            }
        }

        CSVUtil.overwrite(GOALS_FILE, updatedGoals);
        CSVUtil.overwrite(MOVEMENTS_FILE, updatedMovements);
    }

    // RECORD DEPOSIT OR WITHDRAWAL
    public void addMovement(int savingsId, String type,
            BigDecimal amount, String description)
            throws IOException {

        if (!type.equals("DEPOSIT")
                && !type.equals("WITHDRAWAL")) {
            throw new IllegalArgumentException(
                "Invalid savings transaction type."
            );
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                "Amount must be greater than zero."
            );
        }

        boolean goalExists = false;

        for (Savings savings : findAll()) {
            if (savings.getId() == savingsId) {
                goalExists = true;
                break;
            }
        }

        if (!goalExists) {
            throw new IllegalArgumentException(
                "Savings goal does not exist."
            );
        }

        if (type.equals("WITHDRAWAL")
                && getBalance(savingsId).compareTo(amount) < 0) {
            throw new IllegalArgumentException(
                "Insufficient savings balance."
            );
        }

        CSVUtil.append(MOVEMENTS_FILE, new String[]{
            String.valueOf(nextMovementId()),
            String.valueOf(savingsId),
            LocalDate.now().toString(),
            type,
            amount.toPlainString(),
            description
        });
    }

    // CALCULATE SAVINGS BALANCE
    public BigDecimal getBalance(int savingsId)
            throws IOException {

        BigDecimal balance = BigDecimal.ZERO;

        List<String[]> rows = CSVUtil.read(MOVEMENTS_FILE);

        for (int i = 1; i < rows.size(); i++) {

            String[] row = rows.get(i);

            if (row.length != 6) continue;

            if (Integer.parseInt(row[1]) == savingsId) {

                BigDecimal amount = new BigDecimal(row[4]);

                if (row[3].equals("DEPOSIT")) {
                    balance = balance.add(amount);
                } else if (row[3].equals("WITHDRAWAL")) {
                    balance = balance.subtract(amount);
                }
            }
        }

        return balance;
    }

    // NEXT GOAL ID
    public int nextId() throws IOException {

        int max = 0;

        for (Savings savings : findAll()) {
            max = Math.max(max, savings.getId());
        }

        return max + 1;
    }

    // NEXT MOVEMENT ID
    private int nextMovementId() throws IOException {

        List<String[]> rows = CSVUtil.read(MOVEMENTS_FILE);

        int max = 0;

        for (int i = 1; i < rows.size(); i++) {
            max = Math.max(max, Integer.parseInt(rows.get(i)[0]));
        }

        return max + 1;
    }
}
