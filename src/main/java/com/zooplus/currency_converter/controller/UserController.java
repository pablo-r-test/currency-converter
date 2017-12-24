package com.zooplus.currency_converter.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zooplus.currency_converter.model.exception.UserNotFoundException;
import com.zooplus.currency_converter.model.domain.CurrencyExchangeRate;
import com.zooplus.currency_converter.model.domain.User;
import com.zooplus.currency_converter.repository.ExchangeRateRepository;
import com.zooplus.currency_converter.repository.UserRepository;

@RestController
@RequestMapping("/users")
/**
 * REST endpoint for User entity
 */
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @RequestMapping(method = RequestMethod.GET)
    public List<User> getAllUsers() {
        List<User> result = new ArrayList<>();
        userRepository.findAll().forEach(result::add);
        return result;
    }

    @RequestMapping(method = RequestMethod.GET, path = "{userId}")
    public User getUser(@PathVariable Long userId) {
        return userRepository.findOne(userId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "{userId}/exchangeRateRequests")
    public List<CurrencyExchangeRate> getUserExchangeRateRequests(@PathVariable Long userId) {
        User user = userRepository.findOne(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        List<CurrencyExchangeRate> result = new ArrayList<>();
        exchangeRateRepository.findByUserIdOrderByIdDesc(userId).forEach(result::add);
        return result;
    }

}
