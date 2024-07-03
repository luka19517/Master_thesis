package org.matf.master.luka;

import org.matf.master.luka.hbase.HBaseBenchmarkExecutor;
import org.matf.master.luka.hbase.HBaseBenchmarkOLAPUtility;
import org.matf.master.luka.hbase.HBaseBenchmarkUtility;
import org.matf.master.olap.benchmark.BenchmarkExecutor;
import org.matf.master.olap.benchmark.BenchmarkOLAPUtility;
import org.matf.master.olap.benchmark.BenchmarkUtility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Enter number of iterations: ");
        Scanner transactionNumInput = new Scanner( System.in );
        int numOfTransactions = transactionNumInput.nextInt();

        System.out.println("Enter number of clients");
        Scanner clientNumInput = new Scanner(System.in);
        int numOfClients = clientNumInput.nextInt();

        BenchmarkUtility benchmarkUtility = new HBaseBenchmarkUtility();
        BenchmarkOLAPUtility benchmarkOLTPUtility = new HBaseBenchmarkOLAPUtility();
        BenchmarkExecutor benchmarkExecutor = new HBaseBenchmarkExecutor();

        System.out.println("Bulk load start");
        benchmarkExecutor.executeBulkLoad(benchmarkUtility,benchmarkOLTPUtility);
        System.out.println("Bulk load end");

        System.out.println("Workload start");
        benchmarkExecutor.executeOLAPWorkload(benchmarkUtility,benchmarkOLTPUtility, numOfTransactions,numOfClients);
        System.out.println("Workload end");

    }
}