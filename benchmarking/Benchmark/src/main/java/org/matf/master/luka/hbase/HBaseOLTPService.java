package org.matf.master.luka.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.matf.master.luka.common.OLTPService;
import org.matf.master.luka.common.Order;

import java.io.IOException;
import java.util.Date;

import static org.matf.master.luka.hbase.HBaseUtility.hbaseConnection;

public class HBaseOLTPService implements OLTPService {

    @Override
    public void createUser(int seed,String username, String password, String email) throws IOException {

        Table fxUserTable = hbaseConnection.getTable(TableName.valueOf("fxuser"));
        Put put = new Put(Bytes.toBytes(seed));
        put.addColumn(Bytes.toBytes("credentials"),Bytes.toBytes("username"),Bytes.toBytes(username));
        put.addColumn(Bytes.toBytes("credentials"),Bytes.toBytes("password"),Bytes.toBytes(password));
        put.addColumn(Bytes.toBytes("credentials"),Bytes.toBytes("email"),Bytes.toBytes(email));

        fxUserTable.put(put);
    }

    @Override
    public void authenticateUser(String username, String password) {
        System.out.println("HBASE ---- Authenticate");
    }

    @Override
    public void createOrder(Order order) {
        System.out.println("HBASE ---- Create order");
    }
}
