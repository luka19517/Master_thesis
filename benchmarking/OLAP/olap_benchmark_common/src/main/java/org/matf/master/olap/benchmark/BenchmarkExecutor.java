package org.matf.master.olap.benchmark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public interface BenchmarkExecutor {

    default void executeBulkLoad(BenchmarkUtility benchmarkUtility,BenchmarkOLAPUtility olapUtility) throws Exception {
        long bulkLoadStart = System.currentTimeMillis();
        olapUtility.bulkLoad(benchmarkUtility.connect());
        long bulkLoadEnd = System.currentTimeMillis();
        System.out.println("Bulk load duration: "+(bulkLoadEnd-bulkLoadStart));
    }
    default void executeOLAPWorkload(BenchmarkUtility benchmarkUtility,BenchmarkOLAPUtility olapUtility,int totalIterations, int numOfClients) throws SQLException, IOException, InterruptedException {
        List<Integer> iterationsPerClientList = new ArrayList<>();
        int iterationsToAssign = totalIterations;
        int iterationsPerClient = iterationsToAssign /numOfClients;
        for (int i = 0 ; i<numOfClients;i++){
            iterationsPerClientList.add(iterationsPerClient);
            iterationsToAssign-=iterationsPerClient;
        }

        if(iterationsToAssign>0){
            int iterationsForLastClient = iterationsPerClientList.get(numOfClients-1);
            iterationsPerClientList.set(numOfClients-1,iterationsForLastClient+iterationsToAssign);
        }

        assert numOfClients==iterationsPerClientList.size();

        Thread[] threads = new Thread[numOfClients];
        CountDownLatch latch = new CountDownLatch(numOfClients);

        for(int i=0;i<numOfClients;i++){
            threads[i] = new Thread(new BenchmarkSingleClientExecutor(benchmarkUtility,olapUtility,i*iterationsPerClientList.get(i),iterationsPerClientList.get(i),latch));
        }

        long startTimestamp = System.currentTimeMillis();
        for(int i = 0;i<numOfClients;i++){
            threads[i].start();
        }
        latch.await();
        long endTimestamp = System.currentTimeMillis();
        System.err.println("Total benchmark duration: " + (endTimestamp-startTimestamp));
    }

    default void executeOLAPWorkloadFaster(BenchmarkUtility benchmarkUtility,BenchmarkOLAPUtility olapUtility,int totalIterations, int numOfClients) throws SQLException, IOException, InterruptedException {
        List<Integer> iterationsPerClientList = new ArrayList<>();
        int iterationsToAssign = totalIterations;
        int iterationsPerClient = iterationsToAssign /numOfClients;
        for (int i = 0 ; i<numOfClients;i++){
            iterationsPerClientList.add(iterationsPerClient);
            iterationsToAssign-=iterationsPerClient;
        }

        if(iterationsToAssign>0){
            int iterationsForLastClient = iterationsPerClientList.get(numOfClients-1);
            iterationsPerClientList.set(numOfClients-1,iterationsForLastClient+iterationsToAssign);
        }

        assert numOfClients==iterationsPerClientList.size();

        Thread[] threads = new Thread[numOfClients];
        CountDownLatch latch = new CountDownLatch(numOfClients);

        for(int i=0;i<numOfClients;i++){
            threads[i] = new Thread(new BenchmarkSingleClientExecutorFaster(benchmarkUtility,olapUtility,i*iterationsPerClientList.get(i),iterationsPerClientList.get(i),latch));
        }

        long startTimestamp = System.currentTimeMillis();
        for(int i = 0;i<numOfClients;i++){
            threads[i].start();
        }
        latch.await();
        long endTimestamp = System.currentTimeMillis();
        System.err.println("Total modified benchmark duration: " + (endTimestamp-startTimestamp));
    }

}
