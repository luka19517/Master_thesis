package org.matf.master.luka;

import org.matf.master.luka.postgres.PostgresBenchmarkExecutor;
import org.matf.master.luka.postgres.PostgresBenchmarkOLTPUtility;
import org.matf.master.luka.postgres.PostgresBenchmarkUtility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        System.out.println("Enter number of transactions: ");
        Scanner transactionNumInput = new Scanner( System.in );
        int numOfTransactions = transactionNumInput.nextInt();

        System.out.println("Enter number of clients");
        Scanner clientNumInput = new Scanner(System.in);
        int numOfClients = clientNumInput.nextInt();

        BenchmarkUtility benchmarkUtility = new PostgresBenchmarkUtility();
        BenchmarkOLTPUtility benchmarkOLTPUtility = new PostgresBenchmarkOLTPUtility();
        BenchmarkExecutor benchmarkExecutor = new PostgresBenchmarkExecutor();

        System.out.println("Workload start");
        benchmarkExecutor.executeOLTPWorkload(benchmarkUtility,benchmarkOLTPUtility, numOfTransactions,numOfClients);
        System.out.println("Workload end");

        System.out.println("Random access start");
        benchmarkExecutor.executeRandomAccessTest(benchmarkUtility,benchmarkOLTPUtility, numOfTransactions);
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