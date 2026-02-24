package com.raju.getmyshow.booking.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "booking")
@Getter
@Setter
public class BookingProperties {

    private int expiryInMinutes;
    private int maxSeatsPerUser;
}