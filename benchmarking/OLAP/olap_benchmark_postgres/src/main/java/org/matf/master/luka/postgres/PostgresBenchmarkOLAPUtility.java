package org.matf.master.luka.postgres;

import org.matf.master.olap.benchmark.BenchmarkOLAPUtility;
import org.matf.master.olap.benchmark.BenchmarkUtility;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Objects;

public class PostgresBenchmarkOLAPUtility implements BenchmarkOLAPUtility {
    @Override
    public void bulkLoad(Object connection) throws Exception {

        new CopyManager((BaseConnection) connection)
                    .copyIn(
                            "COPY postgresdb.PRODUCT FROM STDIN (FORMAT csv)",
                            new BufferedReader(new FileReader("./product.csv")));
        new CopyManager((BaseConnection) connection)
                .copyIn(
                        "COPY postgresdb.SUPPLIER FROM STDIN (FORMAT csv)",
                        new BufferedReader(new FileReader("./supplier.csv")));
        new CopyManager((BaseConnection) connection)
                .copyIn(
                        "COPY postgresdb.PRODUCTSUPPLIER FROM STDIN (FORMAT csv)",
                        new BufferedReader(new FileReader("./productsupplier.csv")));

        new CopyManager((BaseConnection) connection)
                .copyIn(
                        "COPY postgresdb.CUSTOMER FROM STDIN (FORMAT csv)",
                        new BufferedReader(new FileReader("./customer.csv")));

        new CopyManager((BaseConnection) connection)
                .copyIn(
                        "COPY postgresdb.ORDER FROM STDIN (FORMAT csv)",
                        new BufferedReader(new FileReader("./order.csv")));

        new CopyManager((BaseConnection) connection)
                .copyIn(
                        "COPY postgresdb.ORDER_ITEM FROM STDIN (FORMAT csv)",
                        new BufferedReader(new FileReader("./orderitem.csv")));
        
    }

    @Override
    public void executeQuery1(Object connection) throws SQLException {
        String query1String = """
               select
                    OI.STATUS status,
                    sum(OI.QUANTITY) as sum_qty,
                    sum(OI.BASE_PRICE) as sum_base_price,
                    sum(OI.BASE_PRICE*(1-OI.discount)) as sum_disc_price,
                    sum(OI.BASE_PRICE*(1-OI.discount)*(1+OI.TAX)) as sum_charge,
                    avg(OI.QUANTITY) as avg_qty,
                    avg(OI.BASE_PRICE) as avg_price,
                    avg(OI.DISCOUNT) as avg_disc,
                    count(*) as count_order
               from
                    postgresdb.ORDER_ITEM OI,
                    postgresdb.ORDER O,
                    postgresdb.PRODUCT P,
                    postgresdb.SUPPLIER S
               where
                    O.ID = OI.ORDER_ID AND
                    P.ID = OI.PRODUCT AND
                    S.ID = OI.SUPPLIER AND
                    OI.SHIP_DATE >= TO_DATE(?,'DD.MM.YYYY') AND
                    O.STATUS = ?
               group by
                    OI.STATUS
               order by
                    OI.STATUS;
                """;
        PreparedStatement query1PreparedStatement =  ((Connection) connection).prepareStatement(query1String);
        query1PreparedStatement.setString(1,"07.07.2024");
        query1PreparedStatement.setString(2,"status_1");
        ResultSet result =  query1PreparedStatement.executeQuery();
        while (result.next()) {
            int totalQuantity = result.getInt("sum_qty");
            String status = result.getString("status");
            System.out.println("Sum for "+status+": "+totalQuantity);
        }

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
