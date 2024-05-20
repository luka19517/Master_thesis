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

        Scan accountScan = new Scan().withStartRow(Bytes.toBytes(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code()));
        accountScan.setMaxResultSize(1);
        accountScan.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"));
        ResultScanner accountScanResult = fxAccount.getScanner(accountScan);
        BigDecimal availableBalance = null;
        for (Result res : accountScanResult) {
            availableBalance = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("balance"),Bytes.toBytes("balance")));
            break;
        }
        accountScanResult.close();

        BigDecimal neededResources = null;
        Table fxRates = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxrate"));

        Scan neededResourcesScan = new Scan().withStartRow(Bytes.toBytes(fxTransaction.getFxAccount_to().getCurrency_code() + fxTransaction.getFxAccount_from().getCurrency_code()));
        accountScan.setMaxResultSize(1);
        neededResourcesScan.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"));
        ResultScanner neededResourcesScanResult = fxRates.getScanner(neededResourcesScan);
        for (Result res : neededResourcesScanResult) {
            neededResources = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("info"),Bytes.toBytes("rate"))).multiply(fxTransaction.getAmount());
        }
        neededResourcesScanResult.close();

        String transactionStatus = "NEW";
        assert neededResources != null;
        if (neededResources.compareTo(availableBalance) > 0) {
            transactionStatus = "BLOCKED";
        }

        Table fxtransaction = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxtransaction"));

        Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put.addColumn(Bytes.toBytes("id"), Bytes.toBytes("id"), Bytes.toBytes(fxTransaction.getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("fxaccount_from"), Bytes.toBytes(fxTransaction.getFxAccount_from().getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("fxaccount_to"), Bytes.toBytes(fxTransaction.getFxAccount_to().getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("amount"), Bytes.toBytes(fxTransaction.getAmount()));
        put.addColumn(Bytes.toBytes("status"), Bytes.toBytes("status"), Bytes.toBytes(transactionStatus));
        put.addColumn(Bytes.toBytes("date"), Bytes.toBytes("entry_date"), Bytes.toBytes(System.currentTimeMillis()));
        fxtransaction.put(put);


        return ExecutePaymentInfo.builder()
                .amountToGive(neededResources)
                .amountToReceive(fxTransaction.getAmount())
                .accountToHBaseKeyID(fxTransaction.getFxAccount_to().getFxUser().getUsername() + fxTransaction.getFxAccount_to().getCurrency_code())
                .accountFromHBaseKeyID(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code())
                .build();
    }

    @Override
    public void executePayment(ExecutePaymentInfo executePaymentInfo) throws IOException {

        Table fxAccount = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxaccounts"));
        
        Scan fxAccountsFromScan = new Scan().withStartRow(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()));
        fxAccountsFromScan.setMaxResultSize(1);
        fxAccountsFromScan.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"));
        ResultScanner fxAccountsFromScanResult = fxAccount.getScanner(fxAccountsFromScan);
        BigDecimal fromBalance = null;

        for (Result res : fxAccountsFromScanResult) {
            fromBalance = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("balance"),Bytes.toBytes("balance")));
            break;
        }
        fxAccountsFromScanResult.close();

        Put put = new Put(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()));
        assert fromBalance != null;
        put.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"),Bytes.toBytes(fromBalance.subtract(executePaymentInfo.getAmountToGive())));
        fxAccount.put(put);

        Scan fxAccountsToScan = new Scan().withStartRow(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()));
        fxAccountsToScan.setMaxResultSize(1);
        fxAccountsToScan.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"));
        ResultScanner fxAccountsToScanResult = fxAccount.getScanner(fxAccountsToScan);
        BigDecimal toBalance = null;

        for (Result res : fxAccountsToScanResult) {
            toBalance = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("balance"),Bytes.toBytes("balance")));
            break;
        }
        fxAccountsToScanResult.close();

        Put put2 = new Put(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()));
        assert toBalance != null;
        put2.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"),Bytes.toBytes(toBalance.add(executePaymentInfo.getAmountToReceive())));
        fxAccount.put(put2);
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
