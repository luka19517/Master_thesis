package org.matf.master.luka.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HTable;
import org.matf.master.luka.common.BenchmarkUtility;

import java.io.IOException;
import java.net.http.HttpClient;

public class HBaseUtility implements BenchmarkUtility {

    public static Connection hbaseConnection;
    private Configuration config;

    @Override
    public void makeConfig() {

        config = HBaseConfiguration.create();

    }

    @Override
    public void connect() throws IOException {
        hbaseConnection = ConnectionFactory.createConnection(config);
    }

    @Override
    public void close() {

    }
}
