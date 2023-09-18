package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class BookingDto {

    private static final String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss";

    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Integer itemId;
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    @FutureOrPresent(groups = {CreateGroup.class, UpdateGroup.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimeFormat)
    private LocalDateTime start;
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    @FutureOrPresent(groups = {CreateGroup.class, UpdateGroup.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = dateTimeFormat)
    private LocalDateTime end;
}