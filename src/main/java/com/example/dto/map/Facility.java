package com.example.dto.map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Facility {
    @JsonProperty("place_name")
    private String name;

    @JsonProperty("address_name")
    private String address;

    private String latitude;
    private String longitude;

    public Facility(String name, String address, String longitude, String latitude) {
        this.name = name;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
