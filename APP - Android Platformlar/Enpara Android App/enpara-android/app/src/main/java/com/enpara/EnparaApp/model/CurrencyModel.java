package com.enpara.EnparaApp.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilhan.kaya on 6/3/2017.
 */

@IgnoreExtraProperties
public class CurrencyModel {
    private Double buy = 0.0;
    private Double sell = 0.0;

    public CurrencyModel() {

    }

    public CurrencyModel(Double buy, Double sell) {
        this.buy = buy;
        this.sell = sell;
    }

    public Double getBuy() {
        return buy;
    }

    public Double getSell() {
        return sell;
    }

    public void setBuy(Double buy) {
        this.buy = buy;
    }

    public void setSell(Double sell) {
        this.sell = sell;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("buy", buy);
        result.put("sell", sell);
        return result;
    }

}
