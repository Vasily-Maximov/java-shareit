package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.OrderState;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImp implements BookingService {

    private final JpaBookingRepository bookingRepository;

    private final JpaUserRepository userRepository;

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
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найден пользователь по id: %d", userId)));
        Item item = ItemMapper.toItem(itemService.getItemById(bookingDto.getItemId(), userId));
        if (itemService.getOwnerId(item.getId()).equals(userId)) {
            throw new ObjectNotFoundException(String.format("Арендатор по id = %d не должен быть владельцем бронируемой вещи",
                    userId));
        }
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingDto.getStart())
                    .end(bookingDto.getEnd())
                    .item(item)
                    .booker(user)
                    .status(BookingStatus.WAITING)
                    .build();
            log.info("Выполнен запрос на добавление нового бронирования от арендатора по id:= {}", userId);
            return BookingMapper.toBookingDto(bookingRepository.save(booking));
        } else {
            throw new OperationException(String.format("Вещь для аренды недоступна по id = %s", item.getId()));
        }
    }

    @Override
    public ResponseBookingDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Не найдено бронирование по id = %s", bookingId)));
        if (booking.getBooker().getId().equals(userId) || booking.getItem().getOwnerId().equals(userId)) {
            log.info("Выполнен запрос на получение информации бронирования по id:= {} от пользователя по id: {}", bookingId, userId);
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ObjectNotFoundException(String.format("Нет доступа к просмотру бронирования. Не найден владелец или арендатор" +
                    " вещи по id = %s", userId));
        }
    }

    @Override
    public List<ResponseBookingDto> getAllBookingsByUser(String state, Integer userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найден пользователь по id: %d", userId)));
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending()); //start_date
        LocalDateTime currentDateTime = LocalDateTime.now();
        OrderState orderState;
        try {
            orderState = OrderState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new OperationException(String.format("Unknown state: %s", state));
        }
        switch (orderState.getNameStatus()) {
            case "Получение бронирований":
                log.info("Выполнен запрос на получение бронирований арендатора по id:= {}", userId);
                return BookingMapper.toBookingDto(bookingRepository.findByBookerId(userId, pageable));
            case "Получение текущего бронирования":
                log.info("Выполнен запрос на получение текущего бронирования арендатора по id:= {}", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsAfterAndStartIsBefore(userId, currentDateTime, currentDateTime,
                                pageable));
            case "Получение завершённых бронирований":
                log.info("Выполнен запрос на получение завершённых бронирований арендатора по id:= {}", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndEndIsBefore(userId, currentDateTime, pageable));
            case "Получение будущих бронирований":
                log.info("Выполнен запрос на получение будущих бронирований арендатора по id:= {}", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfter(userId, currentDateTime, pageable));
            case "Получение бронирований ожидающих подтверждения":
                log.info("Выполнен запрос на получение бронирований ожидающих подтверждений арендатора по id:= {}", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStartIsAfterAndStatusIs(userId, currentDateTime,
                                BookingStatus.WAITING, pageable));
            case "Получение отклонённых бронирований":
                log.info("Выполнен запрос на получение отклонённых бронирований арендатора по id:= {}", userId);
                return BookingMapper.toBookingDto(bookingRepository
                        .findByBookerIdAndStatusIs(userId, BookingStatus.REJECTED, pageable));
            default:
                throw new OperationException(String.format("Unknown state: %s", state));
        }
    }

    @Override
    public List<ResponseBookingDto> getAllBookingsByOwner(String state, Integer ownerId, Integer from, Integer size) {
        userRepository.findById(ownerId).orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найден пользователь по id: %d", ownerId)));
        Pageable pageable = PageRequest.of(from / size, size);
        LocalDateTime currentDateTime = LocalDateTime.now();
        OrderState orderState;
        try {
            orderState = OrderState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new OperationException(String.format("Unknown state: %s", state));
        }
        switch (orderState.getNameStatus()) {
            case "Получение бронирований":
                log.info("Выполнен запрос на получение бронирований всех вещей владельца по id:= {}", ownerId);
                return BookingMapper.toBookingDto(bookingRepository.getByItemOwnerId(ownerId, pageable));
            case "Получение текущего бронирования":
                log.info("Выполнен запрос на получение текущих бронирований всех вещей владельца по id:= {}", ownerId);
                return BookingMapper.toBookingDto(bookingRepository.getCurrentBookingsOwner(ownerId, currentDateTime, pageable));
            case "Получение завершённых бронирований":
                log.info("Выполнен запрос на получение завершённых бронирований всех вещей владельца по id:= {}", ownerId);
                return BookingMapper.toBookingDto(bookingRepository.getPastBookingsOwner(ownerId, currentDateTime, pageable));
            case "Получение будущих бронирований":
                log.info("Выполнен запрос на получение будущих бронирований всех вещей владельца по id:= {}", ownerId);
                return BookingMapper.toBookingDto(bookingRepository.getFutureBookingsOwner(ownerId, currentDateTime, pageable));
            case "Получение бронирований ожидающих подтверждения":
                log.info("Выполнен запрос на получение бронирований ожидающих подтверждения для всех вещей владельца по id:= {}",
                        ownerId);
                return BookingMapper.toBookingDto(bookingRepository
                        .getWaitingBookingsOwner(ownerId, currentDateTime, BookingStatus.WAITING, pageable));
            case "Получение отклонённых бронирований":
                log.info("Выполнен запрос на получение отклонённых бронирований по всем вещам владельца по id:= {}", ownerId);
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
            throw new OperationException(String.format("Бронирование по id = %d уже подтверждено владельцем", bookingId));
        }
        if (!ownerId.equals(userId)) {
            throw new ObjectNotFoundException(String.format("Нет доступа к изменению статуса бронирования. Не найден владелец вещи" +
                    "по id = %s", userId));
        }
        if (approve) {
            booking.setStatus(BookingStatus.APPROVED);
            bookingRepository.save(BookingStatus.APPROVED, bookingId);
            log.info("Выполнен запрос изменения статуса бронирования на: {}", BookingStatus.APPROVED.getNameStatus());
        } else {
            booking.setStatus(BookingStatus.REJECTED);
            bookingRepository.save(BookingStatus.REJECTED, bookingId);
            log.info("Выполнен запрос изменения статуса бронирования на: {}", BookingStatus.REJECTED.getNameStatus());
        }
        return booking;
    }
}