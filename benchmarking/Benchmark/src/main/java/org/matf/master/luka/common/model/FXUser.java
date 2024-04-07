package org.matf.master.luka.common.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class FXUser {

    private long id;
    private String username;
    private String password;
    private String startBalanceCurrencyCode;
    private BigDecimal startBalance;
    private String firstName;
    private String lastName;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String phone;
    private String mobile;
    private String email;
    private Date created;

}
