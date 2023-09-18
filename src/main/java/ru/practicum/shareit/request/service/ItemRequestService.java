package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import java.util.List;

public interface ItemRequestService {

    ItemRequestDto add(ItemRequestDto itemRequestDto, Integer userId);

    ItemRequestDto getById(Integer userId, Integer requestId);

    List<ItemRequestDto> getRequests(Integer userId, int from, int size);

    List<ItemRequestDto> getUserRequests(Integer userId);
}