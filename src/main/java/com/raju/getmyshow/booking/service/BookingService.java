package com.raju.getmyshow.booking.service;

import com.raju.getmyshow.booking.config.BookingProperties;
import com.raju.getmyshow.booking.dto.request.CreateBookingRequest;
import com.raju.getmyshow.booking.dto.response.AvailableSeatsResponse;
import com.raju.getmyshow.booking.dto.response.BookedSeatDto;
import com.raju.getmyshow.booking.dto.response.BookingResponse;
import com.raju.getmyshow.booking.dto.response.SeatDto;
import com.raju.getmyshow.booking.entity.*;
import com.raju.getmyshow.booking.repository.BookingRepository;
import com.raju.getmyshow.booking.repository.BookingSeatRepository;
import com.raju.getmyshow.booking.repository.SeatInventoryRepository;
import com.raju.getmyshow.eventcatalog.domain.entity.Seat;
import com.raju.getmyshow.eventcatalog.domain.entity.Show;
import com.raju.getmyshow.eventcatalog.repository.SeatRepository;
import com.raju.getmyshow.eventcatalog.repository.ShowRepository;
import com.raju.getmyshow.shared.exception.BusinessException;
import com.raju.getmyshow.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final SeatInventoryRepository seatInventoryRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    private final BookingProperties bookingProperties;

    @Transactional(readOnly = true)
    public AvailableSeatsResponse getAvailableSeats(Long showId) {

        Show show = showRepository.findById(showId).orElseThrow(() -> {
            return new NotFoundException("Show", showId);
        });

        List<SeatInventory> seatInventoryList = seatInventoryRepository.findByShowIdAndSeatStatus(showId,
                SeatStatus.AVAILABLE);

        List<Long> seatIds = seatInventoryList.stream()
                .map(new TestImpl())
                .toList();

        Map<Long, Seat> seatMap = seatRepository.findAllById(seatIds)
                .stream()
                .collect(Collectors.toMap(Seat::getId, seat -> seat));

        List<SeatDto> seatDtoList = seatInventoryList.stream().map((inventory) -> {

            Seat seat = seatMap.get(inventory.getSeatId());

            return SeatDto.builder()
                    .seatInventoryId(inventory.getId())
                    .rowLabel(seat.getRowLabel())
                    .seatNumber(seat.getSeatNumber())
                    .seatType(seat.getSeatType().name())
                    .price(inventory.getPrice())
                    .isAccessible(seat.getIsAccessible())
                    .isAisle(seat.getIsAisle())
                    .displayLabel(seat.getDisplayLabel())
                    .build();

        }).toList();

        return AvailableSeatsResponse.builder()
                .showId(showId)
                .eventTitle(show.getEventTitle())
                .totalSeats(show.getTotalSeats())
                .availableSeats(show.getAvailableSeats())
                .seats(seatDtoList)
                .build();
    }

    /**
     * 💡 CREATE BOOKING (Naive - NO locking yet!)
     * WARNING: This has race conditions!
     * - Two users can book same seats simultaneously
     */
    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {

        log.info("Creating booking for user: {}, show: {}, seats: {}",
                request.getUserId(),
                request.getShowId(),
                request.getSeatInventoryIds());

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new NotFoundException("Invalid seat requested", request.getShowId()));

        if (!show.isBookable()) {
            throw new BusinessException("SHOW_NOT_BOOKABLE", "Show is not available for booking");
        }

        List<SeatInventory> seatInventoryList = seatInventoryRepository.findAllById(request.getSeatInventoryIds());

        if (seatInventoryList.size() != request.getSeatInventoryIds().size()) {
            throw new BusinessException("INVALID_SEATS", "Some seats not found");
        }

        for (SeatInventory seatInventory : seatInventoryList) {
            if (seatInventory.getSeatStatus() != SeatStatus.AVAILABLE) {
                throw new BusinessException("SEAT_NOT_AVAILABLE",
                        "Seat " + seatInventory.getId() + " is not available");
            }
        }

        BigDecimal totalPrice = seatInventoryList.stream().map((currentSeat) -> {
            return currentSeat.getPrice();
        }).reduce(BigDecimal.ZERO, (currentSum, currentPrice) -> {
            return currentSum.add(currentPrice);
        });

        LocalDateTime lockExpiryTime = LocalDateTime.now().plusMinutes(bookingProperties.getExpiryInMinutes());

        for (SeatInventory currentSeats : seatInventoryList) {
            currentSeats.lock(request.getSessionId(), bookingProperties.getExpiryInMinutes());
        }

        seatInventoryRepository.saveAll(seatInventoryList);

        //Create Booking
        Booking booking = Booking.builder()
                .userId(request.getUserId())
                .showId(request.getShowId())
                .bookingReference(generateBookingReference())
                .status(BookingStatus.PENDING)
                .totalSeats(seatInventoryList.size())
                .totalAmount(totalPrice)
                .expiresAt(lockExpiryTime)
                .build();

        booking = bookingRepository.save(booking);

        //Create Booking_Seat entry for each seats
        List<BookingSeat> bookingSeatList = new ArrayList<>();
        for (SeatInventory seatInventory : seatInventoryList) {
            String seatLabel = seatRepository.findDisplayLabelById(seatInventory.getSeatId());
            BookingSeat bookingSeat = BookingSeat.builder()
                    .booking(booking)
                    .seatInventoryId(seatInventory.getId())
                    .seatLabel(seatLabel)
                    .price(seatInventory.getPrice())
                    .build();
            bookingSeatList.add(bookingSeat);
        }
        bookingSeatRepository.saveAll(bookingSeatList);

        // 8. Update show available seats
        show.decrementAvailableSeats(seatInventoryList.size());
        showRepository.save(show);

        log.info("Booking created: {}", booking.getBookingReference());


        return buildBookingResponse(booking, show, bookingSeatList, seatInventoryList);
    }

    // Helper methods

    private String generateBookingReference() {
        // Simple implementation - can be improved
        return "BK" + System.currentTimeMillis();
    }

    private BookingResponse buildBookingResponse(Booking booking, Show show,
                                                 List<BookingSeat> bookingSeatList,
                                                 List<SeatInventory> seatInventoryList) {

        List<BookedSeatDto> seats = seatInventoryList.stream().map((current) -> {

            BookingSeat bookingSeat = bookingSeatList.stream().filter((currentBookedSeat) -> {
                return currentBookedSeat.getSeatInventoryId().equals(current.getId());
            }).findFirst().orElse(null);

            return BookedSeatDto.builder()
                    .seatInventoryId(current.getId())
                    .seatLabel(bookingSeat.getSeatLabel())
                    .seatType(bookingSeat != null ? "PREMIUM" : "REGULAR") //TODO: Get actual type
                    .price(current.getPrice())
                    .build();
        }).toList();

        return BookingResponse.builder()
                .bookingId(booking.getId())
                .bookingReference(booking.getBookingReference())
                .eventTitle(show.getEventTitle())
                .showId(show.getId())
                .status(booking.getStatus())
                .seats(seats)
                .totalSeats(Long.valueOf(booking.getTotalSeats()))
                .totalAmount(booking.getTotalAmount())
                .showStartTime(show.getStartTime())
                .createdAt(booking.getCreatedAt())
                .expiresAt(booking.getExpiresAt())
                .build();
    }
}
