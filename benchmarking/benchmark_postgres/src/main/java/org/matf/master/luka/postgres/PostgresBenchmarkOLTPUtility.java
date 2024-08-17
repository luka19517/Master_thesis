package org.matf.master.luka.postgres;

import org.matf.master.luka.BenchmarkOLTPUtility;
import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Objects;

public class PostgresBenchmarkOLTPUtility implements BenchmarkOLTPUtility {

    @Override
    public ExecutePaymentInfo createFXTransaction(Object connection, FXTransaction fxTransaction) throws SQLException {
        //dohvati stanja sa ciljanih FXACCOUNT_FROM, FXACCOUNT_TO
        //Long start = System.currentTimeMillis();
        String availableBalanceString = """
                SELECT FA.BALANCE
                FROM postgresdb.FXACCOUNT FA
                WHERE FA.ID = ?
                """;
        PreparedStatement availableBalanceSt = ((Connection) connection).prepareStatement(availableBalanceString);
        availableBalanceSt.setLong(1, fxTransaction.getFxAccount_from().getId());
        ResultSet availableBalanceRS = availableBalanceSt.executeQuery();
        BigDecimal availableBalance = null;
        while (availableBalanceRS.next()) {
            availableBalance = availableBalanceRS.getBigDecimal(1);
        }
        availableBalanceRS.close();
        availableBalanceSt.close();
        //Long end = System.currentTimeMillis();
        //System.out.println("First get: "+(end-start));

        //start = System.currentTimeMillis();
        String neededResourcesString = """
                SELECT FR.RATE
                FROM postgresdb.FXRATES FR
                WHERE FR.CURRENCY_TO = ? AND FR.CURRENCY_FROM = ?
                """;
        PreparedStatement neededResourcesSt = ((Connection) connection).prepareStatement(neededResourcesString);
        neededResourcesSt.setString(1, fxTransaction.getFxAccount_from().getCurrency_code());
        neededResourcesSt.setString(2, fxTransaction.getFxAccount_to().getCurrency_code());
        ResultSet neededResourcesRS = neededResourcesSt.executeQuery();
        BigDecimal neededResources = null;
        while (neededResourcesRS.next()) {
            neededResources = neededResourcesRS.getBigDecimal(1).multiply(fxTransaction.getAmount());
        }
        neededResourcesRS.close();
        neededResourcesSt.close();
        //end = System.currentTimeMillis();

        //System.out.println("Second get: "+(end-start));

        String transactionStatus = "NEW";

        assert neededResources != null;
        if (neededResources.compareTo(availableBalance) > 0) {
            transactionStatus = "BLOCKED";
        }

        //start = System.currentTimeMillis();
        String insertTransactionString = """
                INSERT INTO postgresdb.FXTRANSACTION(ID,FXACCOUNT_FROM, FXACCOUNT_TO, AMOUNT, STATUS, ENTRY_DATE)
                VALUES(?,?,?,?,?,?)
                """;
        PreparedStatement insertTransactionSt = ((Connection) connection).prepareStatement(insertTransactionString);
        insertTransactionSt.setLong(1, fxTransaction.getId());
        insertTransactionSt.setLong(2, fxTransaction.getFxAccount_from().getId());
        insertTransactionSt.setLong(3, fxTransaction.getFxAccount_to().getId());
        insertTransactionSt.setBigDecimal(4, fxTransaction.getAmount());
        insertTransactionSt.setString(5, transactionStatus);
        insertTransactionSt.setDate(6, new Date(System.currentTimeMillis()));
        insertTransactionSt.executeUpdate();
        insertTransactionSt.close();

        ((Connection) connection).commit();

        //end = System.currentTimeMillis();
        //System.out.println("Insert: "+(end-start));

        return ExecutePaymentInfo.builder()
                .transactionID(fxTransaction.getId())
                .amountToGive(neededResources)
                .amountToReceive(fxTransaction.getAmount())
                .accountFrom(fxTransaction.getFxAccount_from().getId())
                .accountTo(fxTransaction.getFxAccount_to().getId())
                .build();
    }

