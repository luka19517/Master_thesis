package org.matf.master.luka.common;

import org.matf.master.luka.common.model.FXTransaction;

import java.math.BigDecimal;

public interface BenchmarkOLTPUtility {

    void createFXTransaction(FXTransaction fxTransaction);

    void executePayment(FXTransaction fxTransaction);

    void checkTransactionStatus(FXTransaction fxTransaction);

    void testConsistency();

    void testAtomicity();

}
