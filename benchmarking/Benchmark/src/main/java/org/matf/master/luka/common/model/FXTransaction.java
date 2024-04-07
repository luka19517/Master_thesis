package org.matf.master.luka.common.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class FXTransaction {

    private long id;
    private FXAccount fxAccount_from;
    private FXAccount fxAccount_to;
    private BigDecimal amount;
    private String status;
    private Date entryDate;

}
