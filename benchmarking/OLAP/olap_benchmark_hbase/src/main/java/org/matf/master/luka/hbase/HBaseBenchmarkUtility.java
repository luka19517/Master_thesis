package org.matf.master.luka.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.matf.master.olap.benchmark.BenchmarkUtility;

import java.io.IOException;

public class HBaseBenchmarkUtility implements BenchmarkUtility {
    @Override
    public Object connect() throws IOException {
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.master", "localhost:16010");
        config.set("hbase.zookeeper.quorum", "localhost:2181");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        return ConnectionFactory.createConnection(config);
    }
}
