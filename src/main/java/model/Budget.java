
package com.cashmanagement.model;

import java.math.BigDecimal;

public class Budget {

    private int id;
    private String month;
    private String category;
    private BigDecimal limit;

    public Budget(int id, String month,
            String category, BigDecimal limit) {

        this.id = id;
        this.month = month;
        this.category = category;
        this.limit = limit;
    }

    public int getId() {
        return id;
    }

    public String getMonth() {
        return month;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }
}
