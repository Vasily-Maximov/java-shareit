package ru.practicum.shareit.item.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    ItemDto add(Integer ownerId, ItemDto itemDto);

    ItemDto getById(Integer itemId, Integer ownerId);

    List<ItemDto> getItemByOwner(Integer ownerId, Integer from, Integer size);

    ItemDto update(Integer ownerId, Integer itemId, ItemDto itemDto);

    List<ItemDto> search(String text, Integer from, Integer size);

    Integer getOwnerId(Integer itemId);

    ItemDto getItemById(Integer itemId, Integer userId);

    void setLastAndNextBooking(ItemDto itemDto, List<Booking> bookings);

    void deleteById(Integer itemId);

    CommentDto addComment(Integer itemId, Integer userId, CommentDto commentDto);
}