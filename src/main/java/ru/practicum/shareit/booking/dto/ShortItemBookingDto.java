package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ShortItemBookingDto {

    private Integer id;
    private Integer bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
}