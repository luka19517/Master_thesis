package org.matf.master.luka.common.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class FXAccount {

    private long id;
    private FXUser fxUser;
    private String currency_code;
    private BigDecimal balance;
    private Date created;


}
