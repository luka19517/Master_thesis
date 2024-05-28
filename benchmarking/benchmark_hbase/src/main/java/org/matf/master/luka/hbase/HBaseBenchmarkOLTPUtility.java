package org.matf.master.luka.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.regionserver.MultiVersionConcurrencyControl;
import org.apache.hadoop.hbase.util.Bytes;
import org.matf.master.luka.BenchmarkOLTPUtility;
import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.io.IOException;
import java.math.BigDecimal;

public class HBaseBenchmarkOLTPUtility implements BenchmarkOLTPUtility {

    @Override
    public ExecutePaymentInfo createFXTransaction(FXTransaction fxTransaction) throws IOException {

        //dohvatanje
        Table fxAccount = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxaccounts"));

        Scan accountScan = new Scan().withStartRow(Bytes.toBytes(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code()));
        accountScan.setMaxResultSize(1);
        accountScan.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"));
        ResultScanner accountScanResult = fxAccount.getScanner(accountScan);
        BigDecimal availableBalance = null;
        for (Result res : accountScanResult) {
            availableBalance = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("balance"), Bytes.toBytes("balance")));
            break;
        }
        accountScanResult.close();

        BigDecimal neededResources = null;
        Table fxRates = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxrate"));

        Scan neededResourcesScan = new Scan().withStartRow(Bytes.toBytes(fxTransaction.getFxAccount_to().getCurrency_code() + fxTransaction.getFxAccount_from().getCurrency_code()));
        accountScan.setMaxResultSize(1);
        neededResourcesScan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("rate"));
        ResultScanner neededResourcesScanResult = fxRates.getScanner(neededResourcesScan);
        for (Result res : neededResourcesScanResult) {
            neededResources = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("info"), Bytes.toBytes("rate"))).multiply(fxTransaction.getAmount());
        }
        neededResourcesScanResult.close();

        String transactionStatus = "NEW";
        assert neededResources != null;
        if (neededResources.compareTo(availableBalance) > 0) {
            transactionStatus = "BLOCKED";
        }

        Table fxtransaction = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxtransaction"));

        Put put = new Put(Bytes.toBytes(fxTransaction.getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("fxaccount_from"), Bytes.toBytes(fxTransaction.getFxAccount_from().getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("fxaccount_to"), Bytes.toBytes(fxTransaction.getFxAccount_to().getId()));
        put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("amount"), Bytes.toBytes(fxTransaction.getAmount()));
        put.addColumn(Bytes.toBytes("status"), Bytes.toBytes("status"), Bytes.toBytes(transactionStatus));
        put.addColumn(Bytes.toBytes("date"), Bytes.toBytes("entry_date"), Bytes.toBytes(System.currentTimeMillis()));
        fxtransaction.put(put);


        return ExecutePaymentInfo.builder()
                .transactionID(fxTransaction.getId())
                .amountToGive(neededResources)
                .amountToReceive(fxTransaction.getAmount())
                .accountToHBaseKeyID(fxTransaction.getFxAccount_to().getFxUser().getUsername() + fxTransaction.getFxAccount_to().getCurrency_code())
                .accountFromHBaseKeyID(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code())
                .build();
    }

    @Override
    public void executePayment(ExecutePaymentInfo executePaymentInfo) throws IOException {

        MultiVersionConcurrencyControl mvcc = new MultiVersionConcurrencyControl();
        Table fxAccount = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxaccounts"));

        Scan fxAccountsFromScan = new Scan().withStartRow(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()));
        fxAccountsFromScan.setMaxResultSize(1);
        fxAccountsFromScan.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"));
        ResultScanner fxAccountsFromScanResult = fxAccount.getScanner(fxAccountsFromScan);
        BigDecimal fromBalance = null;

        for (Result res : fxAccountsFromScanResult) {
            fromBalance = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("balance"), Bytes.toBytes("balance")));
            break;
        }
        fxAccountsFromScanResult.close();

        Scan fxAccountsToScan = new Scan().withStartRow(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()));
        fxAccountsToScan.setMaxResultSize(1);
        fxAccountsToScan.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"));
        ResultScanner fxAccountsToScanResult = fxAccount.getScanner(fxAccountsToScan);
        BigDecimal toBalance = null;


        for (Result res : fxAccountsToScanResult) {
            toBalance = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("balance"), Bytes.toBytes("balance")));
            break;
        }
        fxAccountsToScanResult.close();

        MultiVersionConcurrencyControl.WriteEntry writeEntry = mvcc.begin();
        try {
            Put put = new Put(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()));
            assert fromBalance != null;
            put.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"), Bytes.toBytes(fromBalance.subtract(executePaymentInfo.getAmountToGive())));
            fxAccount.put(put);

            Put put2 = new Put(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()));
            assert toBalance != null;
            put2.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"), Bytes.toBytes(toBalance.add(executePaymentInfo.getAmountToReceive())));
            fxAccount.put(put2);

            Table fxTransaction = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxtransaction"));
            Put fxTransactionStatusUpdate = new Put(Bytes.toBytes(executePaymentInfo.getTransactionID()));
            fxTransactionStatusUpdate.addColumn(Bytes.toBytes("status"), Bytes.toBytes("status"), Bytes.toBytes("PROCESSED"));
            fxTransaction.put(fxTransactionStatusUpdate);
            mvcc.completeAndWait(writeEntry);
        }catch (Exception e) {
            mvcc.complete(writeEntry);
        }

    }

    @Override
    public String checkTransactionStatus(FXTransaction fxTransaction) throws IOException {
        Table fxTransactions = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxtransaction"));
        Scan fxTransactionsScan = new Scan().withStartRow(Bytes.toBytes(fxTransaction.getId()));
        fxTransactionsScan.setMaxResultSize(1);
        fxTransactionsScan.addColumn(Bytes.toBytes("status"), Bytes.toBytes("status"));
        ResultScanner fxTransactionsScanResult = fxTransactions.getScanner(fxTransactionsScan);
        String status = null;
        for (Result res : fxTransactionsScanResult) {
            status = Bytes.toString(res.getValue(Bytes.toBytes("status"), Bytes.toBytes("status")));
            break;
        }
        return status;

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
