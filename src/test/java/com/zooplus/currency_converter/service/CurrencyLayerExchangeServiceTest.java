package com.zooplus.currency_converter.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.zooplus.currency_converter.repository.ExchangeRateRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class CurrencyLayerExchangeServiceTest {

    @Configuration
    static class AccountServiceTestContextConfiguration {
        @Bean
        public CurrencyExchangeService currencyExchangeService() {
            return new CurrencyLayerExchangeService();
        }

        @Bean
        public ExchangeRateRepository exchangeRateRepository() {
            return Mockito.mock(ExchangeRateRepository.class);
        }

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

    }

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Test
    public void testCurrentExchangeRate() {
        double usdToEurRate = currencyExchangeService.currentExchangeRate("USD", "EUR");
        Assert.assertTrue(usdToEurRate > 0.5);
        Assert.assertTrue(usdToEurRate < 1.5);
        double gbpToEurRate = currencyExchangeService.currentExchangeRate("GBP", "EUR");
        Assert.assertTrue(gbpToEurRate > 0.5);
        Assert.assertTrue(gbpToEurRate < 1.5);
    }

    @Test
    public void testHistoricalExchangeRate() {
        double usdToEurRate = currencyExchangeService.historicalExchangeRate("USD", "EUR", "2013-01-05");
        Assert.assertEquals(0.763568d, usdToEurRate, 0d);
        double gbpToEurRate = currencyExchangeService.historicalExchangeRate("GBP", "EUR", "2013-01-05");
        Assert.assertEquals(1.23045d, gbpToEurRate, 0d);
    }

}
