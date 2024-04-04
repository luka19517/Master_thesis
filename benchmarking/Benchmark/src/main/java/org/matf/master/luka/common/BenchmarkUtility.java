package org.matf.master.luka.common;

import java.io.IOException;
import java.sql.SQLException;

public interface BenchmarkUtility {

    void connect() throws SQLException, IOException;
    void populateDatabase();
    void executeOLTPBenchmark();
    void executeOLAPBenchmark();
    void close();

}
