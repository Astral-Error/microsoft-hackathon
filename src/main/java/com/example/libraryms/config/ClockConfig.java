package com.example.libraryms.config;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClockConfig {

    @Bean
    public Clock appClock() {
        return Clock.fixed(Instant.parse("2026-08-04T00:00:00Z"), ZoneId.of("UTC"));
    }

    @Bean
    public LocalDate demoToday(Clock appClock) {
        return LocalDate.now(appClock);
    }
}