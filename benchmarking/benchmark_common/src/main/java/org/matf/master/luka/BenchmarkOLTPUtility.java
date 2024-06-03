package org.matf.master.luka;

import org.matf.master.luka.model.ExecutePaymentInfo;
import org.matf.master.luka.model.FXTransaction;

import java.io.IOException;
import java.sql.SQLException;

public interface BenchmarkOLTPUtility {

    /*
    dohvati stanja sa ciljanih FXACCOUNT_FROM, FXACCOUNT_TO
    dohvati odgovarajuci valutni par
    dodaj novi slog u FXTRANSACTION tabelu u statusu NEW/BLOCKED.
    */
    ExecutePaymentInfo createFXTransaction(Object connection, FXTransaction fxTransaction) throws SQLException, IOException;

    /*
    azuriraj stanje oba naloga
    azuriraj status transakcije
     */
    void executePayment(Object connection, ExecutePaymentInfo fxTransaction) throws SQLException, IOException;

    /*
    dohvatanje statusa iz FXTRANSACTION
     */
    String checkTransactionStatus(Object connection, FXTransaction fxTransaction) throws SQLException, IOException;

    void testConsistency(Object connection) throws SQLException;
}
