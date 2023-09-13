package ru.practicum.shareit.booking;

public enum OrderState {
    ALL("Получение бронирований"),
    CURRENT("Получение текущего бронирования"),
    PAST("Получение завершённых бронирований"),
    FUTURE("Получение будущих бронирований"),
    WAITING("Получение бронирований ожидающих подтверждения"),
    REJECTED("Получение отклонённых бронирований");

    private final String nameStatus;

    OrderState(String nameStatus) {
        this.nameStatus = nameStatus;
    }

    public String getNameStatus() {
        return nameStatus;
    }
}