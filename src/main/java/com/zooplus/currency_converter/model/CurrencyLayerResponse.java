package com.zooplus.currency_converter.model;

import java.util.Map;

/**
 * POJO for currency exchange rate response from CurrencyLayer service
 */
public class CurrencyLayerResponse {

    private Map<String, Double> quotes;

    public Map<String, Double> getQuotes() {
        return quotes;
    }

    public void setQuotes(Map<String, Double> quotes) {
        this.quotes = quotes;
    }
}
