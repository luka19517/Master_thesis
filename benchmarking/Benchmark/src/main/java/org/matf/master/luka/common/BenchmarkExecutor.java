package org.matf.master.luka.common;

public interface BenchmarkExecutor {


    default void executeOLTPWorkload(BenchmarkOLTPUtility oltpWorkloadUtility, long totalTransactions){

        //TODO here we insert implement OLTP workload

    }
}
