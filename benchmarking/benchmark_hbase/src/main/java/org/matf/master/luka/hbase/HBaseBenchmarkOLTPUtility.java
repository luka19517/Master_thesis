package org.matf.master.luka.hbase;

import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.matf.master.luka.BenchmarkOLTPUtility;
import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NavigableMap;

public class HBaseBenchmarkOLTPUtility implements BenchmarkOLTPUtility {

    @Override
    public ExecutePaymentInfo createFXTransaction(FXTransaction fxTransaction) throws IOException {
        Table fxAccount = HBaseBenchmarkUtility.hbaseConnection.getTable(TableName.valueOf("fxaccounts"));
        SingleColumnValueFilter filter = new SingleColumnValueFilter(
                Bytes.toBytes("id"),
                Bytes.toBytes("id"),
                CompareOperator.EQUAL,
                new BinaryComparator((Bytes.toBytes(0)))
        );

        Scan accountScan = new Scan();
        accountScan.setFilter(filter);
        accountScan.addFamily(Bytes.toBytes("id"));

        ResultScanner accountScanResult = fxAccount.getScanner(accountScan);

        BigDecimal availableBalance;
        String col = "";
        String value = "";
        for (Result res : accountScanResult) {
            NavigableMap<byte[], NavigableMap<byte[], byte[]>> resultMap = res.getNoVersionMap();
            for (byte[] columnFamily : resultMap.keySet()) {
                NavigableMap<byte[], byte[]> columnMap = resultMap.get(columnFamily);
                for (byte[] column : columnMap.keySet()) {
                    col = Bytes.toString(column);
                    value = Bytes.toString(columnMap.get(column));

                }
            }
        }
        accountScanResult.close();

        System.out.println("Column: " + col + " value: " + value);

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
