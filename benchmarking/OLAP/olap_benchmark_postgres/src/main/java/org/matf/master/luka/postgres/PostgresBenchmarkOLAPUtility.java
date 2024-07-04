package org.matf.master.luka.postgres;

import org.matf.master.olap.benchmark.BenchmarkOLAPUtility;
import org.matf.master.olap.benchmark.BenchmarkUtility;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.*;

public class PostgresBenchmarkOLAPUtility implements BenchmarkOLAPUtility {
    @Override
    public void bulkLoad(Object connection) throws Exception {
/*

PRODUCT (200 000)

	INSERT INTO PRODUCT(ID,NAME,BRAND,TYPE,SIZE,CONTAINER,PRICE,COMMENT)
	VALUES(N, NAME_N, BRAND_N,TYPE_N, N%200, CONTAINER_N, N%1000, COMMENT_N);
*/
//        for (int i = 0;i<200000;i++){
//            String insertSQL= """
//            INSERT INTO postgresdb.PRODUCT(ID,NAME,BRAND,TYPE,SIZE,CONTAINER,PRICE,COMMENT)
//            VALUES(?, ?, ?,?, ?, ?, ?, ?);
//            """;
//            PreparedStatement insertPreparedStatement =  ((Connection) connection).prepareStatement(insertSQL);
//            insertPreparedStatement.setInt(1,i);
//            insertPreparedStatement.setString(2,"name_"+i);
//            insertPreparedStatement.setString(3,"brand_"+i);
//            insertPreparedStatement.setString(4,"type_"+(i%200));
//            insertPreparedStatement.setInt(5,i%200);
//            insertPreparedStatement.setString(6,"container_"+i);
//            insertPreparedStatement.setBigDecimal(7,new BigDecimal("1000.0"));
//            insertPreparedStatement.setString(8,"comment_"+i);
//            insertPreparedStatement.executeUpdate();
//        }
            long rowsInserted = new CopyManager((BaseConnection) connection)
                    .copyIn(
                            "COPY postgresdb.PRODUCT FROM STDIN (FORMAT csv, HEADER)",
                            new BufferedReader(new FileReader("./product.csv")));
        System.out.println("Rows inserted: "+rowsInserted);


/*
SUPPLIER (10 000)

	INSERT INTO SUPPLIER(ID,NAME, ADDRESS, PHONE)
	VALUES(N, NAME_N,ADDRESS_N, PHONE_N)
*/
//        for (int i = 0;i<10000;i++){
//            String insertSQL= """
//            INSERT INTO postgresdb.SUPPLIER(ID,NAME,ADDRESS,PHONE)
//            VALUES(?, ?, ?, ?);
//            """;
//            PreparedStatement insertPreparedStatement =  ((Connection) connection).prepareStatement(insertSQL);
//            insertPreparedStatement.setInt(1,i);
//            insertPreparedStatement.setString(2,"name_"+i);
//            insertPreparedStatement.setString(3,"address_"+i);
//            insertPreparedStatement.setString(4,"phone_"+i);
//            insertPreparedStatement.executeUpdate();
//        }
/*
CUSTOMER (150 000)

    INSERT INTO CUSTOMER(ID,NAME,ADDRESS, PHONE,COMMENT)
    VALUES(N, NAME_N, ADDRESS_N, PHONE_N, COMMENT_N)
*/
//        for (int i = 0;i<150000;i++){
//            String insertSQL= """
//            INSERT INTO postgresdb.CUSTOMER(ID,NAME,ADDRESS,PHONE,COMMENT)
//            VALUES(?, ?, ?, ?,?);
//            """;
//            PreparedStatement insertPreparedStatement =  ((Connection) connection).prepareStatement(insertSQL);
//            insertPreparedStatement.setInt(1,i);
//            insertPreparedStatement.setString(2,"name_"+i);
//            insertPreparedStatement.setString(3,"address_"+i);
//            insertPreparedStatement.setString(4,"phone_"+i);
//            insertPreparedStatement.setString(5,"comment_"+i);
//            insertPreparedStatement.executeUpdate();
//        }
/*
PRODUCTSUPPLIER (800 000)

	INSERT INTO PRODUCTSUPPLIER(PRODUCT, SUPPLIER, AVAILABLE, SUPPLY_COST, COMMENT)
	VALUES(N%200 000, N % 10 000, N %100, 250, COMMENT_N)
*/
//        for (int i = 0;i<800000;i++){
//            String insertSQL= """
//            INSERT INTO postgresdb.PRODUCTSUPPLIER(ID,PRODUCT,SUPPLIER,AVAILABLE,SUPPLY_COST,COMMENT)
//            VALUES(?,?, ?, ?, ?,?);
//            """;
//            PreparedStatement insertPreparedStatement =  ((Connection) connection).prepareStatement(insertSQL);
//            insertPreparedStatement.setInt(1,i);
//            insertPreparedStatement.setInt(2,i%200000);
//            insertPreparedStatement.setInt(3,i%10000);
//            insertPreparedStatement.setInt(4,i%100);
//            insertPreparedStatement.setBigDecimal(5,new BigDecimal("200.00"));
//            insertPreparedStatement.setString(6,"comment_"+i);
//            insertPreparedStatement.executeUpdate();
//        }
/*
ORDER (1 500 000)

	INSERT INTO ORDER(ID, CUSTOMER, STATUS, TOTAL_PRICE, ENTRY_DATE, PRIORITY, COMMENT)
	VALUES(N, N%150 000, STATUS_(N%20), 20000, new Date(), PRIORITY_(N%10), COMMENT_N);
*/
//        for (int i = 0;i<1500000;i++){
//            String insertSQL= """
//            INSERT INTO postgresdb.ORDER(ID,CUSTOMER,STATUS,TOTAL_PRICE,ENTRY_DATE, PRIORITY,COMMENT)
//            VALUES(?,?, ?, ?, ?,?,?);
//            """;
//            PreparedStatement insertPreparedStatement =  ((Connection) connection).prepareStatement(insertSQL);
//            insertPreparedStatement.setInt(1,i);
//            insertPreparedStatement.setInt(2,i%150000);
//            insertPreparedStatement.setString(3,"status_"+(i%20));
//            insertPreparedStatement.setBigDecimal(4,new BigDecimal("20000"));
//            insertPreparedStatement.setDate(5,new Date(System.currentTimeMillis()));
//            insertPreparedStatement.setString(6, "priority_"+(i%10));
//            insertPreparedStatement.setString(7,"comment_"+i);
//            insertPreparedStatement.executeUpdate();
//        }
/*
ORDER_ITEM (6 000 000)

	INSERT INTO ORDER(ORDER, PRODUCT, SUPPLIER, ORDER_NO, QUANTITY, BASE_PRICE, DISCOUNT, TAX, STATUS, SHIP_DATE, COMMIT_DATE, COMMENT)
	VALUES(N%1500000, N%200000, N%10000, N%10, N%5, 3500, 0.1, 0.2, STATUS_N, new Date(), new Date(), COMMENT_N);


 */
//        for (int i = 0;i<6000000;i++){
//            String insertSQL= """
//            INSERT INTO postgresdb.ORDER_ITEM(ORDER_ID,PRODUCT,SUPPLIER,ORDER_NO,QUANTITY, BASE_PRICE,DISCOUNT, TAX, STATUS, SHIP_DATE, COMMIT_DATE, COMMENT)
//            VALUES(?, ?, ?, ?,?,?,?, ?, ?, ?,?,?);
//            """;
//            PreparedStatement insertPreparedStatement =  ((Connection) connection).prepareStatement(insertSQL);
//            insertPreparedStatement.setInt(1,i%1450000);
//            insertPreparedStatement.setInt(2,i%197000);
//            insertPreparedStatement.setInt(3,i%9500);
//            insertPreparedStatement.setInt(4,i%10);
//            insertPreparedStatement.setInt(5,i%5);
//            insertPreparedStatement.setBigDecimal(6, new BigDecimal("3500"));
//            insertPreparedStatement.setBigDecimal(7,new BigDecimal("0.1"));
//            insertPreparedStatement.setBigDecimal(8,new BigDecimal("0.2"));
//            insertPreparedStatement.setString(9, "status_"+(i%10));
//            insertPreparedStatement.setDate(10, new Date(System.currentTimeMillis()));
//            insertPreparedStatement.setDate(11, new Date(System.currentTimeMillis()));
//            insertPreparedStatement.setString(12, "comment_"+i);
//            insertPreparedStatement.executeUpdate();
//        }
    }

