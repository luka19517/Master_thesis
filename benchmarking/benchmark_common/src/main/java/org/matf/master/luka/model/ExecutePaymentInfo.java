package org.matf.master.luka.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ExecutePaymentInfo {

    private BigDecimal amountToGive;
    private BigDecimal amountToReceive;
    private long accountFrom;
    private long accountTo;

}
