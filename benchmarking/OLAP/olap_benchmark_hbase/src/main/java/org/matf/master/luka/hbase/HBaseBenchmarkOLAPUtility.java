package org.matf.master.luka.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.mapreduce.ImportTsv;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.matf.master.olap.benchmark.BenchmarkOLAPUtility;

import java.sql.SQLException;

public class HBaseBenchmarkOLAPUtility implements BenchmarkOLAPUtility {

    @Override
    public void bulkLoad(Object connection) throws Exception {

        Configuration config = ((Connection) connection).getConfiguration();

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:brand,data:type,data:size,data:container,data:price,data:comment");
        Job productJob = ImportTsv.createSubmittableJob(config, new String[]{"product", "product.csv"});
        productJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:address,data:phone");
        Job supplierJob = ImportTsv.createSubmittableJob(config, new String[]{"supplier", "supplier.csv"});
        supplierJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:product,data:supplier,data:available,data:supply_cost,data:comment");
        Job productSupplierJob = ImportTsv.createSubmittableJob(config, new String[]{"productsupplier", "productsupplier.csv"});
        productSupplierJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:name,data:address,data:phone,data:comment");
        Job customerJob = ImportTsv.createSubmittableJob(config, new String[]{"customer", "customer.csv"});
        customerJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:customer,data:status,data:total_price,data:entry_date,data:priority,data:comment");
        Job orderJob = ImportTsv.createSubmittableJob(config, new String[]{"order", "order.csv"});
        orderJob.waitForCompletion(true);

        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:order,data:product,data:supplier,data:orderNo,data:quantity,data:basePrice,data:discount,data:tax,data:status,data:shipDate,data:commitDate,data:comment");
        Job orderItemJob = ImportTsv.createSubmittableJob(config, new String[]{"orderitem", "orderitem.csv"});
        orderItemJob.waitForCompletion(true);
        config.set("importtsv.separator", ",");
        config.set("importtsv.columns", "HBASE_ROW_KEY,data:status," +
                "stats:quantitySumOn01072024,stats:basePrice01072024,stats:sumDiscountPrice01072024,stats:sumChargePrice01072024,stats:avgQuantity01072024,stats:avgPrice01072024,stats:avgDiscount01072024," +
                "stats:quantitySumOn02072024,stats:basePrice02072024,stats:sumDiscountPrice02072024,stats:sumChargePrice02072024,stats:avgQuantity02072024,stats:avgPrice02072024,stats:avgDiscount02072024," +
                "stats:quantitySumOn03072024,stats:basePrice03072024,stats:sumDiscountPrice03072024,stats:sumChargePrice03072024,stats:avgQuantity03072024,stats:avgPrice03072024,stats:avgDiscount03072024," +
                "stats:quantitySumOn04072024,stats:basePrice04072024,stats:sumDiscountPrice04072024,stats:sumChargePrice04072024,stats:avgQuantity04072024,stats:avgPrice04072024,stats:avgDiscount04072024," +
                "stats:quantitySumOn05072024,stats:basePrice05072024,stats:sumDiscountPrice05072024,stats:sumChargePrice05072024,stats:avgQuantity05072024,stats:avgPrice05072024,stats:avgDiscount05072024," +
                "stats:quantitySumOn06072024,stats:basePrice06072024,stats:sumDiscountPrice06072024,stats:sumChargePrice06072024,stats:avgQuantity06072024,stats:avgPrice06072024,stats:avgDiscount06072024," +
                "stats:quantitySumOn07072024,stats:basePrice07072024,stats:sumDiscountPrice07072024,stats:sumChargePrice07072024,stats:avgQuantity07072024,stats:avgPrice07072024,stats:avgDiscount07072024");
        Job orderItemStatsJob = ImportTsv.createSubmittableJob(config, new String[]{"orderitemstats", "orderitemstats.csv"});
        orderItemStatsJob.waitForCompletion(true);

    }

    @Override
    public void executeQuery1(Object connection) throws Throwable {

        Table orderItem = ((Connection) connection).getTable(TableName.valueOf("orderitem"));
        //AggregationClient aggClient = new AggregationClient(((Connection) connection).getConfiguration());
        Scan orderItemScan = new Scan();

        PrefixFilter prefixFilter = new PrefixFilter(Bytes.toBytes("status_1"));
        orderItemScan.setFilter(prefixFilter);
        orderItemScan.addColumn(Bytes.toBytes("data"), Bytes.toBytes("shipDate"));
        orderItemScan.addColumn(Bytes.toBytes("data"), Bytes.toBytes("quantity"));
        ResultScanner orderItemScanResult = orderItem.getScanner(orderItemScan);

        int sum = 0;
        for (Result res : orderItemScanResult) {
            if (Bytes.toString(res.getValue(Bytes.toBytes("data"), Bytes.toBytes("shipDate"))).equals("01.07.2024."))
                sum += Integer.parseInt(Bytes.toString(res.getValue(Bytes.toBytes("data"), Bytes.toBytes("quantity"))));
        }
        System.out.println("Sum for status1: " + sum);
    }

    @Override
    public void executeOLAPQueryFaster(Object connection) throws Throwable {
        Table orderItemStats = ((Connection) connection).getTable(TableName.valueOf("orderitemstats"));

        Get orderItemStatsGet = new Get(Bytes.toBytes("status_1"));
        orderItemStatsGet.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("quantitySumOn01072024"));
        orderItemStatsGet.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("basePrice01072024"));
        orderItemStatsGet.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("sumDiscountPrice01072024"));
        orderItemStatsGet.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("sumChargePrice01072024"));
        orderItemStatsGet.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("avgQuantity01072024"));
        orderItemStatsGet.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("avgPrice01072024"));
        orderItemStatsGet.addColumn(Bytes.toBytes("stats"), Bytes.toBytes("avgDiscount01072024"));

        Result orderItemStatsResult = orderItemStats.get(orderItemStatsGet);
    }

    @Override
    public void executeQuery2(Object o) throws SQLException {

    }

    @Override
    public void executeQuery3(Object o) {

    }
}
