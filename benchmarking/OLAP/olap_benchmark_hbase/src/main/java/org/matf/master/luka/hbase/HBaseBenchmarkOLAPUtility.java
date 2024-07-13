package org.matf.master.luka.hbase;

import org.apache.commons.el.IntegerLiteral;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.client.coprocessor.AggregationClient;
import org.apache.hadoop.hbase.client.coprocessor.BigDecimalColumnInterpreter;
import org.apache.hadoop.hbase.client.coprocessor.LongColumnInterpreter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.mapreduce.ImportTsv;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;
import org.matf.master.olap.benchmark.BenchmarkOLAPUtility;
import org.matf.master.olap.benchmark.BenchmarkUtility;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;

public class HBaseBenchmarkOLAPUtility implements BenchmarkOLAPUtility {

    @Override
    public void bulkLoad(Object connection) throws Exception {

        Configuration config = ((Connection)connection).getConfiguration();

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:brand,data:type,data:size,data:container,data:price,data:comment");
        Job productJob = ImportTsv.createSubmittableJob(config,new String[]{"product", "product.csv"});
        productJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:address,data:phone");
        Job supplierJob = ImportTsv.createSubmittableJob(config,new String[]{"supplier", "supplier.csv"});
        supplierJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:product,data:supplier,data:available,data:supply_cost,data:comment");
        Job productSupplierJob = ImportTsv.createSubmittableJob(config,new String[]{"productsupplier", "productsupplier.csv"});
        productSupplierJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:address,data:phone,data:comment");
        Job customerJob = ImportTsv.createSubmittableJob(config,new String[]{"customer", "customer.csv"});
        customerJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:customer,data:status,data:total_price,data:entry_date,data:priority,data:comment");
        Job orderJob = ImportTsv.createSubmittableJob(config,new String[]{"order", "order.csv"});
        orderJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:order,data:product,data:supplier,data:orderNo,data:quantity,data:basePrice,data:discount,data:tax,data:status,data:shipDate,data:commitDate,data:comment,stats:q01072024,stats:q02072024,stats:q03072024,stats:q04072024,stats:q05072024,stats:q06072024,stats:q07072024");
        Job orderItemJob = ImportTsv.createSubmittableJob(config,new String[]{"orderitem", "orderitem.csv"});
        orderItemJob.waitForCompletion(true);

    }

    @Override
    public void executeQuery1(Object connection) throws Throwable {

        Table orderItem = ((Connection )connection).getTable(TableName.valueOf("orderitem"));
        //AggregationClient aggClient = new AggregationClient(((Connection) connection).getConfiguration());
        Scan orderItemScan = new Scan();

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes("status_1"));
        orderItemScan.setFilter(prefixFilter);
        orderItemScan.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("q07072024"));
        ResultScanner orderItemScanResult = orderItem.getScanner(orderItemScan);
//        Long sum = aggClient.sum(orderItem,new LongColumnInterpreter(),orderItemScan);
//
        int sum = 0;
        for (Result res : orderItemScanResult) {
            sum += Integer.parseInt(Bytes.toString(res.getValue(Bytes.toBytes("stats"), Bytes.toBytes("q07072024"))));
        }
        System.out.println("Sum for status0: "+sum);

//
//        prefixFilter = new PrefixFilter(Bytes.toBytes("status_1"));
//        orderItemScan.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("q07072024"));
//        orderItemScan.setFilter(prefixFilter);
//        orderItemScanResult = orderItem.getScanner(orderItemScan);
//        sum = 0;
//        for (Result res : orderItemScanResult) {
//            sum += Integer.parseInt(Bytes.toString(res.getValue(Bytes.toBytes("stats"), Bytes.toBytes("q07072024"))));
//        }
//        System.out.println("Sum for status1: "+sum);
//
//        prefixFilter = new PrefixFilter(Bytes.toBytes("status_2"));
//        orderItemScan.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("q07072024"));
//        orderItemScan.setFilter(prefixFilter);
//        orderItemScanResult = orderItem.getScanner(orderItemScan);
//        sum = 0;
//        for (Result res : orderItemScanResult) {
//            sum += Integer.parseInt(Bytes.toString(res.getValue(Bytes.toBytes("stats"), Bytes.toBytes("q07072024"))));
//        }
//        orderItemScanResult.close();
//        System.out.println("Sum for status2: "+sum);


    }

    @Override
    public void executeQuery2(Object o) throws SQLException {

    }

    @Override
    public void executeQuery3(Object o) {

    }
}
