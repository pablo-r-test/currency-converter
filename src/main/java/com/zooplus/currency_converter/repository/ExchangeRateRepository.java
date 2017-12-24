package com.zooplus.currency_converter.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.zooplus.currency_converter.model.domain.CurrencyExchangeRate;
import com.zooplus.currency_converter.model.domain.User;

@Repository("exchangeRateRepository")
public interface ExchangeRateRepository extends CrudRepository<CurrencyExchangeRate, Long> {

    List<CurrencyExchangeRate> findByUserIdOrderByIdDesc(Long userId);

    List<CurrencyExchangeRate> findTop10ByUserOrderByIdDesc(User user);

    List<CurrencyExchangeRate> findTop1BySourceAndDestinationOrderByIdDesc(String source, String destination);

}
