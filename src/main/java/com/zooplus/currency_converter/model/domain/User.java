package com.zooplus.currency_converter.model.domain;

import java.sql.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "username")
    @NotEmpty(message = "Username can't be empty")
    private String username;

    @Column(name = "firstname")
    @NotEmpty(message = "First name can't be empty")
    private String firstName;

    @Column(name = "lastname")
    @NotEmpty(message = "Lastname can't be empty")
    private String lastName;

    @Column(name = "password")
    @NotEmpty(message = "password can't be empty")
    private String password;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Please provide a valid e-mail")
    @NotEmpty(message = "Please provide an e-mail")
    private String email;

    @Column(name = "birthday", nullable = false)
    @NotNull(message = "birthday can't be empty")
    private Date birthday;

    @Embedded
    @Valid
    private Address address = new Address();

    @OneToMany(fetch = FetchType.LAZY)
    private List<CurrencyExchangeRate> requests;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<CurrencyExchangeRate> getRequests() {
        return requests;
    }

    public void setRequests(List<CurrencyExchangeRate> requests) {
        this.requests = requests;
    }
}
