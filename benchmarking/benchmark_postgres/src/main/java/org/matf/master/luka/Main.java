package org.matf.master.luka;

import org.matf.master.luka.postgres.PostgresBenchmarkExecutor;
import org.matf.master.luka.postgres.PostgresBenchmarkOLTPUtility;
import org.matf.master.luka.postgres.PostgresBenchmarkUtility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {

        BenchmarkUtility benchmarkUtility = new PostgresBenchmarkUtility();
        BenchmarkOLTPUtility benchmarkOLTPUtility = new PostgresBenchmarkOLTPUtility();
        BenchmarkExecutor benchmarkExecutor = new PostgresBenchmarkExecutor();

        System.out.println("Connect start");
        benchmarkUtility.connect();
        System.out.println("Connection retrieved successfully");

        System.out.println("Workload start");
        benchmarkExecutor.executeOLTPWorkload(benchmarkOLTPUtility, 10);
        System.out.println("Workload end");

        System.out.println("Close start");
        benchmarkUtility.close();
        System.out.println("Close end");

    }
}