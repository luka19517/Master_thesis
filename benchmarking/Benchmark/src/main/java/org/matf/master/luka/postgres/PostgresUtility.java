package org.matf.master.luka.postgres;

import org.matf.master.luka.common.BenchmarkUtility;

import java.sql.*;
import java.util.Properties;

public class PostgresUtility implements BenchmarkUtility {

    public static Connection postgresSQLDriverConnection;

    @Override
    public void makeConfig() {

    }

    @Override
    public void connect() throws SQLException {

        String url="jdbc:postgresql://localhost:5433/postgresdb";
        Properties props = new Properties();
        props.setProperty("user","postgres");
        props.setProperty("password","postgres");
        postgresSQLDriverConnection = DriverManager.getConnection(url,props);

    }
    @Override
    public void close() {

    }
}
