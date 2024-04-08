package org.matf.master.luka.postgres;

import org.matf.master.luka.common.BenchmarkOLTPUtility;
import org.matf.master.luka.common.model.FXTransaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresBenchmarkOLTPUtility implements BenchmarkOLTPUtility {

    @Override
    public void createFXTransaction(FXTransaction fxTransaction) {

    }

    @Override
    public void executePayment(FXTransaction fxTransaction) {

    }

    @Override
    public void checkTransactionStatus(FXTransaction fxTransaction) {

    }

    @Override
    public void testConsistency() throws SQLException {

/*
        1. sum(FXACCOUNT_RATE*FXACCOUNT_BALANCE) = START_BALANCE
        2. svaki slog u history tabeli mora imati odgovarajuci slog u fxtransaction tabeli U ISTOM STATUSU

        Nakon svakih 10 transakcija uradimo test konzistentosti
*/

        String selectUsersSQL = """
                SELECT FU.ID USERID, FU.START_BALANCE START_BALANCE, FU.START_BALANCECURRENCY START_BALANCECURRENCY
                FROM postgresdb.FXUSER FU
                """;
        Statement statement = PostgresBenchmarkUtility.postgresSQLDriverConnection.createStatement();
        ResultSet rs = statement.executeQuery(selectUsersSQL);
        while(rs.next()){
            long userID = rs.getLong("USERID");
            double startBalance=rs.getDouble("START_BALANCE");
            String startBalanceCurrency = rs.getString("START_BALANCECURRENCY");

            String totalFromAccountsSQL = """
                    SELECT SUM(FA.BALANCE*FR.RATE) TOTAL_BALANCE
                    FROM postgresdb.FXACCOUNT FA, postgresdb.FXRATES FR
                    WHERE FA.FXUSER = ? AND FR.CURRENCY_TO=? AND FR.CURRENCY_FROM=FA.CURRENCY_CODE
                    """;
            PreparedStatement selectTotalFromAccounts = PostgresBenchmarkUtility.postgresSQLDriverConnection.prepareStatement(totalFromAccountsSQL);
            selectTotalFromAccounts.setLong(1,userID);
            selectTotalFromAccounts.setString(2, startBalanceCurrency);

            ResultSet totalFromAccountsRS = selectTotalFromAccounts.executeQuery();
            double totalBalance;
            while(totalFromAccountsRS.next()){
                totalBalance = totalFromAccountsRS.getDouble("TOTAL_BALANCE");
                if(totalBalance!=startBalance)
                    throw new AssertionError("Total balance and start balance do not match for user: "+userID);
            }

        }

    }

    @Override
    public void testAtomicity() {

/*
        Test atomicnosti

        1. Kreiramo transakciju i proverimo da li su vrednosti odogovarajue
        2. Umesto komita uradimo rollback, proverimo da li su vrednosti kao u pocetku
*/

    }
}
