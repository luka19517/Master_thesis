package org.matf.master.olap.benchmark;

import java.io.IOException;
import java.sql.SQLException;

public interface BenchmarkOLAPUtility {

    void bulkLoad(Object connection) throws Exception;

    void executeQuery1(Object connection) throws Throwable;

    void executeQuery2(Object connection) throws IOException,SQLException;

    void executeQuery3(Object connection) throws IOException,SQLException;

}
