package org.mr17195;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;

public class Main {
    public static void main(String[] args) {
        Configuration config = HBaseConfiguration.create();



        System.out.println("Hello world!");
    }
}