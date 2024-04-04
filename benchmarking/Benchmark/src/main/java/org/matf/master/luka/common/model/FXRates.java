package org.matf.master.luka.common.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FXRates {

    private String currency_to;
    private String currency_from;
    private BigDecimal rate;
}
