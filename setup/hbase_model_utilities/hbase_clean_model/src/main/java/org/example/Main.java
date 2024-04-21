package org.example;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Configuration config = HBaseConfiguration.create();
        Connection hbaseConnection = ConnectionFactory.createConnection(config);

        Admin admin = hbaseConnection.getAdmin();

        TableName fxUserTable = TableName.valueOf("fxuser");
        TableName fxAccountsTable = TableName.valueOf("fxaccounts");
        TableName fxTransactionTable = TableName.valueOf("fxtransaction");
        TableName fxRatesTable = TableName.valueOf("fxrates");

        admin.disableTable(fxUserTable);
        admin.disableTable(fxAccountsTable);
        admin.disableTable(fxTransactionTable);
        admin.disableTable(fxRatesTable);

        admin.deleteTable(fxUserTable);
        admin.deleteTable(fxAccountsTable);
        admin.deleteTable(fxTransactionTable);
        admin.deleteTable(fxRatesTable);

    }
}