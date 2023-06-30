package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {

    private Integer id;
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    private String name;
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Size(max = 300, groups = {CreateGroup.class, UpdateGroup.class})
    private String description;
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private Boolean available;
    private Integer requestId;
}