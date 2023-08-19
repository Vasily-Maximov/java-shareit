package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.OperationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImp implements BookingService {

    private final JpaBookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public ResponseBookingDto add(BookingDto bookingDto, Integer userId) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().equals(bookingDto.getStart())) {
            throw new OperationException(String
                    .format("Ошибка, неверно указанны даты начала и конца бронирования: начало = %s конец = %s",
                            bookingDto.getStart(), bookingDto.getEnd()));
        }
        User user = UserMapper.toUser(userService.getById(userId));
        Item item = ItemMapper.toItem(itemService.getItemById(bookingDto.getItemId(), userId));
        if (itemService.getOwnerId(item.getId()).equals(userId)) {
            throw new ObjectNotFoundException("Покупатель не может быть владельцем");
        }
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDto.getStart())
                    .end(bookingDto.getEnd())
                    .item(item)
                    .booker(user)
                    .status(BookingStatus.WAITING)
                    .build();
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new OperationException(String.format("Вещь недоступна по id = %s", item.getId()));
        }
    }

    @Override
    @Transactional
    public ResponseBookingDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Не найдено бронирование по id = %s", bookingId)));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ObjectNotFoundException(String.format("Нет доступа к бронированию. Не найден владелец вещи по id = " +
                    "%s", userId));
        }
    }

    @Override
    @Transactional
    public List<ResponseBookingDto> getAllBookingsByUser(String state, Integer userId, Integer from, Integer size) {
        userService.getById(userId);
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.findByBookerIdOrderByStartDesc(userId, pageable));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(userId, now, now, pageable));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsBeforeOrderByStartDesc(userId, now, pageable));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterOrderByStartDesc(userId, now, pageable));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(userId, now,
                                BookingStatus.WAITING, pageable));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStatusIsOrderByStartDesc(userId, BookingStatus.REJECTED, pageable));

        }
        throw new OperationException(String.format("Unknown state: %s", state));
    }

    @Override
    @Transactional
    public List<ResponseBookingDto> getAllBookingsByOwner(String state, Integer ownerId, Integer from, Integer size) {
        userService.getById(ownerId);
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime now = LocalDateTime.now();
        switch (state) {
            case "ALL":
                return BookingMapper.toBookingDto(bookingRepository.getByItemOwnerId(ownerId, pageable));
            case "CURRENT":
                return BookingMapper.toBookingDto(bookingRepository.getCurrentBookingsOwner(ownerId, now, pageable));
            case "PAST":
                return BookingMapper.toBookingDto(bookingRepository.getPastBookingsOwner(ownerId, now, pageable));
            case "FUTURE":
                return BookingMapper.toBookingDto(bookingRepository.getFutureBookingsOwner(ownerId, now, pageable));
            case "WAITING":
                return BookingMapper.toBookingDto(bookingRepository
                        .getWaitingBookingsOwner(ownerId, now, BookingStatus.WAITING, pageable));
            case "REJECTED":
                return BookingMapper.toBookingDto(bookingRepository
                        .getRejectedBookingsOwner(ownerId, BookingStatus.REJECTED, pageable));
        }
        throw new OperationException(String.format("Unknown state: %s", state));
    }

    @Override
    @Transactional
    public ResponseBookingDto approve(Integer bookingId, Integer userId, Boolean approve) {
        ResponseBookingDto booking = getBookingById(bookingId, userId);
        Integer ownerId = itemService.getOwnerId(booking.getItem().getId());
        if (ownerId.equals(userId)
                && booking.getStatus().equals(BookingStatus.APPROVED)) {
            throw new OperationException("Бронирование уже принято");
        }
        if (!ownerId.equals(userId)) {
            throw new ObjectNotFoundException(String.format("Нет доступа к бронированию. Не найден владелец вещи по id = " +
                    "%s", userId));
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(BookingStatus.APPROVED, bookingId);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(BookingStatus.REJECTED, bookingId);
        }
        return booking;
    }
}