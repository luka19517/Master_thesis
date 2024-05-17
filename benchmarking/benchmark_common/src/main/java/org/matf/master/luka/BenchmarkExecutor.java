package org.matf.master.luka;

import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.io.IOException;
import java.sql.SQLException;

public interface BenchmarkExecutor {


    default void executeOLTPWorkload(BenchmarkOLTPUtility oltpWorkloadUtility, long totalTransactions) throws SQLException, IOException {

        long totalCreateFXTransaction = 0;
        long totalExecutePayment = 0;
        long totalCheckTransactionStatus = 0;

        oltpWorkloadUtility.testConsistency();

        for (int i = 0; i < totalTransactions; i++) {
            FXTransaction fxTransaction = DataOnDemandUtility.createFXTransaction(i);
            long createFXTransactionStart = System.currentTimeMillis();
            ExecutePaymentInfo executePaymentInfo = oltpWorkloadUtility.createFXTransaction(fxTransaction);
            long createFXTransactionFinish = System.currentTimeMillis();
            long executePaymentStart = System.currentTimeMillis();
            oltpWorkloadUtility.executePayment(executePaymentInfo);
            long executePaymentFinish = System.currentTimeMillis();
            oltpWorkloadUtility.testConsistency();
            long checkTransactionStatusStart = System.currentTimeMillis();
            oltpWorkloadUtility.checkTransactionStatus(fxTransaction);
            long checkTransactionStatusFinish = System.currentTimeMillis();


            totalCreateFXTransaction += createFXTransactionFinish - createFXTransactionStart;
            totalExecutePayment += executePaymentFinish - executePaymentStart;
            totalCheckTransactionStatus += checkTransactionStatusFinish - checkTransactionStatusStart;

        }

        System.err.println("Total create fx transaction: " + totalCreateFXTransaction);
        System.err.println("Total execute payment: " + totalExecutePayment);
        System.err.println("Total check transaction status: " + totalCheckTransactionStatus);

        long totalDuration = totalCreateFXTransaction + totalExecutePayment + totalCheckTransactionStatus;
        System.err.println("Total benchmark duration: " + totalDuration);
    }

}
