package ru.practicum.shareit.booking;

public enum BookingStatus {

    WAITING("Новое бронирование, ожидает одобрения"),
    APPROVED("Бронирование подтверждено владельцем"),
    REJECTED("Бронирование отклонено владельцем"),
    CANCELED("Бронирование отменено создателем");

    private final String nameStatus;

    BookingStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }

    public String getNameStatus() {
        return nameStatus;
    }
}