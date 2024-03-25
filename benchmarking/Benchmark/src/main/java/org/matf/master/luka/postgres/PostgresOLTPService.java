package org.matf.master.luka.postgres;

import org.matf.master.luka.common.OLTPService;
import org.matf.master.luka.common.Order;

public class PostgresOLTPService implements OLTPService {
    @Override
    public void authenticateUser(String username, String password) {
        System.out.println("POSTGRES ---- Authenticate");
    }

    @Override
    public void createOrder(Order order) {
        System.out.println("POSTGRES ---- Create order");
    }
}
