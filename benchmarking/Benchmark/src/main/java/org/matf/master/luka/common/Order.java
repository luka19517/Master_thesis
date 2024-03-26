package org.matf.master.luka.common;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class Order {

    private BigDecimal amount;
    private String buyCurrencyCode;
    private String sellCurrencyCode;



}
