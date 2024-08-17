package org.matf.master.luka;

import org.matf.master.luka.model.FXAccount;
import org.matf.master.luka.model.FXTransaction;
import org.matf.master.luka.model.FXUser;

import java.math.BigDecimal;

public class DataOnDemandUtility {

    public static FXTransaction createFXTransaction(int seed) {
        return FXTransaction.builder().id(seed).amount(BigDecimal.TEN).fxAccount_from(generateFXAccountWithID(seed%120000)).fxAccount_to(generateFXAccountWithID((seed + 30000)%120000)).build();
    }

    private static FXAccount generateFXAccountWithID(long id) {
        return FXAccount.builder().id(id).currency_code(getCurrencyCode(id)).fxUser(generateFxUserWithUsername(id%30000)).build();
    }

    private static FXUser generateFxUserWithUsername(long id){
        return FXUser.builder().username("username_"+id).build();
    }

    private static String getCurrencyCode(long id) {
        String currencyCode;
        switch ((int) id / 30000) {
            case 0: {
                currencyCode = "EUR";
                break;
            }
            case 1: {
                currencyCode = "USD";
                break;
            }
            case 2: {
                currencyCode = "DIN";
                break;
            }
            default: {
                currencyCode = "CHF";
                break;
            }
        }

        return currencyCode;
    }

}
