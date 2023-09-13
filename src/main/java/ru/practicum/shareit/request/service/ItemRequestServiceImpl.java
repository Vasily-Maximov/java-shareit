package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {

    private final JpaItemRequestRepository requestRepository;

    private final JpaUserRepository userRepository;

    private final JpaItemRepository itemRepository;

    @Override
    public ItemRequestDto add(ItemRequestDto itemRequestDto, Integer userId) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(String
                        .format("Не найден пользователь по id: %d", userId))))
                .created(LocalDateTime.now())
                .build();
        log.info("Выполнен запрос на создание запроса от пользователя id:= {}", userId);
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getById(Integer userId, Integer requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Не найден запрос по идентификатору id = %d",
                        requestId)));
        itemRequest.setItems(itemRepository.findAllByRequestId(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setRequester(UserMapper.toUserDto(userRepository.findById(userId)
                .orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найден пользователь по id: %d", userId)))));
        log.info("Выполнен запрос на получение вещи по запросу с id : {}", requestId);
        return itemRequestDto;
    }

    @Override
    public List<ItemRequestDto> getRequests(Integer userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найден пользователь по id: %d", userId)));
        Pageable page = PageRequest.of(from / size, size, Sort.by("created"));
        List<ItemRequest> itemRequests = requestRepository.findByRequesterIdIsNot(userId, page);
        List<ItemRequestDto> ItemRequestDtoList = itemRequests.stream().map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        Map<Integer, List<Item>> items = itemRepository.findByRequestIdIn(itemRequests, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Item -> Item.getRequestId().getId(), toList()));
        for (ItemRequestDto itemRequestDto: ItemRequestDtoList) {
            int key = itemRequestDto.getId();
            if (items.containsKey(key)) {
                itemRequestDto.setItems(items.get(key).stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
            }
        }
        log.info("Передан запрос на получение запросов");
        return ItemRequestDtoList;
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Integer userId) {
        userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найден пользователь по id: %d", userId)));
        List<ItemRequest> itemRequests = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        List<ItemRequestDto> ItemRequestDtoList = itemRequests.stream().map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
        Map<Integer, List<Item>> items = itemRepository.findByRequestIdIn(itemRequests, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Item -> Item.getRequestId().getId(), toList()));
        for (ItemRequestDto itemRequestDto: ItemRequestDtoList) {
            int key = itemRequestDto.getId();
            if (items.containsKey(key)) {
                itemRequestDto.setItems(items.get(key).stream().map(ItemMapper::toItemDto).collect(Collectors.toList()));
            }
        }
        log.info("Выполнен запрос на получение запросов от пользователя по id: {}", userId);
        return ItemRequestDtoList;
    }
}