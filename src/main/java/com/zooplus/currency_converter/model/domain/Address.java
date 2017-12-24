package com.zooplus.currency_converter.model.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

@Embeddable
public class Address {

    @Column(name = "country")
    @NotEmpty(message = "country can't be empty")
    private String country;

    @Column(name = "city")
    @NotEmpty(message = "city can't be empty")
    private String city;

    @Column(name = "street")
    @NotEmpty(message = "street can't be empty")
    private String street;

    @Column(name = "zip")
    @NotNull(message = "zip can't be empty")
    private Integer zip;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getZip() {
        return zip;
    }

    public void setZip(Integer zip) {
        this.zip = zip;
    }
}
