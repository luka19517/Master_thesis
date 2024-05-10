package org.matf.master.luka;

import java.io.IOException;
import java.sql.SQLException;

public interface BenchmarkUtility {

    void connect() throws SQLException, IOException;
    void close() throws IOException;

}
