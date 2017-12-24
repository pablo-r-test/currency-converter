package com.zooplus.currency_converter.service;

import java.text.DecimalFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.zooplus.currency_converter.model.domain.CurrencyExchangeRate;
import com.zooplus.currency_converter.model.CurrencyLayerResponse;
import com.zooplus.currency_converter.repository.ExchangeRateRepository;

@Service
public class CurrencyLayerExchangeService implements CurrencyExchangeService {

    private final String CURRENT_URL = "http://www.apilayer.net/api/live?access_key=8e422d037e5d1085f2390091f77ff370&currencies=%s,%s&format=1";

    private final String HISTORICAL_URL = "http://www.apilayer.net/api/historical?access_key=8e422d037e5d1085f2390091f77ff370&currencies=%s,%s&date=%s&format=1";

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    @Cacheable("currentRates")
    @HystrixCommand(fallbackMethod = "findLatestRequestInDatabase")
    public double currentExchangeRate(String sourceCurrency, String destincationCurrency) {
        String url = buildCurrentRequestUrl(sourceCurrency, destincationCurrency);
        return executeCurrencyRequest(sourceCurrency, destincationCurrency, url);
    }

    @Override
    @Cacheable("historicalRates")
    public double historicalExchangeRate(String sourceCurrency, String destincationCurrency, String date) {
        String url = buildHistoricalRequestUrl(sourceCurrency, destincationCurrency, date);
        return executeCurrencyRequest(sourceCurrency, destincationCurrency, url);
    }

    public double findLatestRequestInDatabase(String sourceCurrency, String destincationCurrency, Throwable exceptionOccured) {
        List<CurrencyExchangeRate> latestHistoricalRequest = exchangeRateRepository.findTop1BySourceAndDestinationOrderByIdDesc(sourceCurrency, destincationCurrency);
        if (!latestHistoricalRequest.isEmpty()) {
            return latestHistoricalRequest.get(0).getQuote();
        }   else {
            // here another currency exchange service should be called
            throw new RuntimeException(exceptionOccured);
        }
    }

    private double executeCurrencyRequest(String sourceCurrency, String destincationCurrency, String url) {
        CurrencyLayerResponse forObject = restTemplate.getForObject(url, CurrencyLayerResponse.class);
        String sourceKey = ("USD" + sourceCurrency).toUpperCase();
        String destKey = ("USD" + destincationCurrency).toUpperCase();
        Double sourceExchangeRate = forObject.getQuotes().get(sourceKey);
        Double destExchangeRate = forObject.getQuotes().get(destKey);
        double result = destExchangeRate / sourceExchangeRate;
        DecimalFormat newFormat = new DecimalFormat("#.######");
        return Double.valueOf(newFormat.format(result));
    }

    private String buildHistoricalRequestUrl(String sourceCurrency, String destincationCurrency, String date) {
        return String.format(HISTORICAL_URL, sourceCurrency, destincationCurrency, date);
    }

    private String buildCurrentRequestUrl(String sourceCurrency, String destincationCurrency) {
        return String.format(CURRENT_URL, sourceCurrency, destincationCurrency);
    }

}
