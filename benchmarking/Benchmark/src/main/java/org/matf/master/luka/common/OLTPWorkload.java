package org.matf.master.luka.common;

import lombok.Data;

@Data
public class OLTPWorkload {

    private OLTPService oltpService;

    public OLTPWorkload(OLTPService oltpService){
        this.oltpService = oltpService;
    }

    public void executeWorkload(){
        this.oltpService.authenticateUser("username","password");
        this.oltpService.createOrder(new Order());
        this.oltpService.createOrder(new Order());
        this.oltpService.createOrder(new Order());
    }
}
