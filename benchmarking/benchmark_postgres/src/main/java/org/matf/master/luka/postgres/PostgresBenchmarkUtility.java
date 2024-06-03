package org.matf.master.luka.postgres;

import org.matf.master.luka.BenchmarkUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresBenchmarkUtility implements BenchmarkUtility {


    @Override
    public Object connect() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgresdb";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        Connection postgresConnection = DriverManager.getConnection(url, props);
        postgresConnection.setAutoCommit(false);
        return postgresConnection;
    }

    @Override
    public void close() {
    }
}
