package org.matf.master.luka;

import org.matf.master.luka.common.BenchmarkUtility;
import org.matf.master.luka.common.OLTPWorkload;
import org.matf.master.luka.hbase.HBaseOLTPService;
import org.matf.master.luka.hbase.HBaseUtility;
import org.matf.master.luka.postgres.PostgresOLTPService;
import org.matf.master.luka.postgres.PostgresUtility;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose benchmark option [Postgres-1, HBase-2]:");
        int option =  sc.nextInt();

        BenchmarkUtility benchmarkUtility;
        OLTPWorkload oltpWorkload;
        if(option==1){
            benchmarkUtility = new PostgresUtility();
            oltpWorkload = new OLTPWorkload(new PostgresOLTPService());
            System.out.println("Benchmark for POSTGRES...");
        }
        else{
            benchmarkUtility = new HBaseUtility();
            oltpWorkload = new OLTPWorkload(new HBaseOLTPService());
            System.out.println("Benchmark for HBASE...");
        }

        System.out.println("Configuration start");
        benchmarkUtility.makeConfig();
        System.out.println("Configuration end");

        System.out.println("Connect start");
        benchmarkUtility.connect();
        System.out.println("Connect end");

        System.out.println("Workload start");
        oltpWorkload.executeWorkload();
        System.out.println("Workload end");

        System.out.println("Close start");
        benchmarkUtility.close();
        System.out.println("Close end");

    }
}