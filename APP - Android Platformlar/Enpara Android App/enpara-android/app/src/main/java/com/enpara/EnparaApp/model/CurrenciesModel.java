package com.enpara.EnparaApp.model;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by ilhan.kaya on 6/3/2017.
 */

@IgnoreExtraProperties
public class CurrenciesModel {
    private CurrencyModel usd;
    private CurrencyModel eur;
    private CurrencyModel gold;

    public CurrenciesModel() {

    }

    public CurrenciesModel(CurrencyModel usd, CurrencyModel eur, CurrencyModel gold) {
        this.usd = usd;
        this.eur = eur;
        this.gold = gold;
    }

    public CurrencyModel getUsd() {
        return usd;
    }

    public CurrencyModel getEur() {
        return eur;
    }

    public CurrencyModel getGold() {
        return gold;
    }
}
