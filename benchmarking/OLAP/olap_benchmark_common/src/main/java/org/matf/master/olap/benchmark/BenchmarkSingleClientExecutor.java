package org.matf.master.olap.benchmark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

public class BenchmarkSingleClientExecutor implements Runnable{

    private final CountDownLatch endSignal;
    private final BenchmarkOLAPUtility benchmarkOLAPUtility;
    private final int numOfTransactions;
    private final int startFrom;

    private final Object connection;

    public BenchmarkSingleClientExecutor(BenchmarkUtility benchmarkUtility, BenchmarkOLAPUtility oltpWorkloadUtility, int startFrom, int numOfTransactions, CountDownLatch countDownLatch) throws SQLException, IOException {
        this.benchmarkOLAPUtility = oltpWorkloadUtility;
        this.startFrom = startFrom;
        this.numOfTransactions = numOfTransactions;
        this.endSignal = countDownLatch;
        this.connection = benchmarkUtility.connect();
    }

    @Override
    public void run() {

        try {
            for (int i = this.startFrom; i < this.startFrom + this.numOfTransactions; i++) {
                benchmarkOLAPUtility.executeQuery1(connection);
                benchmarkOLAPUtility.executeQuery2(connection);
                benchmarkOLAPUtility.executeQuery3(connection);
            }
            endSignal.countDown();
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

}
