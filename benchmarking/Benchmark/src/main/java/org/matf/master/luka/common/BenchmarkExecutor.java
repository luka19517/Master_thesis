package org.matf.master.luka.common;

import org.matf.master.luka.common.model.FXTransaction;

import javax.xml.crypto.Data;

public interface BenchmarkExecutor {


    default void executeOLTPWorkload(BenchmarkOLTPUtility oltpWorkloadUtility, long totalTransactions){

        long totalCreateFXTransaction = 0;
        long totalExecutePayment = 0;
        long totalCheckTransactionStatus = 0;

        oltpWorkloadUtility.testConsistency();

        for(int i = 0 ; i<totalTransactions; i++){
            FXTransaction fxTransaction = DataOnDemandUtility.createFXTransaction(i);
            long createFXTransactionStart = System.currentTimeMillis();
            oltpWorkloadUtility.createFXTransaction(fxTransaction);
            long createFXTransactionFinish = System.currentTimeMillis();
            oltpWorkloadUtility.testConsistency();
            long executePaymentStart = System.currentTimeMillis();
            oltpWorkloadUtility.executePayment(fxTransaction);
            long executePaymentFinish = System.currentTimeMillis();
            oltpWorkloadUtility.testConsistency();
            long checkTransactionStatusStart = System.currentTimeMillis();
            oltpWorkloadUtility.checkTransactionStatus(fxTransaction);
            long checkTransactionStatusFinish = System.currentTimeMillis();

            totalCreateFXTransaction+=createFXTransactionFinish-createFXTransactionStart;
            totalExecutePayment+=executePaymentFinish-executePaymentStart;
            totalCheckTransactionStatus+=checkTransactionStatusFinish-checkTransactionStatusStart;
        }

        System.out.println("Total create fx transaction: "+totalCreateFXTransaction);
        System.out.println("Total execute payment: "+totalExecutePayment);
        System.out.println("Total check transaction status: "+totalCheckTransactionStatus);

        long totalDuration = totalCreateFXTransaction + totalExecutePayment + totalCheckTransactionStatus;
        System.out.println("Total benchmark duration: "+totalDuration);


    }

}
