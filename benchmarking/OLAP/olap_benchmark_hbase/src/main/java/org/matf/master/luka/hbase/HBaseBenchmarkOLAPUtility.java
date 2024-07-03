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

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:address,data:phone,data:comment");
        Job customerJob = ImportTsv.createSubmittableJob(config,new String[]{"customer", "customer.tsv"});
        customerJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:customer,data:status,data:total_price,data:entry_date,data:priority,data:comment");
        Job orderJob = ImportTsv.createSubmittableJob(config,new String[]{"order", "order.tsv"});
        orderJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:order,data:product,data:supplier,data:orderNo,data:quantity,data:basePrice,data:discount,data:tax,data:status,data:shipDate,data:commitDate,data:comment");
        Job orderItemJob = ImportTsv.createSubmittableJob(config,new String[]{"orderitem", "orderitem.tsv"});
        orderItemJob.waitForCompletion(true);

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
