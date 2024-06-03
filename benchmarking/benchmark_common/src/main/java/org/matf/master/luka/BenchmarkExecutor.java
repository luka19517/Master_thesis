package org.matf.master.luka;

import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface BenchmarkExecutor{


    default void executeOLTPWorkload(BenchmarkUtility benchmarkUtility, BenchmarkOLTPUtility oltpWorkloadUtility, int totalTransactions, int numOfClients) throws SQLException, IOException, InterruptedException {

        List<Integer> transactionsPerClientList = new ArrayList<>();
        int transactionsToAssign = totalTransactions;
        int transactionsPerClient = transactionsToAssign / numOfClients;

        for(int i = 0; i<numOfClients;i++){
            transactionsPerClientList.add(transactionsPerClient);
            transactionsToAssign-=transactionsPerClient;
        }
        if (transactionsToAssign > 0) {
            int transactionForLastClient = transactionsPerClientList.get(numOfClients - 1);
            transactionsPerClientList.set(numOfClients - 1, transactionForLastClient + transactionsToAssign);
        }

        assert numOfClients==transactionsPerClientList.size();

        Thread[] threads = new Thread[numOfClients];
        CountDownLatch latch = new CountDownLatch(numOfClients);
        for(int i = 0;i<numOfClients;i++){
            threads[i] = new Thread(new BenchmarkSingleClientExecutor(benchmarkUtility,oltpWorkloadUtility,i*transactionsPerClientList.get(i),transactionsPerClientList.get(i),latch));
        }


        long startTimestamp = System.currentTimeMillis();
        for(int i = 0;i<numOfClients;i++){
            threads[i].start();
        }
        latch.await();
        long endTimestamp = System.currentTimeMillis();
        System.err.println("Total benchmark duration: " + (endTimestamp-startTimestamp));
    }


}
