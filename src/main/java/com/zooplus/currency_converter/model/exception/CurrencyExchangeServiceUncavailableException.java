package com.zooplus.currency_converter.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class CurrencyExchangeServiceUncavailableException extends RuntimeException{

    public CurrencyExchangeServiceUncavailableException(String message) {
        super(message);
    }

}
