package org.matf.master.luka;

import java.io.IOException;
import java.sql.SQLException;

public interface BenchmarkUtility {

    Object connect() throws IOException,SQLException;

    void close() throws IOException,SQLException;

}
