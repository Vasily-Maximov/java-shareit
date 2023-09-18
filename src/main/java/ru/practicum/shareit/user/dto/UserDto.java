package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {

    @Null(groups = CreateGroup.class)
    private Integer id;
    @NotBlank(groups = CreateGroup.class)
    private String name;
    @Email(groups = {CreateGroup.class, UpdateGroup.class})
    @NotNull(groups = CreateGroup.class)
    private String email;
}