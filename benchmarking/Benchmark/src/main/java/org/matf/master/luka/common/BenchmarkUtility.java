package org.matf.master.luka.common;

import java.io.IOException;
import java.sql.SQLException;

public interface BenchmarkUtility {

    void makeConfig();
    void connect() throws SQLException, IOException;
    void close();

}
