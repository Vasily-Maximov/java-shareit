package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {

    private final ItemRequestService requestService;

    private static final String OWNER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto add(@RequestHeader(OWNER_ID) Integer userId,
                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Передан запрос на создание запроса от пользователя: {}", userId);
        return requestService.add(itemRequestDto, userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(OWNER_ID) Integer userId,
                                         @PathVariable Integer requestId) {
        log.info("Передан запрос на получение запроса по id : {}", requestId);
        return requestService.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getRequests(@RequestHeader(OWNER_ID) Integer userId,
                                            @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                            @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Передан запрос на получение запросов");
        return requestService.getRequests(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(OWNER_ID) Integer userId) {
        log.info("Передан запрос на получение запросов от пользователя по id: {}", userId);
        return requestService.getUserRequests(userId);
    }
}