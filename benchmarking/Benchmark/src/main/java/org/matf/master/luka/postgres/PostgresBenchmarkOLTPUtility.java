package org.matf.master.luka.postgres;

import org.matf.master.luka.common.BenchmarkOLTPUtility;
import org.matf.master.luka.common.model.FXTransaction;

public class PostgresBenchmarkOLTPUtility implements BenchmarkOLTPUtility {

    @Override
    public void createFXTransaction(FXTransaction fxTransaction) {

    }

    @Override
    public void executePayment(FXTransaction fxTransaction) {

    }

    @Override
    public void checkTransactionStatus(long fxTransaction) {

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

    @Override
    public void testAtomicity() {

/*
        Test atomicnosti

        1. Kreiramo transakciju i proverimo da li su vrednosti odogovarajue
        2. Umesto komita uradimo rollback, proverimo da li su vrednosti kao u pocetku
*/

    }
}
