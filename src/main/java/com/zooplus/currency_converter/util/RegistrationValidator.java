package com.zooplus.currency_converter.util;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.zooplus.currency_converter.model.domain.User;
import com.zooplus.currency_converter.repository.UserRepository;

@Service
public class RegistrationValidator {

    @Autowired
    private UserRepository userRepository;

    public void validateRegistrationData(User user, BindingResult bindingResult) {
        validateBirthday(user, bindingResult);
        validateEmail(user, bindingResult);
        validateUsername(user, bindingResult);
        validateZip(user, bindingResult);
    }

    private void validateBirthday(User user, BindingResult bindingResult) {
        Date birthday = user.getBirthday();
        LocalDate userBirthday = LocalDate.parse(birthday.toString());
        LocalDate earliestDate = LocalDate.now().minus(110, ChronoUnit.YEARS);
        LocalDate latestDate = LocalDate.now().minus(10, ChronoUnit.YEARS);
        if (userBirthday.isBefore(earliestDate) || userBirthday.isAfter(latestDate)) {
            String errorMessage = String.format("Birthday must be in range between %s and %s", earliestDate, latestDate);
            bindingResult.rejectValue("birthday", "user.birthday", errorMessage);
        }
    }

    private void validateEmail(User user, BindingResult bindingResult) {
        String email = user.getEmail();
        User existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            bindingResult.rejectValue("email", "user.email", "User with email " + email + " already exists");
        }
    }

    private void validateUsername(User user, BindingResult bindingResult) {
        String username = user.getUsername();
        User existingUser = userRepository.findByUsername(username);
        if (existingUser != null) {
            bindingResult.rejectValue("username", "user.username", "User with name " + username + " already exists");
        }
    }

    private void validateZip(User user, BindingResult bindingResult) {
        Integer zip = user.getAddress().getZip();
        if (zip.toString().length() != 5) {
            bindingResult.rejectValue("address.zip", "user.address.zip", "zip must be 5-digit number");
        }
    }

}
