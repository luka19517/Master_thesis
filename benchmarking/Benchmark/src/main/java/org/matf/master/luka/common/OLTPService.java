package org.matf.master.luka.common;

import java.sql.SQLException;
import java.util.Date;

public interface OLTPService {

    void createUser(String username, String password, String email) throws SQLException;
    void authenticateUser(String username, String password);
    void createOrder(Order order);

}
