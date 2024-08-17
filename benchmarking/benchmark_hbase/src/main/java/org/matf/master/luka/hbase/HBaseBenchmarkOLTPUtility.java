package org.matf.master.luka.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.matf.master.luka.BenchmarkOLTPUtility;
import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.io.IOException;
import java.math.BigDecimal;

public class HBaseBenchmarkOLTPUtility implements BenchmarkOLTPUtility {

    @Override
    public ExecutePaymentInfo createFXTransaction(Object connection, FXTransaction fxTransaction) throws IOException {

        //dohvatanje
        Table fxAccount = ((Connection) connection).getTable(TableName.valueOf("fxaccounts"));

        //Long start = System.currentTimeMillis();
        Get get = new Get(Bytes.toBytes(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code()));
        get.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"));
        Result result = fxAccount.get(get);
        BigDecimal availableBalance = Bytes.toBigDecimal(result.getValue(Bytes.toBytes("balance"), Bytes.toBytes("balance")));
        //Long end = System.currentTimeMillis();
        //System.out.println("First get: "+(end-start));

        Table fxRates = ((Connection) connection).getTable(TableName.valueOf("fxrate"));
        //start = System.currentTimeMillis();
        Get fxRateGet = new Get(Bytes.toBytes(fxTransaction.getFxAccount_to().getCurrency_code() + fxTransaction.getFxAccount_from().getCurrency_code()));
        get.addColumn(Bytes.toBytes("info"), Bytes.toBytes("rate"));
        Result fxRateResult = fxRates.get(fxRateGet);
        BigDecimal neededResources = Bytes.toBigDecimal(fxRateResult.getValue(Bytes.toBytes("info"), Bytes.toBytes("rate"))).multiply(fxTransaction.getAmount());
        //end = System.currentTimeMillis();
        //System.out.println("Second get: "+(end-start));

        String transactionStatus = "NEW";
        assert neededResources != null;
        if (neededResources.compareTo(availableBalance) > 0) {
            transactionStatus = "BLOCKED";
        }

        Table fxtransaction = ((Connection) connection).getTable(TableName.valueOf("fxtransaction"));

        //start = System.currentTimeMillis();
        Put put = new Put(Bytes.toBytes(fxTransaction.getId()));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("fxaccount_from"), Bytes.toBytes(fxTransaction.getFxAccount_from().getId()));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("fxaccount_to"), Bytes.toBytes(fxTransaction.getFxAccount_to().getId()));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("amount"), Bytes.toBytes(fxTransaction.getAmount()));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("status"), Bytes.toBytes(transactionStatus));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("entry_date"), Bytes.toBytes(System.currentTimeMillis()));
        fxtransaction.put(put);
        //end = System.currentTimeMillis();
        //System.out.println("Insert: "+(end-start));


        return ExecutePaymentInfo.builder()
                .transactionID(fxTransaction.getId())
                .amountToGive(neededResources)
                .amountToReceive(fxTransaction.getAmount())
                .accountToHBaseKeyID(fxTransaction.getFxAccount_to().getFxUser().getUsername() + fxTransaction.getFxAccount_to().getCurrency_code())
                .accountFromHBaseKeyID(fxTransaction.getFxAccount_from().getFxUser().getUsername() + fxTransaction.getFxAccount_from().getCurrency_code())
                .build();
    }

    @Override
    public void executePayment(Object connection, ExecutePaymentInfo executePaymentInfo) throws IOException {

        Table fxAccount = ((Connection) connection).getTable(TableName.valueOf("fxaccounts"));
        Get get = new Get(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()));
        get.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"));
        Result result = fxAccount.get(get);
        BigDecimal fromBalance = Bytes.toBigDecimal(result.getValue(Bytes.toBytes("balance"), Bytes.toBytes("balance")));


        Get getTo = new Get(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()));
        get.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"));
        Result resultTo = fxAccount.get(getTo);
        BigDecimal toBalance = Bytes.toBigDecimal(resultTo.getValue(Bytes.toBytes("balance"), Bytes.toBytes("balance")));;

        Put put = new Put(Bytes.toBytes(executePaymentInfo.getAccountFromHBaseKeyID()));
        assert fromBalance != null;
        put.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"), Bytes.toBytes(fromBalance.subtract(executePaymentInfo.getAmountToGive())));
        fxAccount.put(put);

        Put put2 = new Put(Bytes.toBytes(executePaymentInfo.getAccountToHBaseKeyID()));
        assert toBalance != null;
        put2.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"), Bytes.toBytes(toBalance.add(executePaymentInfo.getAmountToReceive())));
        fxAccount.put(put2);

        Table fxTransaction = ((Connection) connection).getTable(TableName.valueOf("fxtransaction"));
        Put fxTransactionStatusUpdate = new Put(Bytes.toBytes(executePaymentInfo.getTransactionID()));
        fxTransactionStatusUpdate.addColumn(Bytes.toBytes("data"), Bytes.toBytes("status"), Bytes.toBytes("PROCESSED"));
        fxTransaction.put(fxTransactionStatusUpdate);

    }

    @Override
    public String checkTransactionStatus(Object connection, FXTransaction fxTransaction) throws IOException {
        Table fxTransactions = ((Connection) connection).getTable(TableName.valueOf("fxtransaction"));

        Get get = new Get(Bytes.toBytes(fxTransaction.getId()));
        get.addColumn(Bytes.toBytes("data"), Bytes.toBytes("status"));
        Result result = fxTransactions.get(get);
        String status = Bytes.toString(result.getValue(Bytes.toBytes("data"), Bytes.toBytes("status")));
        return status;

    }

    @Override
    public void executeRandomAccess(Object connection, int maxId) throws IOException {
        Table fxtransaction = ((Connection) connection).getTable(TableName.valueOf("fxtransaction"));

        Scan transactionScan = new Scan().withStartRow(Bytes.toBytes(maxId / 2));
        transactionScan.setMaxResultSize(1);
        transactionScan.addColumn(Bytes.toBytes("data"), Bytes.toBytes("amount"));

        ResultScanner accountScanResult = fxtransaction.getScanner(transactionScan);
        for (Result res : accountScanResult) {
            //BigDecimal amount = Bytes.toBigDecimal(res.getValue(Bytes.toBytes("data"), Bytes.toBytes("amount")));
            String status = Bytes.toString(res.getValue(Bytes.toBytes("data"), Bytes.toBytes("status")));
            break;
        }
    }

    @Override
    public void executeRandomAccessWithoutIndex(Object connection) throws Exception {
        Table fxAccount = ((Connection) connection).getTable(TableName.valueOf("fxaccounts"));

        Get get = new Get(Bytes.toBytes("username_15000" + "EUR"));
        get.addColumn(Bytes.toBytes("balance"), Bytes.toBytes("balance"));
        Result result = fxAccount.get(get);

        BigDecimal availableBalance = Bytes.toBigDecimal(result.getValue(Bytes.toBytes("balance"), Bytes.toBytes("balance")));

    }

    @Override
    public void executeInsert(Object connection) throws IOException {
        Table fxtransaction = ((Connection) connection).getTable(TableName.valueOf("fxtransaction"));
        Put put = new Put(Bytes.toBytes(1000000));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("fxaccount_from"), Bytes.toBytes(250));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("fxaccount_to"), Bytes.toBytes(300));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("amount"), Bytes.toBytes(1000));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("status"), Bytes.toBytes("NEW"));
        put.addColumn(Bytes.toBytes("data"), Bytes.toBytes("entry_date"), Bytes.toBytes(System.currentTimeMillis()));
        fxtransaction.put(put);
    }

    @Override
    public void testConsistency(Object connection) {

/*
        U startu korisnik ima 5000

        1. sum(FXACCOUNT_RATE*FXACCOUNT_BALANCE) = START_BALANCE
        2. svaki slog u history tabeli mora imati odgovarajuci slog u fxtransaction tabeli U ISTOM STATUSU

        Nakon svakih 10 transakcija uradimo test konzistentosti
*/

    }

}
