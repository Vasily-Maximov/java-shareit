package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Integer itemId;
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    @FutureOrPresent(groups = {CreateGroup.class, UpdateGroup.class})
    private LocalDateTime start;
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    @FutureOrPresent(groups = {CreateGroup.class, UpdateGroup.class})
    private LocalDateTime end;
}