    @Override
    public void executePayment(Object connection, ExecutePaymentInfo executePaymentInfo) throws SQLException {

        String fromAccountOldBalanceString = """
                SELECT BALANCE
                FROM postgresdb.FXACCOUNT
                WHERE ID = ?
                """;
        PreparedStatement fromAccountBalanceSt = ((Connection) connection).prepareStatement(fromAccountOldBalanceString);
        fromAccountBalanceSt.setLong(1, executePaymentInfo.getAccountFrom());
        ResultSet fromAccountOldBalanceRS = fromAccountBalanceSt.executeQuery();
        BigDecimal oldBalance = null;
        while (fromAccountOldBalanceRS.next()) {
            oldBalance = fromAccountOldBalanceRS.getBigDecimal(1);
        }


        //	-	azuriranje balansa accounta	(write)
        String updateFromAccountString = """
                UPDATE postgresdb.FXACCOUNT
                SET BALANCE = ?
                WHERE id = ?
                """;
        PreparedStatement updateFromAccountSt = ((Connection) connection).prepareStatement(updateFromAccountString);
        assert oldBalance != null;
        updateFromAccountSt.setBigDecimal(1, oldBalance.subtract(executePaymentInfo.getAmountToGive()));
        updateFromAccountSt.setLong(2, executePaymentInfo.getAccountFrom());
        updateFromAccountSt.executeUpdate();
        //	-	azuriranje statusa transakcije (write)

        String toAccountOldBalanceString = """
                SELECT BALANCE
                FROM postgresdb.FXACCOUNT
                WHERE ID = ?
                """;
        PreparedStatement toAccountBalanceSt = ((Connection) connection).prepareStatement(toAccountOldBalanceString);
        toAccountBalanceSt.setLong(1, executePaymentInfo.getAccountTo());
        ResultSet toAccountOldBalanceRS = toAccountBalanceSt.executeQuery();
        BigDecimal toAccountOldBalance = null;
        while (toAccountOldBalanceRS.next()) {
            toAccountOldBalance = toAccountOldBalanceRS.getBigDecimal(1);
        }

        String updateToAccountString = """
                UPDATE postgresdb.FXACCOUNT
                SET BALANCE = ?
                WHERE id = ?
                """;
        PreparedStatement updateToAccountSt = ((Connection) connection).prepareStatement(updateToAccountString);
        assert toAccountOldBalance != null;
        updateToAccountSt.setBigDecimal(1, toAccountOldBalance.add(executePaymentInfo.getAmountToReceive()));
        updateToAccountSt.setLong(2, executePaymentInfo.getAccountTo());
        updateToAccountSt.executeUpdate();

        String updateTransactionStatus = """
                UPDATE postgresdb.FXTRANSACTION
                SET STATUS = ?
                WHERE id = ?
                """;
        PreparedStatement updateTransactionStatusSt = ((Connection) connection).prepareStatement(updateTransactionStatus);
        updateTransactionStatusSt.setString(1, "PROCESSED");
        updateTransactionStatusSt.setLong(2, executePaymentInfo.getTransactionID());
        updateTransactionStatusSt.executeUpdate();


        fromAccountOldBalanceRS.close();
        fromAccountBalanceSt.close();
        updateFromAccountSt.close();
        toAccountOldBalanceRS.close();
        toAccountBalanceSt.close();
        updateToAccountSt.close();
        ((Connection) connection).commit();
    }

    @Override
    public String checkTransactionStatus(Object connection, FXTransaction fxTransaction) throws SQLException {
        String checkTransactionStatusString = """
                SELECT STATUS
                FROM postgresdb.FXTRANSACTION
                WHERE ID = ?
                """;
        PreparedStatement checkTransactionStatusSt = ((Connection) connection).prepareStatement(checkTransactionStatusString);
        checkTransactionStatusSt.setLong(1, fxTransaction.getId());
        ResultSet checkTransactionStatusRS = checkTransactionStatusSt.executeQuery();
        String status = null;
        while (checkTransactionStatusRS.next()) {
            status = checkTransactionStatusRS.getString(1);
        }

        return status;

    }

