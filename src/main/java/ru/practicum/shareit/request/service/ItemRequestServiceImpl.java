package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final JpaItemRequestRepository requestRepository;
    private final UserService userService;
    private final JpaItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto add(ItemRequestDto itemRequestDto, Integer userId) {
        ItemRequest itemRequest = ItemRequest.builder()
                .description(itemRequestDto.getDescription())
                .requester(UserMapper.toUser(userService.getById(userId)))
                .created(LocalDateTime.now())
                .build();
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    @Transactional
    public ItemRequestDto getById(Integer userId, Integer requestId) {
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Не найден запрос по идентификатору id = %d", requestId)));
        itemRequest.setItems(itemRepository.findAllByRequestId(itemRequest));
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setRequester(userService.getById(userId));
        return itemRequestDto;
    }

    @Override
    @Transactional
    public List<ItemRequestDto> getRequests(Integer userId, int from, int size) {
        UserMapper.toUser(userService.getById(userId));
        Pageable page = PageRequest.of(from / size, size, Sort.by("created"));
        List<ItemRequestDto> list = new ArrayList<>();
        List<ItemRequest> findAllByRequesterIdIsNot = requestRepository.findByRequesterIdIsNot(userId, page);
        findAllByRequesterIdIsNot.forEach(itemRequest -> {
            itemRequest.setItems(itemRepository.findAllByRequestId(itemRequest));
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
            list.add(itemRequestDto);
        });
        return list;

    }

    @Override
    @Transactional
    public List<ItemRequestDto> getUserRequests(Integer userId) {
        userService.getById(userId);
        List<ItemRequestDto> list = new ArrayList<>();
        List<ItemRequest> findAllByRequesterIdOrderByCreatedDesc = requestRepository.findByRequesterIdOrderByCreatedDesc(userId);
        findAllByRequesterIdOrderByCreatedDesc.forEach(i -> {
            i.setItems(itemRepository.findAllByRequestId(i));
            ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(i);
            list.add(itemRequestDto);
        });
        return list;
    }
}