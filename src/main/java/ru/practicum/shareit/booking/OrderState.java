package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderState {
    ALL("Получение бронирований"),
    CURRENT("Получение текущего бронирования"),
    PAST("Получение завершённых бронирований"),
    FUTURE("Получение будущих бронирований"),
    WAITING("Получение бронирований ожидающих подтверждения"),
    REJECTED("Получение отклонённых бронирований");

    private final String nameStatus;
}