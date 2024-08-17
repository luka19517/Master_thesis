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
        Long totalCreate = 0L;
        Long totalExecute = 0L;
        Long totalCheck = 0L;
        try {
            for (int i = this.startFrom; i < this.startFrom + this.numOfTransactions; i++) {
                FXTransaction fxTransaction = DataOnDemandUtility.createFXTransaction(i);
                Long start=System.currentTimeMillis();
                ExecutePaymentInfo executePaymentInfo = this.benchmarkOLTPUtility.createFXTransaction(connection,fxTransaction);
                Long end=System.currentTimeMillis();
                totalCreate +=(end-start);
                start = System.currentTimeMillis();
                this.benchmarkOLTPUtility.executePayment(connection,executePaymentInfo);
                end = System.currentTimeMillis();
                totalExecute+=(end-start);
                start = System.currentTimeMillis();
                String status = benchmarkOLTPUtility.checkTransactionStatus(connection,fxTransaction);
                end = System.currentTimeMillis();
                totalCheck +=(end-start);
            }
            endSignal.countDown();
            System.out.println("Create transaction: "+totalCreate);
            System.out.println("Execute payment: "+totalExecute);
            System.out.println("Execute payment: "+totalCheck);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
