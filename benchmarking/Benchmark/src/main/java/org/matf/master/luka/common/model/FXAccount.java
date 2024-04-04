package org.matf.master.luka.common.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class FXAccount {

    private long id;
    private long fxUser;
    private String currency_code;
    private BigDecimal balance;
    private Date created;


}
