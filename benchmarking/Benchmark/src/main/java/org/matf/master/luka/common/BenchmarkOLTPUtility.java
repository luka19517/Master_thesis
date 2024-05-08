package org.matf.master.luka.common;

import org.matf.master.luka.common.model.ExecutePaymentInfo;
import org.matf.master.luka.common.model.FXTransaction;

import java.sql.SQLException;

public interface BenchmarkOLTPUtility {

    /*
    dohvati stanja sa ciljanih FXACCOUNT_FROM, FXACCOUNT_TO
    dohvati odgovarajuci valutni par
    dodaj novi slog u FXTRANSACTION tabelu u statusu NEW/BLOCKED.
    */
    ExecutePaymentInfo createFXTransaction(FXTransaction fxTransaction) throws SQLException;

    /*
    azuriraj stanje oba naloga
    azuriraj status transakcije
     */
    void executePayment(ExecutePaymentInfo fxTransaction) throws SQLException;

    /*
    dohvatanje statusa iz FXTRANSACTION
     */
    void checkTransactionStatus(FXTransaction fxTransaction);

    void testConsistency() throws SQLException;
}