    @Override
    public void executeQuery1(Object connection) throws SQLException {
        String query1String = """
               select
                    OI.STATUS,
                    sum(OI.QUANTITY) as sum_qty,
                    sum(OI.BASE_PRICE) as sum_base_price,
                    sum(OI.BASE_PRICE*(1-OI.discount)) as sum_disc_price,
                    sum(OI.BASE_PRICE*(1-OI.discount)*(1+OI.TAX)) as sum_charge,
                    avg(OI.QUANTITY) as avg_qty,
                    avg(OI.BASE_PRICE) as avg_price,
                    avg(OI.DISCOUNT) as avg_disc,
                    count(*) as count_order
               from
                    postgresdb.ORDER_ITEM OI
               where
                    OI.SHIP_DATE >= TO_DATE(?,'DD.MM.YYYY')
               group by
                    OI.STATUS
               order by
                    OI.STATUS;
                """;
        PreparedStatement query1PreparedStatement =  ((Connection) connection).prepareStatement(query1String);
        query1PreparedStatement.setString(1,"01.01.2024.");
        query1PreparedStatement.executeQuery();

    }

    @Override
    public void executeQuery2(Object connection) throws SQLException {
        String query2String = """
                select
                    s.name,
                    p.id,
                    s.address,
                    s.phone,
                    p.comment
                from
                    postgresdb.PRODUCT p,
                    postgresdb.SUPPLIER s,
                    postgresdb.PRODUCTSUPPLIER ps
                where
                    p.id = ps.product
                    and s.id = ps.supplier
                    and p.size = ?
                    and p.type like ?
                    and ps.supply_cost = (
                        select
                            min(ps2.supply_cost)
                        from
                            postgresdb.PRODUCTSUPPLIER ps2,
                            postgresdb.SUPPLIER s2
                        where
                            p.id = ps2.product
                            and s2.id = ps2.supplier
                    )
                order by
                    s.name,
                    p.id;
        """;
        PreparedStatement query2PreparedStatement =  ((Connection) connection).prepareStatement(query2String);
        query2PreparedStatement.setInt(1,3);
        query2PreparedStatement.setString(2,"%a%");
        query2PreparedStatement.executeQuery();
    }

    @Override
    public void executeQuery3(Object o) {

    }
}
