package org.matf.master.luka.common;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public interface OLTPService {

    void createUser(int seed, String username, String password, String email) throws SQLException, IOException;
    void authenticateUser(String username, String password);
    void createOrder(Order order);

}
