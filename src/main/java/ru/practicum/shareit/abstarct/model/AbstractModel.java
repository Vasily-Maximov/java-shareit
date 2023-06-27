package ru.practicum.shareit.abstarct.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.check.UpdateGroup;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Setter
@Getter
public abstract class AbstractModel {

    @Null(groups = CreateGroup.class)
    @NotNull(groups = UpdateGroup.class)
    private Integer id;
}