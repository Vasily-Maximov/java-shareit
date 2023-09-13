package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.ShortItemBookingDto;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {

    @Null(groups = CreateGroup.class)
    @NotNull(groups = UpdateGroup.class)
    private Integer id;
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String name;
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Size(max = 300, groups = {CreateGroup.class, UpdateGroup.class})
    private String description;
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Boolean available;
    private Integer requestId;
    private ShortItemBookingDto lastBooking;
    private ShortItemBookingDto nextBooking;
    private List<CommentDto> comments;
}