package org.matf.master.luka.postgres;

import org.matf.master.luka.common.OLTPService;
import org.matf.master.luka.common.Order;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PostgresOLTPService implements OLTPService {


    @Override
    public void createUser(String username, String password, String email) throws SQLException {
        PreparedStatement createUserStatement = PostgresUtility.postgresSQLDriverConnection.prepareStatement("""
                INSERT INTO postgresdb.FXUSER(username, password, email, created_on)
                VALUES(?,?,?, now() at time zone 'utc');
                """);
        createUserStatement.setString(1, username);
        createUserStatement.setString(2, password);
        createUserStatement.setString(3, email);
        createUserStatement.execute();
    }

    @Override
    public void authenticateUser(String username, String password) {
        System.out.println("POSTGRES ---- Authenticate");
    }

    @Override
    public void createOrder(Order order) {
        System.out.println("POSTGRES ---- Create order");
    }
}
