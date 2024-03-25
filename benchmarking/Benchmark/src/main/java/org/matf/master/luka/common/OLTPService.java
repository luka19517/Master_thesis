package org.matf.master.luka.common;

public interface OLTPService {

    void authenticateUser(String username, String password);
    void createOrder(Order order);

}
