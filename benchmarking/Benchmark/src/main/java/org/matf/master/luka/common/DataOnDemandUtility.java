package org.matf.master.luka.common;

import org.matf.master.luka.common.model.FXAccount;
import org.matf.master.luka.common.model.FXRates;
import org.matf.master.luka.common.model.FXTransaction;
import org.matf.master.luka.common.model.FXUser;

public class DataOnDemandUtility {

    public static FXTransaction createFXTransaction(int seed){
        return FXTransaction.builder().build();
    }

    public static FXUser createFXUser(int seed){
        return FXUser.builder().build();
    }

    public static FXRates createFXRate(int seed){
        return FXRates.builder().build();

    }

    public static FXAccount createFXAccount(int seed){
        return FXAccount.builder().build();
    }
}
