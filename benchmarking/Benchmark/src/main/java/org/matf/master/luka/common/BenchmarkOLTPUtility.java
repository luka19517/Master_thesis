package org.matf.master.luka.common;

import org.matf.master.luka.common.model.FXTransaction;

import java.math.BigDecimal;
import java.sql.SQLException;

public interface BenchmarkOLTPUtility {

    void createFXTransaction(FXTransaction fxTransaction);

    void executePayment(FXTransaction fxTransaction);

    void checkTransactionStatus(FXTransaction fxTransaction);

    void testConsistency() throws SQLException;

    void testAtomicity();

}
