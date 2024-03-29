package org.matf.master.luka.common;

import java.sql.SQLException;

public interface BenchmarkUtility {

    void makeConfig();
    void connect() throws SQLException;
    void close();

}
