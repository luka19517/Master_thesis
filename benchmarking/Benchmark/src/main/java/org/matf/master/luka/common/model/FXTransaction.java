package org.matf.master.luka.common.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FXTransaction {

    private long id;
    private long fxAccount_from;
    private long fxAccount_to;
    private BigDecimal amount;
    private String status;
    private Date entryDate;

}
