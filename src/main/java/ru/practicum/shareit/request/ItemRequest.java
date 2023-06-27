package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import ru.practicum.shareit.user.model.User;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@AllArgsConstructor
public class ItemRequest {

    @Null(groups = CreateGroup.class)
    @NotNull(groups = CreateGroup.class)
    private Integer id;
    @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
    @Size(max = 300, groups = {CreateGroup.class, UpdateGroup.class})
    private String description;
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private User requestor;
    @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
    private LocalDateTime created;
}