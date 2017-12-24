package com.zooplus.currency_converter.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.zooplus.currency_converter.model.domain.CurrencyExchangeRate;
import com.zooplus.currency_converter.model.domain.User;
import com.zooplus.currency_converter.repository.ExchangeRateRepository;
import com.zooplus.currency_converter.repository.UserRepository;
import com.zooplus.currency_converter.service.CountryService;
import com.zooplus.currency_converter.service.CurrencyExchangeService;
import com.zooplus.currency_converter.util.RegistrationValidator;

@Controller
/**
 * Main controller for CurrencyExchange app. It should be splitted to few separate controllers if more functionality is added.
 */
public class AppController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ExchangeRateRepository exchangeRateRepository;

    @Autowired
    private CurrencyExchangeService currencyExchangeService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private RegistrationValidator registrationValidator;

    private List<String> availableCurrencies = Stream.of("AUD", "EUR", "GBP", "JPY", "MYR", "NZD", "USD", "UAH").collect(Collectors.toList());

    @GetMapping("/")
    public ModelAndView home(ModelAndView modelAndView, CurrencyExchangeRate exchangeRate) {
        modelAndView.addObject("exchangerate", exchangeRate);
        modelAndView.addObject("availableCurrencies", availableCurrencies);
        modelAndView.addObject("exchangeRateRequests", getUsersRequests());
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @PostMapping("/exchangerate")
    public ModelAndView exchangeRate(ModelAndView modelAndView, @Valid CurrencyExchangeRate exchangeRate) {
        User user = getCurrentUser();
        exchangeRate.setUser(user);
        String date = exchangeRate.getDate();
        double currentExchangeRate;
        if (StringUtils.isEmpty(date) || (StringUtils.isEmpty(date) && LocalDate.parse(date).isEqual(LocalDate.now()))) {
            currentExchangeRate = currencyExchangeService.currentExchangeRate(exchangeRate.getSource(), exchangeRate.getDestination());
            exchangeRate.setDate(LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        }   else {
            currentExchangeRate = currencyExchangeService.historicalExchangeRate(exchangeRate.getSource(), exchangeRate.getDestination(), date);
        }
        exchangeRate.setQuote(currentExchangeRate);
        exchangeRateRepository.save(exchangeRate);
        modelAndView.addObject("exchangerate", new CurrencyExchangeRate());
        modelAndView.addObject("availableCurrencies", availableCurrencies);
        modelAndView.addObject("exchangeRateRequests", getUsersRequests());
        modelAndView.setViewName("home");
        return modelAndView;
    }

    @GetMapping(value = "/register")
    public ModelAndView showRegistrationPage(ModelAndView modelAndView, User user){
        modelAndView.addObject("user", user);
        modelAndView.addObject("countries", countryService.getAllAvailableCountries());
        modelAndView.setViewName("register");
        return modelAndView;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ModelAndView register(ModelAndView modelAndView, @Valid User user, BindingResult bindingResult) {
        registrationValidator.validateRegistrationData(user, bindingResult);
        if (bindingResult.hasErrors()) {
            modelAndView.addObject("countries", countryService.getAllAvailableCountries());
            modelAndView.setViewName("register");
            return modelAndView;
        }
        userRepository.save(user);
        modelAndView.setViewName("login");
        return modelAndView;
    }

    private List<CurrencyExchangeRate> getUsersRequests() {
        User currentUser = getCurrentUser();
        return exchangeRateRepository.findTop10ByUserOrderByIdDesc(currentUser);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal = auth.getPrincipal();
        String username = ((org.springframework.security.core.userdetails.User) principal).getUsername();
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username " + username + " not found");
        }
        return user;
    }

}
