package org.matf.master.luka.hbase;

import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
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

        long scanFxAccountBeforeLoopStart = System.currentTimeMillis();
        Scan accountScan = new Scan(Bytes.toBytes(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code()));
        accountScan.setBatch(1);
        accountScan.setCacheBlocks(true);
        accountScan.setCaching(20);
        accountScan.setFilter(new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code()))));
        accountScan.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"));

        ResultScanner accountScanResult = fxAccount.getScanner(accountScan);
        BigDecimal availableBalance = null;
        long scanFxAccountBeforeLoopEnd = System.currentTimeMillis();

        long scanFxAccountStart = System.currentTimeMillis();
        for (Result res : accountScanResult) {
            availableBalance = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("balance"),Bytes.toBytes("balance")));
            break;
        }
        accountScanResult.close();
        long scanFxAccountEnd = System.currentTimeMillis();
        //System.out.println("Scan fxaccount table before loop length: "+(scanFxAccountBeforeLoopEnd-scanFxAccountBeforeLoopStart));
        //System.out.println("Scan fxaccount table for loop length: "+(scanFxAccountEnd-scanFxAccountStart));

        BigDecimal neededResources = null;
        Table fxRates = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxrate"));

        Scan neededResourcesScan = new Scan();
        neededResourcesScan.setFilter(new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes(fxTransaction.getFxAccount_to().getCurrency_code() + fxTransaction.getFxAccount_from().getCurrency_code()))));
        neededResourcesScan.addFamily(Bytes.toBytes("info"));
        ResultScanner neededResourcesScanResult = fxRates.getScanner(neededResourcesScan);
        for (Result res : neededResourcesScanResult) {
            NavigableMap<byte[], NavigableMap<byte[], byte[]>> resultMap = res.getNoVersionMap();
            for (byte[] columnFamily : resultMap.keySet()) {
                NavigableMap<byte[], byte[]> columnMap = resultMap.get(columnFamily);
                for (byte[] column : columnMap.keySet()) {
                    if (Bytes.toString(column).equals("rate"))
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

        long putTransactionStart = System.currentTimeMillis();
        Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put.addColumn(Bytes.toBytes("id"), Bytes.toBytes("id"), Bytes.toBytes(fxTransaction.getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("fxaccount_from"), Bytes.toBytes(fxTransaction.getFxAccount_from().getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("fxaccount_to"), Bytes.toBytes(fxTransaction.getFxAccount_to().getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("amount"), Bytes.toBytes(fxTransaction.getAmount()));
        put.addColumn(Bytes.toBytes("status"), Bytes.toBytes("status"), Bytes.toBytes(transactionStatus));
        put.addColumn(Bytes.toBytes("date"), Bytes.toBytes("entry_date"), Bytes.toBytes(System.currentTimeMillis()));
        fxtransaction.put(put);
        long putTransactionEnd = System.currentTimeMillis();

        //System.out.println("Put transaction: "+(putTransactionEnd-putTransactionStart));


        return ExecutePaymentInfo.builder()
                .amountToGive(neededResources)
                .amountToReceive(fxTransaction.getAmount())
                .accountToHBaseKeyID(fxTransaction.getFxAccount_to().getFxUser().getUsername() + fxTransaction.getFxAccount_to().getCurrency_code())
                .accountFromHBaseKeyID(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code())
                .build();
    }

    @Override
    public void executePayment(ExecutePaymentInfo executePaymentInfo) throws IOException {

        Table fxAccountFrom = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxaccounts"));

        Scan fxAccountsFromScan = new Scan(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()));
        fxAccountsFromScan.setFilter(new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()))));
        fxAccountsFromScan.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"));
        ResultScanner fxAccountsFromScanResult = fxAccountFrom.getScanner(fxAccountsFromScan);
        BigDecimal fromBalance = null;

        for (Result res : fxAccountsFromScanResult) {
            NavigableMap<byte[], NavigableMap<byte[], byte[]>> resultMap = res.getNoVersionMap();
            for (byte[] columnFamily : resultMap.keySet()) {
                NavigableMap<byte[], byte[]> columnMap = resultMap.get(columnFamily);
                for (byte[] column : columnMap.keySet()) {
                    if (Bytes.toString(column).equals("balance"))
                        fromBalance = Bytes.toBigDecimal(columnMap.get(column));
                }
            }
        }
        fxAccountsFromScanResult.close();

        Put put = new Put(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()));
        assert fromBalance != null;
        put.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"),Bytes.toBytes(fromBalance.subtract(executePaymentInfo.getAmountToGive())));
        fxAccountFrom.put(put);

        Scan fxAccountsToScan = new Scan(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()));
        fxAccountsToScan.setFilter(new RowFilter(CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()))));
        fxAccountsToScan.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"));
        ResultScanner fxAccountsToScanResult = fxAccountFrom.getScanner(fxAccountsToScan);
        BigDecimal toBalance = null;

        for (Result res : fxAccountsToScanResult) {
            NavigableMap<byte[], NavigableMap<byte[], byte[]>> resultMap = res.getNoVersionMap();
            for (byte[] columnFamily : resultMap.keySet()) {
                NavigableMap<byte[], byte[]> columnMap = resultMap.get(columnFamily);
                for (byte[] column : columnMap.keySet()) {
                    if (Bytes.toString(column).equals("balance"))
                        toBalance = Bytes.toBigDecimal(columnMap.get(column));
                }
            }
        }
        fxAccountsToScanResult.close();

        Put put2 = new Put(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()));
        assert toBalance != null;
        put2.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"),Bytes.toBytes(fromBalance.add(executePaymentInfo.getAmountToReceive())));
        fxAccountFrom.put(put2);
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
