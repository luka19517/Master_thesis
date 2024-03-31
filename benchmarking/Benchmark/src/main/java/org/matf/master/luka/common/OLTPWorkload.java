package org.matf.master.luka.common;

import lombok.Data;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

@Data
public class OLTPWorkload {

    private OLTPService oltpService;

    public OLTPWorkload(OLTPService oltpService){
        this.oltpService = oltpService;
    }

    public void executeWorkload() throws SQLException, IOException {
        long startMillis = System.currentTimeMillis();
        for(int i=0;i<100000;i++){
            executeWorkflow(i);
        }
        long end = System.currentTimeMillis();
        System.out.println("Workload lasted for: "+((end-startMillis)/1000.0)+" seconds" );
    }

    private void executeWorkflow(int seed) throws SQLException, IOException {
        oltpService.createUser(seed,"username_"+seed, "password_"+seed,"email_"+seed);
    }
}
