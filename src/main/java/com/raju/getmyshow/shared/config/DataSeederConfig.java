package com.raju.getmyshow.shared.config;

import com.raju.getmyshow.booking.entity.SeatInventory;
import com.raju.getmyshow.booking.entity.SeatStatus;
import com.raju.getmyshow.booking.repository.SeatInventoryRepository;
import com.raju.getmyshow.eventcatalog.domain.CensorRating;
import com.raju.getmyshow.eventcatalog.domain.entity.*;
import com.raju.getmyshow.eventcatalog.domain.enums.*;
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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeederConfig {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
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

            Event event1 = createEvent("Inception (20xx)", "Sci-fi, Thriller", new String[]{"Sci-fi", "Thriller"});
            Event event2 = createEvent("The Dart Knight", "Action, Crime", new String[]{"Action", "Crime", "Thriller"});

            Venue venue = createVenue("Start Cineplex", "Dhaka", 5);

            Screen screen1 = createScreen(venue, "Screen 1 - IMAX", ScreenType.IMAX, 100);
            Screen screen2 = createScreen(venue, "Screen 2 - Regular", ScreenType.STANDARD, 80);

            List<Seat> screen1Seats = createSeatsForScreen(screen1, 10, 10); // 10 rows x 10 seats
            List<Seat> screen2Seats = createSeatsForScreen(screen2, 8, 10);  // 8 rows x 10 seats

            Show show1 = createShow(event1, screen1, "Mumbai", LocalDateTime.now().plusDays(1).withHour(14).withMinute(0));
            Show show2 = createShow(event1, screen1, "Mumbai", LocalDateTime.now().plusDays(1).withHour(18).withMinute(30));
            Show show3 = createShow(event2, screen2, "Mumbai", LocalDateTime.now().plusDays(2).withHour(15).withMinute(0));

            log.info("Data seeding completed.");
        };
    }

    private Venue createVenue(String name, String city, int screenCount) {
        Venue venue = Venue.builder()
                .name(name)
                .venueType(VenueType.CINEMA)
                .city(city)
                .area("Mahakhali")
                .address("SKS Tower , Mahakhali, Dhaka")
                .pinCode("1212")
                .state("Dhaka")
                .country("Bangladesh")
                .totalScreens(screenCount)
                .status(VenueStatus.ACTIVE)
                .openingTime(LocalTime.of(9, 0))
                .closingTime(LocalTime.of(23, 0))
                .build();
        venue = venueRepository.save(venue);
        log.info("✅ Created venue: {}", name);
        return venue;
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
                .censorRating(CensorRating.U)
                .countryOfOrigin("USA")
                .originalLanguage("English")
                .imdbRating(new BigDecimal("8.8"))
                .releaseDate(LocalDate.of(2010, 7, 16))
                .formats(new String[]{"2D", "3D", "IMAX"})
                .build();
        movieRepository.save(movie);

        log.info("Created movie event: {}", title);
        return event;
    }

    private Screen createScreen(Venue venue, String name, ScreenType type, int totalSeats) {
        int rows = totalSeats / 10;

        Screen screen = Screen.builder()
                .venue(venue)
                .name(name)
                .screenType(type)
                .totalSeats(totalSeats)
                .totalRows(rows)
                .seatsPerRow(10)
                .layoutType(LayoutType.STANDARD)
                .screenStatus(ScreenStatus.ACTIVE)
                .build();
        screen = screenRepository.save(screen);
        log.info("✅ Created screen: {} with {} seats", name, totalSeats);
        return screen;
    }

    private List<Seat> createSeatsForScreen(Screen screen, int rows, int seatsPerRow) {
        List<Seat> seats = new ArrayList<>();

        for (int row = 0; row < rows; row++) {
            String rowLabel = String.valueOf((char) ('A' + row)); // A, B, C, ...

            for (int seatNum = 1; seatNum <= seatsPerRow; seatNum++) {
                SeatType seatType = determineSeatType(row, rows);

                Seat seat = Seat.builder()
                        .screen(screen)
                        .rowLabel(rowLabel)
                        .seatNumber(seatNum)
                        .seatType(seatType)
                        .displayLabel(rowLabel + seatNum)
                        .isAccessible(false)
                        .isAisle(seatNum == 1 || seatNum == seatsPerRow)
                        .isBlocked(false)
                        .build();
                seats.add(seat);
            }
        }

        seatRepository.saveAll(seats);
        log.info("✅ Created {} seats for screen: {}", seats.size(), screen.getName());
        return seats;
    }

    private SeatType determineSeatType(int rowIndex, int totalRows) {
        if (rowIndex < totalRows * 0.3) {
            return SeatType.REGULAR;
        } else if (rowIndex < totalRows * 0.7) {
            return SeatType.PREMIUM;
        } else {
            return SeatType.VIP;
        }
    }

    private void createSeatInventory(Show show, List<Seat> seats) {
        List<SeatInventory> inventory = new ArrayList<>();

        for (Seat seat : seats) {
            BigDecimal price = calculatePrice(show.getBasePrice(), seat.getSeatType());

            SeatInventory seatInventory = SeatInventory.builder()
                    .showId(show.getId())
                    .seatId(seat.getId())
                    .price(price)
                    .seatStatus(SeatStatus.AVAILABLE)
                    .version(0L)
                    .build();
            inventory.add(seatInventory);
        }

        seatInventoryRepository.saveAll(inventory);
        log.info("✅ Created {} seat inventory records for show: {}",
                inventory.size(), show.getEventTitle());
    }

    private BigDecimal calculatePrice(BigDecimal basePrice, SeatType seatType) {
        return switch (seatType) {
            case REGULAR -> basePrice;
            case PREMIUM -> basePrice.multiply(new BigDecimal("1.5"));
            case VIP -> basePrice.multiply(new BigDecimal("2.0"));
            case RECLINER -> basePrice.multiply(new BigDecimal("2.5"));
            default -> basePrice;
        };
    }

    private Show createShow(Event event, Screen screen, String city, LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusMinutes(150); // Assume 150 min movie

        Show show = Show.builder()
                .event(event)
                .screen(screen)
                .city(city)
                .eventTitle(event.getTitle())
                .venueName(screen.getVenue().getName())
                .eventType(event.getEventType().name())
                .startTime(startTime)
                .endTime(endTime)
                .basePrice(new BigDecimal("250.00"))
                .totalSeats(screen.getTotalSeats())
                .availableSeats(screen.getTotalSeats())
                .showStatus(ShowStatus.BOOKING_OPEN)
                .bookingOpensAt(LocalDateTime.now())
                .bookingClosesAt(startTime.minusHours(1))
                .build();
        show = showRepository.save(show);
        log.info("✅ Created show: {} at {}", event.getTitle(), startTime);
        return show;
    }
}
