package org.matf.master.luka.common;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.SQLException;

@Data
public class OLTPWorkload {

    private OLTPService oltpService;

    public OLTPWorkload(OLTPService oltpService){
        this.oltpService = oltpService;
    }

    public void executeWorkload() throws SQLException {
        long startMillis = System.currentTimeMillis();
        for(int i=0;i<10;i++){
            executeWorkflow(i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Workload lasted for: "+((end-startMillis)/1000.0)+" seconds" );
    }

    private void executeWorkflow(int seed) throws SQLException {
        oltpService.createUser("username_"+seed, "password_"+seed,"email_"+seed);
    }
}
