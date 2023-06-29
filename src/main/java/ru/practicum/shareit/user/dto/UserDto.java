package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserDto {

    private Integer id;
    @NotBlank(groups = CreateGroup.class)
    private String name;
    @Email(groups = {CreateGroup.class, UpdateGroup.class})
    @NotNull(groups = CreateGroup.class)
    private String email;
}