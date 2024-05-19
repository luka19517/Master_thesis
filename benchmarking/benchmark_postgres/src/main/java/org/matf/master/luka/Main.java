package org.matf.master.luka;

import org.matf.master.luka.postgres.PostgresBenchmarkExecutor;
import org.matf.master.luka.postgres.PostgresBenchmarkOLTPUtility;
import org.matf.master.luka.postgres.PostgresBenchmarkUtility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {

        System.out.println("Enter number of transactions: ");
        Scanner transactionNumInput = new Scanner( System.in );
        int numOfTransactions = transactionNumInput.nextInt();

        BenchmarkUtility benchmarkUtility = new PostgresBenchmarkUtility();
        BenchmarkOLTPUtility benchmarkOLTPUtility = new PostgresBenchmarkOLTPUtility();
        BenchmarkExecutor benchmarkExecutor = new PostgresBenchmarkExecutor();

        System.out.println("Connect start");
        benchmarkUtility.connect();
        System.out.println("Connection retrieved successfully");

        System.out.println("Workload start");
        benchmarkExecutor.executeOLTPWorkload(benchmarkOLTPUtility, numOfTransactions);
        System.out.println("Workload end");

        System.out.println("Close start");
        benchmarkUtility.close();
        System.out.println("Close end");

    }
}