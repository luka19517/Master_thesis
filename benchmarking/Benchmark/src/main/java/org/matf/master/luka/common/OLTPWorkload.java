package org.matf.master.luka.common;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OLTPWorkload {

    private OLTPService oltpService;

    public OLTPWorkload(OLTPService oltpService){
        this.oltpService = oltpService;
    }

    public void executeWorkload(){
        for(int i=0;i<1000;i++){
            executeWorkflow(i);
        }
    }

    private void executeWorkflow(int seed) {
        String username = "user_"+seed;
        String password = "password_"+seed;
        Order order =  Order.builder().amount(new BigDecimal(seed)).sellCurrencyCode("EUR").buyCurrencyCode("USD").build();
        oltpService.authenticateUser(username,password);
        oltpService.createOrder(order);
    }
}
