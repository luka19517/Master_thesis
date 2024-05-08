package org.matf.master.luka.hbase;

import org.matf.master.luka.common.BenchmarkOLTPUtility;
import org.matf.master.luka.common.model.ExecutePaymentInfo;
import org.matf.master.luka.common.model.FXTransaction;

public class HBaseBenchmarkOLTPUtility implements BenchmarkOLTPUtility {

    @Override
    public ExecutePaymentInfo createFXTransaction(FXTransaction fxTransaction) {
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
