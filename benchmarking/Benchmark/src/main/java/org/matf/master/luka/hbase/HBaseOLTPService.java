package org.matf.master.luka.hbase;

import org.matf.master.luka.common.OLTPService;
import org.matf.master.luka.common.Order;

public class HBaseOLTPService implements OLTPService {

    @Override
    public void authenticateUser(String username, String password) {
        System.out.println("HBASE ---- Authenticate");
    }

    @Override
    public void createOrder(Order order) {
        System.out.println("HBASE ---- Create order");
    }
}
