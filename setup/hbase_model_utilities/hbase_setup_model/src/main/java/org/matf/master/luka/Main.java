package org.matf.master.luka;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;

public class Main {
    public static void main(String[] args) throws IOException {

        Configuration config = HBaseConfiguration.create();
        config.set("hbase.master", "localhost:16010");
        config.set("hbase.zookeeper.quorum","zookeeper:2181");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        Connection hbaseConnection = ConnectionFactory.createConnection(config);

        Admin admin = hbaseConnection.getAdmin();
        TableName fxUserTable = TableName.valueOf("fxuser");
        TableName fxAccountsTable = TableName.valueOf("fxaccounts");
        TableName fxTransactionTable = TableName.valueOf("fxtransaction");
        TableName fxRatesTable = TableName.valueOf("fxrate");

        HTableDescriptor fxUserDescriptor = new HTableDescriptor(fxUserTable);
        HTableDescriptor fxAccountsDescriptor = new HTableDescriptor(fxAccountsTable);
        HTableDescriptor fxTransactionDescriptor = new HTableDescriptor(fxTransactionTable);
        HTableDescriptor fxRatesDescriptor = new HTableDescriptor(fxRatesTable);


        //------------------------------FXUSER-----------------------------
        fxUserDescriptor.addFamily(new HColumnDescriptor("creds"));
        fxUserDescriptor.addFamily(new HColumnDescriptor("contact"));
        fxUserDescriptor.addFamily(new HColumnDescriptor("loc"));
        fxUserDescriptor.addFamily(new HColumnDescriptor("gen_info"));
        fxUserDescriptor.addFamily(new HColumnDescriptor("balance"));
        fxUserDescriptor.addFamily(new HColumnDescriptor("timestamps"));
        admin.createTable(fxUserDescriptor);
        Table fxUser = hbaseConnection.getTable(fxUserTable);

        for(int i=0;i<30000;i++){
            String username = "username_"+i;
            String password = "password_"+i;
            String startBalanceCurCode="EUR";
            long startBalance = 1000;
            String firstName = "firstname_"+i;
            String lastName = "lastname_"+i;
            String street = "street_"+i%100;
            String city = "city_"+i%50;
            String state = "state_"+i%10;
            String zip = "zip_"+i%100;
            String phone = "phone_"+i;
            String mobile = "mobile_"+i;
            String email = "email_"+i;
            long created = System.currentTimeMillis();

            Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
            put.addColumn(Bytes.toBytes("creds"),Bytes.toBytes("username"),Bytes.toBytes(username));
            put.addColumn(Bytes.toBytes("creds"),Bytes.toBytes("password"),Bytes.toBytes(password));
            put.addColumn(Bytes.toBytes("contact"),Bytes.toBytes("email"),Bytes.toBytes(email));
            put.addColumn(Bytes.toBytes("contact"),Bytes.toBytes("mobile"),Bytes.toBytes(mobile));
            put.addColumn(Bytes.toBytes("contact"),Bytes.toBytes("phone"),Bytes.toBytes(phone));
            put.addColumn(Bytes.toBytes("loc"),Bytes.toBytes("street"),Bytes.toBytes(street));
            put.addColumn(Bytes.toBytes("loc"),Bytes.toBytes("city"),Bytes.toBytes(city));
            put.addColumn(Bytes.toBytes("loc"),Bytes.toBytes("state"),Bytes.toBytes(state));
            put.addColumn(Bytes.toBytes("loc"),Bytes.toBytes("zip"),Bytes.toBytes(zip));
            put.addColumn(Bytes.toBytes("gen_info"),Bytes.toBytes("firstname"),Bytes.toBytes(firstName));
            put.addColumn(Bytes.toBytes("gen_info"),Bytes.toBytes("lastname"),Bytes.toBytes(lastName));
            put.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("start_balance"),Bytes.toBytes(startBalance));
            put.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("start_balance_cur"),Bytes.toBytes(startBalanceCurCode));
            put.addColumn(Bytes.toBytes("timestamps"),Bytes.toBytes("created"),Bytes.toBytes(created));

