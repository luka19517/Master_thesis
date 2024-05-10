package org.matf.master.luka.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FXRates {

    private String currency_to;
    private String currency_from;
    private BigDecimal rate;

}
