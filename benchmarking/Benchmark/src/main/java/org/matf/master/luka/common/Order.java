package org.matf.master.luka.common;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Order {

    private BigDecimal amount;
    private String buyCurrencyCode;
    private String sellCurrencyCode;

}