    @Override
    public void executeRandomAccess(Object connection, int maxId) throws Exception {
//        String checkTransactionStatusString = """
//                SELECT t.amount, t.status
//                FROM postgresdb.FXTRANSACTION t
//                WHERE t.status = ?
//                """;
//        PreparedStatement checkTransactionStatusSt =  ((Connection) connection).prepareStatement(checkTransactionStatusString);
//        checkTransactionStatusSt.setLong(1, maxId/2);
//        ResultSet checkTransactionStatusRS = checkTransactionStatusSt.executeQuery();
//        String status=null;
//        while (checkTransactionStatusRS.next()) {
//            status = checkTransactionStatusRS.getString(1);
//        }
    }

    @Override
    public void executeRandomAccessWithoutIndex(Object object) throws Exception {
        String checkTransactionStatusString = """
                SELECT fa.balance
                FROM postgresdb.FXACCOUNT fa
                WHERE fa.CURRENCY_CODE='EUR' AND fa.fxuser=15000
                """;
        PreparedStatement checkTransactionStatusSt = ((Connection) object).prepareStatement(checkTransactionStatusString);
        ResultSet checkTransactionStatusRS = checkTransactionStatusSt.executeQuery();
        String balance = null;
        while (checkTransactionStatusRS.next()) {
            balance = checkTransactionStatusRS.getString(1);
        }
    }

    @Override
    public void executeInsert(Object connection) throws SQLException {
        String insertTransactionString = """
                INSERT INTO postgresdb.FXTRANSACTION(ID,FXACCOUNT_FROM, FXACCOUNT_TO, AMOUNT, STATUS, ENTRY_DATE)
                VALUES(?,?,?,?,?,?)
                """;
        PreparedStatement insertTransactionSt = ((Connection) connection).prepareStatement(insertTransactionString);
        insertTransactionSt.setLong(1, 1000000);
        insertTransactionSt.setLong(2, 250);
        insertTransactionSt.setLong(3, 300);
        insertTransactionSt.setBigDecimal(4, new BigDecimal(1000));
        insertTransactionSt.setString(5, "NEW");
        insertTransactionSt.setDate(6, new Date(System.currentTimeMillis()));
        insertTransactionSt.executeUpdate();
        insertTransactionSt.close();
        ((Connection) connection).commit();
    }

    @Override
    public void testConsistency(Object connection) throws SQLException {

/*
        1. sum(FXACCOUNT_RATE*FXACCOUNT_BALANCE) = START_BALANCE
        2. svaki slog u history tabeli mora imati odgovarajuci slog u fxtransaction tabeli U ISTOM STATUSU

        Nakon svakih 10 transakcija uradimo test konzistentosti
*/

        String selectUsersSQL = """
                SELECT FU.ID USERID, FU.START_BALANCE START_BALANCE, FU.START_BALANCECURRENCY START_BALANCECURRENCY
                FROM postgresdb.FXUSER FU
                """;
        Statement statement = ((Connection) connection).createStatement();
        ResultSet rs = statement.executeQuery(selectUsersSQL);
        while (rs.next()) {
            long userID = rs.getLong("USERID");
            BigDecimal startBalance = rs.getBigDecimal("START_BALANCE");
            String startBalanceCurrency = rs.getString("START_BALANCECURRENCY");

            String totalFromAccountsSQL = """
                    SELECT SUM(FA.BALANCE*FR.RATE) TOTAL_BALANCE
                    FROM postgresdb.FXACCOUNT FA, postgresdb.FXRATES FR
                    WHERE FA.FXUSER = ? AND FR.CURRENCY_TO=? AND FR.CURRENCY_FROM=FA.CURRENCY_CODE
                    """;
            PreparedStatement selectTotalFromAccounts = ((Connection) connection).prepareStatement(totalFromAccountsSQL);
            selectTotalFromAccounts.setLong(1, userID);
            selectTotalFromAccounts.setString(2, startBalanceCurrency);

            ResultSet totalFromAccountsRS = selectTotalFromAccounts.executeQuery();
            BigDecimal totalBalance;
            while (totalFromAccountsRS.next()) {
                totalBalance = totalFromAccountsRS.getBigDecimal("TOTAL_BALANCE");
                if (!Objects.equals(totalBalance, startBalance))
                    throw new AssertionError("Total balance and start balance do not match for user: " + userID);
            }

        }

    }
}
