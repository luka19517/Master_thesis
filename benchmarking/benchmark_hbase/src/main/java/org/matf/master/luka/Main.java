package org.matf.master.luka;

import org.matf.master.luka.hbase.HBaseBenchmarkExecutor;
import org.matf.master.luka.hbase.HBaseBenchmarkOLTPUtility;
import org.matf.master.luka.hbase.HBaseBenchmarkUtility;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Enter number of transactions: ");
        Scanner transactionNumInput = new Scanner(System.in);
        int numOfTransactions = transactionNumInput.nextInt();

        System.out.println("Enter number of clients");
        Scanner clientNumInput = new Scanner(System.in);
        int numOfClients = clientNumInput.nextInt();

        BenchmarkUtility benchmarkUtility = new HBaseBenchmarkUtility();
        BenchmarkOLTPUtility benchmarkOLTPUtility = new HBaseBenchmarkOLTPUtility();
        BenchmarkExecutor benchmarkExecutor = new HBaseBenchmarkExecutor();
        System.out.println("Benchmark for HBASE...");


        System.out.println("Workload start");
        benchmarkExecutor.executeOLTPWorkload(benchmarkUtility, benchmarkOLTPUtility, numOfTransactions, numOfClients);
        System.out.println("Workload end");

        System.out.println("Random access start");
        benchmarkExecutor.executeRandomAccessTest(benchmarkUtility, benchmarkOLTPUtility, numOfTransactions);
        System.out.println("Random access end");

        System.out.println("Random access start");
        benchmarkExecutor.executeRandomAccessTest2(benchmarkUtility,benchmarkOLTPUtility);
        System.out.println("Random access end");

        System.out.println("Random access start");
        benchmarkExecutor.executeInsert(benchmarkUtility,benchmarkOLTPUtility);
        System.out.println("Random access end");


        System.out.println("Close start");
        benchmarkUtility.close();
        System.out.println("Close end");

    }
}