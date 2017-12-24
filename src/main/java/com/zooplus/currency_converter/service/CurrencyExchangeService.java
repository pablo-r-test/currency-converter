package com.zooplus.currency_converter.service;

/**
 * Service for operations related to currenct exchange rates
 */
public interface CurrencyExchangeService {

    /**
     * @param sourceCurrency - currency to convert from
     * @param destincationCurrency - - currency to convert to
     * @return exchange rate
     */
    double currentExchangeRate(String sourceCurrency, String destincationCurrency);

    /**
     * @param sourceCurrency - currency to convert from
     * @param destincationCurrency - - currency to convert to
     * @param date - day of currency exchange rate
     * @return exchange rate
     */
    double historicalExchangeRate(String sourceCurrency, String destincationCurrency, String date);

}
