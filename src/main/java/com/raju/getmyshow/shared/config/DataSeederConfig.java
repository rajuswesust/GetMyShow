package com.raju.getmyshow.shared.config;

import com.raju.getmyshow.booking.repository.SeatInventoryRepository;
import com.raju.getmyshow.eventcatalog.domain.CensorRating;
import com.raju.getmyshow.eventcatalog.domain.entity.Event;
import com.raju.getmyshow.eventcatalog.domain.entity.Movie;
import com.raju.getmyshow.eventcatalog.domain.enums.EventStatus;
import com.raju.getmyshow.eventcatalog.domain.enums.EventType;
import com.raju.getmyshow.eventcatalog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeederConfig {

    private final EventRepository eventRepository;
    private final MovieRepository movieRepository;
    private final SportsEventRepository sportsEventRepository;
    private final ShowRepository showRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;
    private final SeatInventoryRepository seatInventoryRepository;

    @Bean
    @Profile("dev")
    public CommandLineRunner seedData() {
        return args -> {
            log.info("Starting data seeding...");

            if(eventRepository.count() > 0) {
                log.info("Data already exists...");
                log.info("Quiting Data Seeding...");
                return;
            }

            Event event1 = createEvent("Inception (20xx)", "Sci-fi, Thriller", new String[]{"Sci-fi", "Thriller")});
            Event event2 = createEvent("The Dart Knight", "Action, Crime", new String[]{"Action", "Crime", "Thriller"});

            log.info("Data seeding completed.");
        };
    }

    private Event createEvent(String title, String description, String[] tags) {
        Event event =  Event.builder()
                .title(title)
                .description(description)
                .eventType(EventType.MOVIE)
                .status(EventStatus.PUBLISHED)
                .isFeatured(true)
                .publishedAt(LocalDateTime.now())
                .tags(tags)
                .build();

        event  = eventRepository.save(event);

        Movie movie = Movie.builder()
                .event(event)
                .duration(150)
                .genre("Action")
                .language("English")
                .director("Christopher Nolan")
                .censorRating(CensorRating.UA)
                .imdbRating(new BigDecimal("8.8"))
                .releaseDate(LocalDate.of(2010, 7, 16))
                .formats(new String[]{"2D", "3D", "IMAX"})
                .build();
        movieRepository.save(movie);

        log.info("✅ Created movie event: {}", title);
        return event;
    }
}
