package com.enpara.EnparaApp.model;

import android.util.Log;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ilhan.kaya on 6/3/2017.
 */

@IgnoreExtraProperties
public class AlertModel {

    private String currency;
    private String alertType;
    private Double value;
    private Long time;
    private String key;

    public AlertModel() {

    }

    public AlertModel(String currency, String alertType, Double value) {
        this.currency = currency;
        this.alertType = alertType;
        this.value = value;
        this.time = (new Date()).getTime();
    }

    public String getCurrency() {
        return currency;
    }

    public String getAlertType() {
        return alertType;
    }

    public Double getValue() {
        return value;
    }

    public Long getTime() {
        return time;
    }

    public String getKey() {
        return key;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("currency", currency);
        result.put("alertType", alertType);
        result.put("value", value);
        result.put("time", time);
        return result;
    }

}