            fxUser.put(put);
        }


        //------------------------------FXACCOUNTS-----------------------------
        fxAccountsDescriptor.addFamily(new HColumnDescriptor("id"));
        fxAccountsDescriptor.addFamily(new HColumnDescriptor("fxuser"));
        fxAccountsDescriptor.addFamily(new HColumnDescriptor("balance"));
        fxAccountsDescriptor.addFamily(new HColumnDescriptor("timestamps"));
        admin.createTable(fxAccountsDescriptor);
        Table fxAccounts = hbaseConnection.getTable(fxAccountsTable);

        for(int i = 0;i<120000;i++) {
            Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));

            String user = "username_"+i%30000;
            String currencyCode;
            switch(i/30000){
                case 0:{
                    currencyCode = "EUR";
                    break;
                }
                case 1: {
                    currencyCode = "USD";
                    break;
                }
                case 2: {
                    currencyCode = "DIN";
                    break;
                }
                default: {
                    currencyCode = "CHF";
                    break;
                }
            }
            BigDecimal balance= i>=30000 ? BigDecimal.ZERO : new BigDecimal(1000);
            long created = System.currentTimeMillis();

            put.addColumn(Bytes.toBytes("id"),Bytes.toBytes("id"),Bytes.toBytes(new BigDecimal(i)));
            put.addColumn(Bytes.toBytes("fxuser"),Bytes.toBytes("fxuser"),Bytes.toBytes(user));
            put.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("balance"),Bytes.toBytes(balance));
            put.addColumn(Bytes.toBytes("balance"),Bytes.toBytes("cur_code"),Bytes.toBytes(currencyCode));
            put.addColumn(Bytes.toBytes("timestamps"),Bytes.toBytes("created"),Bytes.toBytes(created));

            fxAccounts.put(put);
        }


        //------------------------------FXTRANSACTION-----------------------------
        fxTransactionDescriptor.addFamily(new HColumnDescriptor("id"));
        fxTransactionDescriptor.addFamily(new HColumnDescriptor("info"));
        fxTransactionDescriptor.addFamily(new HColumnDescriptor("status"));
        fxTransactionDescriptor.addFamily(new HColumnDescriptor("date"));
        admin.createTable(fxTransactionDescriptor);



        //------------------------------FXRATE-----------------------------
        fxRatesDescriptor.addFamily(new HColumnDescriptor("info"));
        admin.createTable(fxRatesDescriptor);
        Table fxRates = hbaseConnection.getTable(fxRatesTable);

        Put put = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("EUR"));
        put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("USD"));
        put.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("1.08"));
        fxRates.put(put);

        Put put2 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put2.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("EUR"));
        put2.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("DIN"));
        put2.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("117.05"));
        fxRates.put(put2);

        Put put3 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put3.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("EUR"));
        put3.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("CHF"));
        put3.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("0.97"));
        fxRates.put(put3);

        Put put4 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put4.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("USD"));
        put4.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("EUR"));
        put4.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("0.92"));
        fxRates.put(put4);

        Put put5 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put5.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("USD"));
        put5.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("DIN"));
        put5.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("108.02"));
        fxRates.put(put5);

        Put put6 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put6.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("USD"));
        put6.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("CHF"));
        put6.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("0.90"));
        fxRates.put(put6);

        Put put7 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put7.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("DIN"));
        put7.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("EUR"));
        put7.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("0.008"));
        fxRates.put(put7);

        Put put8 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put8.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("DIN"));
        put8.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("USD"));
        put8.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("0.009"));
        fxRates.put(put8);

        Put put9 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put9.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("DIN"));
        put9.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("CHF"));
        put9.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("0.008"));
        fxRates.put(put9);

        Put put10 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put10.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("CHF"));
        put10.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("EUR"));
        put10.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("1.022"));
        fxRates.put(put10);

        Put put11 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put11.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("CHF"));
        put11.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("USD"));
        put11.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("1.108"));
        fxRates.put(put11);

        Put put12 = new Put(Bytes.toBytes(UUID.randomUUID().toString()));
        put12.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_from"),Bytes.toBytes("CHF"));
        put12.addColumn(Bytes.toBytes("info"),Bytes.toBytes("cur_to"),Bytes.toBytes("DIN"));
        put12.addColumn(Bytes.toBytes("info"),Bytes.toBytes("rate"),Bytes.toBytes("119.71"));
        fxRates.put(put12);

    }
}