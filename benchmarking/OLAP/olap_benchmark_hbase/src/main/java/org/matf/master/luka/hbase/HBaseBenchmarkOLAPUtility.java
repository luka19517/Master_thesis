package org.matf.master.luka.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.mapreduce.ImportTsv;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.matf.master.olap.benchmark.BenchmarkOLAPUtility;
import org.matf.master.olap.benchmark.BenchmarkUtility;

import java.sql.SQLException;

public class HBaseBenchmarkOLAPUtility implements BenchmarkOLAPUtility {

    @Override
    public void bulkLoad(Object connection) throws Exception {

        Configuration config = ((Connection)connection).getConfiguration();

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:brand,data:type,data:size,data:container,data:price,data:comment");
        Job productJob = ImportTsv.createSubmittableJob(config,new String[]{"product", "product.tsv"});
        productJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:address,data:phone");
        Job supplierJob = ImportTsv.createSubmittableJob(config,new String[]{"supplier", "supplier.tsv"});
        supplierJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:product,data:supplier,data:available,data:supply_cost,data:comment");
        Job productSupplierJob = ImportTsv.createSubmittableJob(config,new String[]{"productsupplier", "productsupplier.tsv"});
        productSupplierJob.waitForCompletion(true);

//        Admin admin = ((Connection)connection).getAdmin();
//        TableName productTable = TableName.valueOf("product");
//        TableName supplierTable = TableName.valueOf("supplier");
//        TableName productSupplierTable = TableName.valueOf("productsupplier");
//        TableName customerTable = TableName.valueOf("customer");
//        TableName orderTable = TableName.valueOf("order");
//        TableName orderItemTable = TableName.valueOf("order_item");
//
//        Table product= ((Connection)connection).getTable(productTable);
//        for(int i=0;i<200000;i++){
//            String name = "name_"+i;
//            String brand = "brand_"+i;
//            String type = "type_"+i;
//            int size = i%200;
//            String container = "container_"+i;
//            BigDecimal price = new BigDecimal("1000");
//            String comment = "comment_"+i;
//
//            Put put = new Put(Bytes.toBytes(name));
//            put.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("name"),Bytes.toBytes(name));
//            put.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("brand"),Bytes.toBytes(brand));
//            put.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("type"),Bytes.toBytes(type));
//            put.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("size"),Bytes.toBytes(size));
//            put.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("container"),Bytes.toBytes(container));
//            put.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("price"),Bytes.toBytes(price));
//            put.addColumn(Bytes.toBytes("cf1"),Bytes.toBytes("comment"),Bytes.toBytes(comment));
//
//
//            product.put(put);
//        }

    }

    @Override
    public void executeQuery1(Object o) throws SQLException {

    }

    @Override
    public void executeQuery2(Object o) throws SQLException {

    }

    @Override
    public void executeQuery3(Object o) {

    }
}
