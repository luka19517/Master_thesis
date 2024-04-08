package org.matf.master.luka;

import org.matf.master.luka.common.BenchmarkExecutor;
import org.matf.master.luka.common.BenchmarkOLTPUtility;
import org.matf.master.luka.common.BenchmarkUtility;
import org.matf.master.luka.hbase.HBaseBenchmarkExecutor;
import org.matf.master.luka.hbase.HBaseBenchmarkOLTPUtility;
import org.matf.master.luka.hbase.HBaseBenchmarkUtility;
import org.matf.master.luka.postgres.PostgresBenchmarkExecutor;
import org.matf.master.luka.postgres.PostgresBenchmarkOLTPUtility;
import org.matf.master.luka.postgres.PostgresBenchmarkUtility;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose benchmark option [Postgres-1, HBase-2]:");
        int option = sc.nextInt();

        BenchmarkUtility benchmarkUtility;
        BenchmarkOLTPUtility benchmarkOLTPUtility;
        BenchmarkExecutor benchmarkExecutor;
        if (option == 1) {
            benchmarkUtility = new PostgresBenchmarkUtility();
            benchmarkOLTPUtility = new PostgresBenchmarkOLTPUtility();
            benchmarkExecutor = new PostgresBenchmarkExecutor();
            System.out.println("Benchmark for POSTGRES...");
        } else {
            benchmarkUtility = new HBaseBenchmarkUtility();
            benchmarkOLTPUtility = new HBaseBenchmarkOLTPUtility();
            benchmarkExecutor = new HBaseBenchmarkExecutor();
            System.out.println("Benchmark for HBASE...");
        }

        System.out.println("Connect start");
        benchmarkUtility.connect();
        System.out.println("Connection retrieved successfully");

        System.out.println("Workload start");
        benchmarkExecutor.executeOLTPWorkload(benchmarkOLTPUtility, 0);
        System.out.println("Workload end");

        System.out.println("Close start");
        benchmarkUtility.close();
        System.out.println("Close end");

    }
}