package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import java.util.List;

public interface BookingService {

    ResponseBookingDto add(BookingDto bookingDto, Integer userId);

    ResponseBookingDto getBookingById(Integer bookingId, Integer userId);

    List<ResponseBookingDto> getAllBookingsByUser(String state, Integer userId, Integer from, Integer size);

    List<ResponseBookingDto> getAllBookingsByOwner(String state, Integer ownerId, Integer from, Integer size);

    ResponseBookingDto approve(Integer bookingId, Integer userId, Boolean approve);
}