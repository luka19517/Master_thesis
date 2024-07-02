package org.matf.master.olap.benchmark;

import java.sql.SQLException;

public interface BenchmarkOLAPUtility {

    void bulkLoad(Object connection) throws SQLException;

    void executeQuery1(Object connection) throws SQLException;

    void executeQuery2(Object connection) throws SQLException;

    void executeQuery3(Object connection);

}
