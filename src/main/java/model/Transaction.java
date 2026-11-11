
package com.cashmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {

    private int id;
    private LocalDate date;
    private String type;
    private String category;
    private String description;
    private BigDecimal amount;

    public Transaction(int id, LocalDate date, String type,
            String category, String description,
            BigDecimal amount) {

        this.id = id;
        this.date = date;
        this.type = type;
        this.category = category;
        this.description = description;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
