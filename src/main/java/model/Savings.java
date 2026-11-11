
package com.cashmanagement.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Savings {

    private int id;
    private String name;
    private BigDecimal target;
    private LocalDate createdDate;

    public Savings(int id, String name,
            BigDecimal target, LocalDate createdDate) {

        this.id = id;
        this.name = name;
        this.target = target;
        this.createdDate = createdDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getTarget() {
        return target;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTarget(BigDecimal target) {
        this.target = target;
    }
}
