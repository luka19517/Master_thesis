package org.matf.master.olap.benchmark;

import java.io.IOException;
import java.sql.SQLException;

public interface BenchmarkUtility {

    Object connect() throws SQLException, IOException;

}
