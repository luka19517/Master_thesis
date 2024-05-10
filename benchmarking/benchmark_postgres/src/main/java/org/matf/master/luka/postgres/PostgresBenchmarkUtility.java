package org.matf.master.luka.postgres;

import org.matf.master.luka.BenchmarkUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class PostgresBenchmarkUtility implements BenchmarkUtility {

    public static Connection postgresSQLDriverConnection;

    @Override
    public void connect() throws SQLException {

        String url = "jdbc:postgresql://localhost:5432/postgresdb";
        Properties props = new Properties();
        props.setProperty("user", "postgres");
        props.setProperty("password", "postgres");
        postgresSQLDriverConnection = DriverManager.getConnection(url, props);
        postgresSQLDriverConnection.setAutoCommit(false);
    }

    @Override
    public void close() {

    }
}
