package org.matf.master.luka;

import org.matf.master.luka.common.BenchmarkExecutor;
import org.matf.master.luka.hbase.HBaseBenchmarkExecutor;
import org.matf.master.luka.postgres.PostgresBenchmarkExecutor;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Choose benchmark option [Postgres-1, HBase-2]:");
        int option =  sc.nextInt();

        BenchmarkExecutor benchmarkExecutor;
        if(option==1){
            benchmarkExecutor = new PostgresBenchmarkExecutor();
        }
        else{
            benchmarkExecutor = new HBaseBenchmarkExecutor();
        }
        benchmarkExecutor.execute();
    }
}