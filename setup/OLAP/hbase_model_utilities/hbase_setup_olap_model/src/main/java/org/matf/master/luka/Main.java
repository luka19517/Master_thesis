package org.matf.master.luka;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException {

        Configuration config = HBaseConfiguration.create();
        config.set("hbase.master", "localhost:16010");
        config.set("hbase.zookeeper.quorum","localhost:2181");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        Connection hbaseConnection = ConnectionFactory.createConnection(config);

        Admin admin = hbaseConnection.getAdmin();
        TableName productTable = TableName.valueOf("product");
        TableName supplierTable = TableName.valueOf("supplier");
        TableName productSupplierTable = TableName.valueOf("productsupplier");
        TableName customerTable = TableName.valueOf("customer");
        TableName orderTable = TableName.valueOf("order");
        TableName orderItemTable = TableName.valueOf("orderitem");
        TableName orderItemStatsTable = TableName.valueOf("orderitemstats");


        HTableDescriptor productDescriptor = new HTableDescriptor(productTable);
        HTableDescriptor supplierDescriptor = new HTableDescriptor(supplierTable);
        HTableDescriptor productSupplierDescriptor = new HTableDescriptor(productSupplierTable);
        HTableDescriptor customerDescriptor = new HTableDescriptor(customerTable);
        HTableDescriptor orderDescriptor = new HTableDescriptor(orderTable);
        HTableDescriptor orderItemDescriptor = new HTableDescriptor(orderItemTable);
        HTableDescriptor orderItemStatsDescriptor = new HTableDescriptor(orderItemStatsTable);

        //------------------------------PRODUCT-----------------------------

        productDescriptor.addFamily(new HColumnDescriptor("data"));
        admin.createTable(productDescriptor);

        //------------------------------SUPPLIER-----------------------------
        supplierDescriptor.addFamily(new HColumnDescriptor("data"));
        admin.createTable(supplierDescriptor);


        //------------------------------PRODUCTSUPPLIER-----------------------------
        productSupplierDescriptor.addFamily(new HColumnDescriptor("data"));
        admin.createTable(productSupplierDescriptor);


        //------------------------------CUSTOMER-----------------------------
        customerDescriptor.addFamily(new HColumnDescriptor("data"));
        admin.createTable(customerDescriptor);

        //------------------------------ORDER-----------------------------
        orderDescriptor.addFamily(new HColumnDescriptor("data"));
        admin.createTable(orderDescriptor);

        //------------------------------ORDER_ITEM-----------------------------
        orderItemDescriptor.addFamily(new HColumnDescriptor("data"));
        admin.createTable(orderItemDescriptor);

        //------------------------------ORDER_ITEM_STATS-----------------------------
        orderItemStatsDescriptor.addFamily(new HColumnDescriptor("data"));
        orderItemStatsDescriptor.addFamily(new HColumnDescriptor("stats"));
        admin.createTable(orderItemStatsDescriptor);

    }
}