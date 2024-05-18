package org.matf.master.luka.hbase;

import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
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

        Scan accountScan = new Scan();
        accountScan.setFilter(new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes(fxTransaction.getFxAccount_from().getFxUser().getUsername()+fxTransaction.getFxAccount_from().getCurrency_code()))));
        accountScan.addFamily(Bytes.toBytes("balance"));

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

        Scan neededResourcesScan = new Scan();
        neededResourcesScan.setFilter(new RowFilter(CompareOperator.EQUAL,new BinaryComparator(Bytes.toBytes(fxTransaction.getFxAccount_to().getCurrency_code()+fxTransaction.getFxAccount_from().getCurrency_code()))));
        neededResourcesScan.addFamily(Bytes.toBytes("info"));
        ResultScanner neededResourcesScanResult = fxRates.getScanner(neededResourcesScan);
        for (Result res : neededResourcesScanResult) {
            NavigableMap<byte[], NavigableMap<byte[], byte[]>> resultMap = res.getNoVersionMap();
            for (byte[] columnFamily : resultMap.keySet()) {
                NavigableMap<byte[], byte[]> columnMap = resultMap.get(columnFamily);
                for (byte[] column : columnMap.keySet()) {
                    if(Bytes.toString(column).equals("rate"))
                        neededResources = Bytes.toBigDecimal(columnMap.get(column)).multiply(fxTransaction.getAmount());
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



        return ExecutePaymentInfo.builder()
                .amountToGive(neededResources)
                .amountToReceive(fxTransaction.getAmount())
                .accountFrom(fxTransaction.getFxAccount_from().getId())
                .accountTo(fxTransaction.getFxAccount_to().getId())
                .build();
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
