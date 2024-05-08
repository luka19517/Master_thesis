package org.matf.master.luka.common;

import org.matf.master.luka.common.model.FXAccount;
import org.matf.master.luka.common.model.FXTransaction;

import java.math.BigDecimal;

public class DataOnDemandUtility {

    public static FXTransaction createFXTransaction(int seed) {
        return FXTransaction.builder().id(seed).amount(BigDecimal.TEN).fxAccount_from(generateFXAccountWithID(seed)).fxAccount_to(generateFXAccountWithID(seed+30000)).build();
    }

    private static FXAccount generateFXAccountWithID(long id){
        return FXAccount.builder().id(id).currency_code(getCurrencyCode(id)).build();
    }

    private static String getCurrencyCode(long id){
        String currencyCode;
        switch ((int) id / 30000) {
            case 0 -> {
                currencyCode = "EUR";
            }
            case 1 -> {
                currencyCode = "USD";
            }
            case 2 -> {
                currencyCode = "DIN";
            }
            default -> {
                currencyCode = "CHF";
            }
        }

        return currencyCode;
    }

}
