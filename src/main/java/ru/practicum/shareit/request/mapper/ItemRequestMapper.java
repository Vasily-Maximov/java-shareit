package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import java.util.ArrayList;
import java.util.stream.Collectors;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@UtilityClass
public class ItemRequestMapper {

    public ItemRequestDto toItemRequestDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .requester(UserMapper.toUserDto(request.getRequester()))
                .items(request.getItems() != null ? request.getItems()
                        .stream()
                        .map(ItemMapper::toItemDto)
                        .collect(Collectors.toList())
                        : new ArrayList<>())
                .build();
    }

    public ItemRequest toItemRequest(ItemRequestDto requestDto) {
        return ItemRequest.builder()
                .id(requestDto.getId())
                .description(requestDto.getDescription())
                .requester(UserMapper.toUser(requestDto.getRequester()))
                .created(requestDto.getCreated())
                .build();
    }
}