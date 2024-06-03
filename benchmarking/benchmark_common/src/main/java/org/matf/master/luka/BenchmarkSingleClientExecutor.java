package org.matf.master.luka;

import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

public class BenchmarkSingleClientExecutor implements Runnable {

    private final CountDownLatch endSignal;
    private final BenchmarkOLTPUtility benchmarkOLTPUtility;
    private final int numOfTransactions;
    private final int startFrom;

    private final Object connection;

    public BenchmarkSingleClientExecutor(BenchmarkUtility benchmarkUtility, BenchmarkOLTPUtility oltpWorkloadUtility, int startFrom, int numOfTransactions, CountDownLatch countDownLatch) throws SQLException, IOException {
        this.benchmarkOLTPUtility = oltpWorkloadUtility;
        this.startFrom = startFrom;
        this.numOfTransactions = numOfTransactions;
        this.endSignal = countDownLatch;
        this.connection = benchmarkUtility.connect();
    }

    @Override
    public void run() {

        try {
            for (int i = this.startFrom; i < this.startFrom + this.numOfTransactions; i++) {
                FXTransaction fxTransaction = DataOnDemandUtility.createFXTransaction(i);
                ExecutePaymentInfo executePaymentInfo = this.benchmarkOLTPUtility.createFXTransaction(connection,fxTransaction);
                this.benchmarkOLTPUtility.executePayment(connection,executePaymentInfo);
                //oltpWorkloadUtility.testConsistency();
                String status = benchmarkOLTPUtility.checkTransactionStatus(connection,fxTransaction);
            }
            endSignal.countDown();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
