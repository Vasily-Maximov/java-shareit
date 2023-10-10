package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.OperationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
class BookingServiceImpTest {

    private BookingService bookingService;

    @Mock
    private ItemService itemService;

    @Mock
    private JpaBookingRepository bookingRepository;

    @Mock
    JpaUserRepository userRepository;

    @Mock
    JpaItemRepository itemRepository;

    private final User user1 = new User(1, "User", "user@email.com");

    @BeforeEach
    public void beforeEach() {
        bookingService = new BookingServiceImp(bookingRepository, userRepository, itemService);
    }

    private final BookingDto bookingDto = BookingDto.builder()
            .start(LocalDateTime.now())
            .end(LocalDateTime.now().plusHours(1L))
            .itemId(1)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1)
            .build();

    private final Item item = Item.builder()
            .id(1)
            .name("Item")
            .description("Description")
            .available(true)
            .ownerId(1)
            .build();

    private final Item item2 = Item.builder()
            .id(1)
            .name("Item2")
            .description("Description2")
            .available(true)
            .ownerId(2)
            .build();

    private final Booking booking1 = Booking.builder()
            .booker(user1)
            .id(1)
            .status(BookingStatus.APPROVED)
            .item(item).build();

    @Test
    void createBookingWhenTimeIsNotValidThenReturnedTimeDataException() {
        BookingDto bookingBadTime = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().minusHours(1))
                .itemId(1)
                .build();

        Exception e = assertThrows(OperationException.class,
                () -> bookingService.add(bookingBadTime, 1));
        assertEquals(e.getMessage(), String.format("Ошибка, неверно указанны даты начала и конца бронирования: начало = %s конец = %s",
                bookingBadTime.getStart(), bookingBadTime.getEnd()));
    }

    @Test
    void createBookingWhenUserIsNotOwnerThenReturnedOperationAccessException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        Mockito.when(itemService.getItemById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        Mockito.when(itemService.getOwnerId(anyInt()))
                .thenReturn(1);

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.add(bookingDto, 1));

        assertEquals(e.getMessage(), String.format("Арендатор по id = %d не должен быть владельцем бронируемой вещи", 1));
    }

    @Test
    void createBookingWhenItemIsNotAvailableThenReturnedNotAvailableException() {
        itemDto.setAvailable(false);
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        Mockito.when(itemService.getItemById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        Mockito.when(itemService.getOwnerId(anyInt()))
                .thenReturn(1);
        Exception e = assertThrows(OperationException.class,
                () -> bookingService.add(bookingDto, 2));

        assertEquals(e.getMessage(), String.format("Вещь для аренды недоступна по id = %s", 1));
    }

    @Test
    void findBookingByIdWhenBookingIsNotFoundThenReturnedNotFoundException() {
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(1, 1));

        assertEquals(e.getMessage(), String.format("Не найдено бронирование по id = %s", 1));
    }

    @Test
    void findBookingByIdWhenUserIsNotOwnerThenReturnedOperationAccessException() {
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking1));
        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.getBookingById(1, 100));

        assertEquals(e.getMessage(), String.format("Нет доступа к просмотру бронирования. Не найден владелец или арендатор" +
                " вещи по id = %s", 100));
    }

    @Test
    void getAllBookingsByUserIdWhenStateIsUnknownThenReturnedBadRequestException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        Exception e = assertThrows(OperationException.class,
                () -> bookingService.getAllBookingsByUser("trt", 1, 0, 10));

        assertEquals(e.getMessage(), "Unknown state: trt");
    }

    @Test
    void getAllBookingsByOwnerIdWhenStateIsUnknownThenReturnedBadRequestException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        Exception e = assertThrows(OperationException.class,
                () -> bookingService.getAllBookingsByOwner("trt", 1, 0, 10));

        assertEquals(e.getMessage(), "Unknown state: trt");
    }


    @ParameterizedTest
    @CsvSource(value = {
            "ALL, 0, 2",
            "CURRENT, -1, 1",
            "PAST, -2, -1",
            "FUTURE, 1, 2",
            "WAITING, 0, 1",
    })
    void getByUserIdAndStateTest(String state, int addToStart, int addToEnd) {
        LocalDateTime start = LocalDateTime.now().plusDays(addToStart);
        LocalDateTime end = LocalDateTime.now().plusDays(addToEnd);
        User testUser1 = new User(1, "User", "user@email.com");
        Booking testBooking = booking1;
        testBooking.setBooker(testUser1);
        testBooking.setStart(start);
        testBooking.setEnd(end);

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item2));
        Mockito.when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(testUser1));
        Mockito.when(bookingRepository.findByBookerId(Mockito.anyInt(), any()))
                .thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndEndIsAfterAndStartIsBefore(
                Mockito.anyInt(),any(), any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndEndIsBefore(Mockito.anyInt(),
                any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndStartIsAfter(Mockito.anyInt(),
                any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(Mockito.anyInt(),
                any(), any(), any())).thenReturn(List.of(testBooking));
        List<ResponseBookingDto> bookings = bookingService.getAllBookingsByUser(state, testUser1.getId(), 0, 10);
        assertFalse(bookings.isEmpty());
        assertEquals(bookings.get(0).getId(), 1);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ALL, 0, 2",
            "CURRENT, -1, 1",
            "PAST, -2, -1",
            "FUTURE, 1, 2",
    })
    void getByItemOwnerIdAndStateTest(String state, int addToStart, int addToEnd) {
        LocalDateTime start = LocalDateTime.now().plusDays(addToStart);
        LocalDateTime end = LocalDateTime.now().plusDays(addToEnd);
        User booker = new User(1, "User", "user@email.com");
        User itemOwner = new User(2, "User2", "user2@email.com");
        Item testItem = Item.builder()
                .id(1)
                .name("Item")
                .description("Description")
                .available(true)
                .ownerId(2)
                .build();
        Booking testBooking = Booking.builder()
                .booker(user1)
                .id(1)
                .status(BookingStatus.APPROVED)
                .item(item).build();
        testBooking.setBooker(booker);
        testBooking.setStart(start);
        testBooking.setEnd(end);
        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(testItem));
        Mockito.when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        Mockito.when(bookingRepository.getByItemOwnerId(Mockito.anyInt(), any()))
                .thenReturn(List.of(testBooking));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(itemOwner));
        Mockito.when(bookingRepository.getCurrentBookingsOwner(
                Mockito.anyInt(),any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.findByBookerIdAndEndIsBefore(Mockito.anyInt(),
                any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.getPastBookingsOwner(
                Mockito.anyInt(),any(), any())).thenReturn(List.of(testBooking));
        Mockito.when(bookingRepository.getFutureBookingsOwner(Mockito.anyInt(),
                any(), any())).thenReturn(List.of(testBooking));
        List<ResponseBookingDto> bookings = bookingService.getAllBookingsByOwner(state, itemOwner.getId(), 0, 10);
        assertEquals(1, bookings.size());
        assertEquals(bookings.get(0).getId(), 1);
    }

    @Test
    void approveWhenBookingDecisionThenReturnedAlreadyExistsException() {
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(itemService.getOwnerId(anyInt()))
                .thenReturn(1);
        Exception e = assertThrows(OperationException.class,
                () -> bookingService.approve(1, 1, true));

        assertEquals(e.getMessage(), String.format("Бронирование по id = %d уже подтверждено владельцем", 1));
    }

    @Test
    void approveWhenUserIsNotOwnerThenReturnedOperationAccessException() {
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(booking1));
        Mockito.when(itemService.getOwnerId(anyInt()))
                .thenReturn(2);
        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.approve(1, 1, true));

        assertEquals(e.getMessage(), String.format("Нет доступа к изменению статуса бронирования. Не найден владелец вещи" +
                "по id = %s", 1));
    }

    @Test
    void addTest() {
        Booking testBooking = booking1;
        testBooking.setStatus(BookingStatus.WAITING);
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user1));
        Mockito.when(itemService.getItemById(anyInt(), anyInt()))
                .thenReturn(itemDto);
        Mockito.when(itemService.getOwnerId(anyInt()))
                .thenReturn(1);
        Mockito.when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);

        ResponseBookingDto responseBookingDto = bookingService.add(bookingDto, 2);

        assertEquals(responseBookingDto.getBooker().getId(), user1.getId());
        assertEquals(responseBookingDto.getStatus(), BookingStatus.WAITING);
    }

    @Test
    void approveWhenUserIsOwner() {
        Booking testBooking = booking1;
        testBooking.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(testBooking));
        Mockito.when(itemService.getOwnerId(anyInt()))
                .thenReturn(1);
        ResponseBookingDto responseBookingDto =  bookingService.approve(1, 1, true);
        assertEquals(responseBookingDto.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    void approveWhenUserIsOwner2() {
        Booking testBooking = booking1;
        testBooking.setStatus(BookingStatus.WAITING);
        Mockito.when(bookingRepository.findById(anyInt()))
                .thenReturn(Optional.of(testBooking));
        Mockito.when(itemService.getOwnerId(anyInt()))
                .thenReturn(1);
        ResponseBookingDto responseBookingDto =  bookingService.approve(1, 1, false);
        assertEquals(responseBookingDto.getStatus(), BookingStatus.REJECTED);
    }
}