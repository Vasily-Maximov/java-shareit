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

@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingService bookingService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseBookingDto add(@RequestHeader(USER_ID) Integer userId,
                                  @Validated(CreateGroup.class) @RequestBody BookingDto bookingDto) {
        log.info("Передан запрос на добавление нового бронирования вещи пользователем с ID: {}", userId);
        return bookingService.add(bookingDto, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getById(@RequestHeader(USER_ID) Integer ownerId, @PathVariable Integer bookingId) {
        log.info("Передан запрос на получение информации бронирования по id:= {}", bookingId);
        return bookingService.getBookingById(bookingId, ownerId);
    }

    @GetMapping
    public List<ResponseBookingDto> getByUserId(@RequestHeader(USER_ID) Integer userId,
                                                @RequestParam(defaultValue = "ALL") String state,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Передан запрос на получение списка всех бронирований пользователя по id:= {}", userId);
        return bookingService.getAllBookingsByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getByOwnerId(@RequestHeader(USER_ID) Integer ownerId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Передан запрос на получение списка бронирований всех вещей владельца по id:= {}", ownerId);
        return bookingService.getAllBookingsByOwner(state, ownerId, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto update(@RequestHeader(USER_ID) Integer ownerId, @PathVariable Integer bookingId,
                                     @RequestParam Boolean approved) {
        log.info("Передан запрос на изменение статуса бронирования по id:= {}", bookingId);
        return bookingService.approve(bookingId, ownerId, approved);
    }
}