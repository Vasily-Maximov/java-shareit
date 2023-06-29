package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Null;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
public class Booking {

    @Null(groups = CreateGroup.class)
    private Integer id;
    @PastOrPresent(groups = {CreateGroup.class, UpdateGroup.class})
    private LocalDateTime start;
    @FutureOrPresent(groups = {CreateGroup.class, UpdateGroup.class})
    private LocalDateTime end;
    private User booker;
    private BookingStatus status;
}