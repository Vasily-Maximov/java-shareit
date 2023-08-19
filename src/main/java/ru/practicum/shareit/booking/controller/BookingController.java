package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.check.CreateGroup;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseBookingDto add(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                  @Validated(CreateGroup.class) @RequestBody BookingDto bookingDto) {
        log.info("Передан запрос на добавление нового бронирования вещи пользователем с ID: {}", userId);
        return bookingService.add(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getById(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable Integer bookingId) {
        log.info("Передан запрос на получение бронирования по id:= {}", bookingId);
        return bookingService.getBookingById(bookingId, ownerId);
    }

    @GetMapping
    public List<ResponseBookingDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Передан запрос на получение списка всех бронирований пользователя по id:= {}", ownerId);
        return bookingService.getAllBookingsByUser(state, ownerId, from, size);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getByOwnerId(@RequestHeader("X-Sharer-User-Id") Integer ownerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Передан запрос на получение списка всех бронирований владельца по id:= {}", ownerId);
        return bookingService.getAllBookingsByOwner(state, ownerId, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto update(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable Integer bookingId,
                                     @RequestParam Boolean approved) {
        log.info("Передан запрос на изменение бронирования по id:= {}", bookingId);
        return bookingService.approve(bookingId, ownerId, approved);
    }
}