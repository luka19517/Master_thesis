package org.matf.master.luka;

import org.matf.master.luka.postgres.PostgresBenchmarkExecutor;
import org.matf.master.luka.postgres.PostgresBenchmarkOLAPUtility;
import org.matf.master.luka.postgres.PostgresBenchmarkUtility;
import org.matf.master.olap.benchmark.BenchmarkExecutor;
import org.matf.master.olap.benchmark.BenchmarkOLAPUtility;
import org.matf.master.olap.benchmark.BenchmarkUtility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, IOException, InterruptedException {
        System.out.println("Enter number of iterations: ");
        Scanner transactionNumInput = new Scanner( System.in );
        int numOfTransactions = transactionNumInput.nextInt();

        System.out.println("Enter number of clients");
        Scanner clientNumInput = new Scanner(System.in);
        int numOfClients = clientNumInput.nextInt();

        BenchmarkUtility benchmarkUtility = new PostgresBenchmarkUtility();
        BenchmarkOLAPUtility benchmarkOLTPUtility = new PostgresBenchmarkOLAPUtility();
        BenchmarkExecutor benchmarkExecutor = new PostgresBenchmarkExecutor();

        System.out.println("Bulk load start");
        benchmarkExecutor.executeBulkLoad(benchmarkUtility,benchmarkOLTPUtility);
        System.out.println("Bulk load end");

        System.out.println("Workload start");
        benchmarkExecutor.executeOLAPWorkload(benchmarkUtility,benchmarkOLTPUtility, numOfTransactions,numOfClients);
        System.out.println("Workload end");

    }
}