package org.springframework.data.orient.document.repository;

import com.orientechnologies.orient.core.annotation.OId;
import com.orientechnologies.orient.core.annotation.OVersion;

public class Address {

    @OId
    private String id;
    
    @OVersion
    private Long version;
    
    private String country;
    
    private String city;
    
    private String street;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
}
