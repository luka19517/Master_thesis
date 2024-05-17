package org.matf.master.luka.hbase;

import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnValueFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.matf.master.luka.BenchmarkOLTPUtility;
import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NavigableMap;
import java.util.UUID;

public class HBaseBenchmarkOLTPUtility implements BenchmarkOLTPUtility {

    @Override
    public ExecutePaymentInfo createFXTransaction(FXTransaction fxTransaction) throws IOException {

        //dohvatanje
        Table fxAccount = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxaccounts"));
        SingleColumnValueFilter filter = new SingleColumnValueFilter(
                Bytes.toBytes("id"),
                Bytes.toBytes("id"),
                CompareOperator.EQUAL,
                Bytes.toBytes(new BigDecimal(fxTransaction.getId()))
        );
        filter.setFilterIfMissing(true);

        Scan accountScan = new Scan();
        accountScan.setFilter(filter);
        accountScan.addColumn(Bytes.toBytes("id"),Bytes.toBytes("id"));
        accountScan.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"));
        accountScan.addColumn(Bytes.toBytes("fxuser"),Bytes.toBytes("fxuser"));

        ResultScanner accountScanResult = fxAccount.getScanner(accountScan);
        BigDecimal availableBalance = null;

        for (Result res : accountScanResult) {
            NavigableMap<byte[], NavigableMap<byte[], byte[]>> resultMap = res.getNoVersionMap();
            for (byte[] columnFamily : resultMap.keySet()) {
                NavigableMap<byte[], byte[]> columnMap = resultMap.get(columnFamily);
                for (byte[] column : columnMap.keySet()) {
                    if(Bytes.toString(column).equals("balance"))
                        availableBalance = Bytes.toBigDecimal(columnMap.get(column));
                }
            }
        }
        accountScanResult.close();

        BigDecimal neededResources = null;
        Table fxRates = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxrate"));

        FilterList findNeededResourcesFilterList = new FilterList();
        findNeededResourcesFilterList.addFilter(new ColumnValueFilter(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),CompareOperator.EQUAL,Bytes.toBytes(fxTransaction.getFxAccount_from().getCurrency_code())));
        findNeededResourcesFilterList.addFilter(new ColumnValueFilter(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),CompareOperator.EQUAL,Bytes.toBytes(fxTransaction.getFxAccount_to().getCurrency_code())));

        Scan neededResourcesScan = new Scan();
        neededResourcesScan.setFilter(findNeededResourcesFilterList);
        neededResourcesScan.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"));

        ResultScanner neededResourcesScanResult = fxRates.getScanner(neededResourcesScan);
        for (Result res : neededResourcesScanResult) {
            NavigableMap<byte[], NavigableMap<byte[], byte[]>> resultMap = res.getNoVersionMap();
            for (byte[] columnFamily : resultMap.keySet()) {
                NavigableMap<byte[], byte[]> columnMap = resultMap.get(columnFamily);
                for (byte[] column : columnMap.keySet()) {
                    if(Bytes.toString(column).equals("rate"))
                        neededResources = Bytes.toBigDecimal(columnMap.get(column));
                }
            }
        }
        neededResourcesScanResult.close();

        String transactionStatus = "NEW";
        assert neededResources != null;
        if (neededResources.compareTo(availableBalance) > 0) {
            transactionStatus = "BLOCKED";
        }


        Table fxtransaction = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxtransaction"));
        Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put.addColumn(Bytes.toBytes("id"),Bytes.toBytes("id"), Bytes.toBytes(fxTransaction.getId()));
        put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("fxaccount_from"), Bytes.toBytes(fxTransaction.getFxAccount_from().getId()));
        put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("fxaccount_to"), Bytes.toBytes(fxTransaction.getFxAccount_to().getId()));
        put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("amount"), Bytes.toBytes(fxTransaction.getAmount()));
        put.addColumn(Bytes.toBytes("status"),Bytes.toBytes("status"), Bytes.toBytes(transactionStatus));
        put.addColumn(Bytes.toBytes("date"),Bytes.toBytes("entry_date"), Bytes.toBytes(System.currentTimeMillis()));
        fxtransaction.put(put);



        return null;
    }

    @Override
    public void executePayment(ExecutePaymentInfo fxTransaction) {

    }

    @Override
    public void checkTransactionStatus(FXTransaction fxTransaction) {

    }

    @Override
    public void testConsistency() {

/*
        U startu korisnik ima 5000

        1. sum(FXACCOUNT_RATE*FXACCOUNT_BALANCE) = START_BALANCE
        2. svaki slog u history tabeli mora imati odgovarajuci slog u fxtransaction tabeli U ISTOM STATUSU

        Nakon svakih 10 transakcija uradimo test konzistentosti
*/

    }

}
