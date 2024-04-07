package org.matf.master.luka.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.matf.master.luka.common.BenchmarkUtility;

import java.io.IOException;

public class HBaseBenchmarkUtility implements BenchmarkUtility {

    public static Connection hbaseConnection;

    @Override
    public void connect() throws IOException {
        Configuration config = HBaseConfiguration.create();
        hbaseConnection = ConnectionFactory.createConnection(config);
    }

    @Override
    public void close() {

    }
}
