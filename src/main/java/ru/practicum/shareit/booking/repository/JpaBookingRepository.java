package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import java.time.LocalDateTime;
import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Integer> {

    @Modifying
    @Query("UPDATE Booking b "
            + "SET b.status = :status  "
            + "WHERE b.id = :bookingId")
    void save(BookingStatus status, Integer bookingId);

    List<Booking> findByBookerIdOrderByStartDesc(Integer id, Pageable pageable);

    List<Booking> findByBookerIdAndStatusIsOrderByStartDesc(Integer id, BookingStatus status, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Integer id, LocalDateTime time, Pageable pageable);

    List<Booking> findByBookerIdAndStartIsAfterAndStatusIsOrderByStartDesc(Integer bookerId,
                                                                           LocalDateTime start,
                                                                           BookingStatus status,
                                                                           Pageable pageable);

    List<Booking> findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Integer id,
                                                                              LocalDateTime end,
                                                                              LocalDateTime start,
                                                                              Pageable pageable);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Integer id, LocalDateTime time, Pageable pageable);

    List<Booking> findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(Integer itemId,
                                                                   Integer bookerId,
                                                                   BookingStatus status,
                                                                   LocalDateTime time);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "ORDER BY b.start DESC")
    List<Booking> getByItemOwnerId(Integer ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND :time between b.start AND b.end "
            + "ORDER BY b.start DESC")
    List<Booking> getCurrentBookingsOwner(Integer ownerId, LocalDateTime time, Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.end < :time "
            + "ORDER BY b.start DESC")
    List<Booking> getPastBookingsOwner(Integer ownerId, LocalDateTime time, Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.start > :time "
            + "ORDER BY b.start DESC")
    List<Booking> getFutureBookingsOwner(Integer ownerId, LocalDateTime time, Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.start > :time AND b.status = :status "
            + "ORDER BY b.start DESC")
    List<Booking> getWaitingBookingsOwner(Integer ownerId, LocalDateTime time, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId "
            + "AND b.status = :status "
            + "ORDER BY b.start DESC")
    List<Booking> getRejectedBookingsOwner(Integer ownerId, BookingStatus status, Pageable pageable);

    @Query("SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.id = :itemId "
            + "ORDER BY b.start DESC")
    List<Booking> getBookingsItem(Integer itemId);
}