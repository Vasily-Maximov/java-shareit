package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @Validated(CreateGroup.class) @RequestBody ItemDto
            itemDto) {
        log.info("Передан запрос на создание вещи: {}", itemDto);
        return ItemMapper.toItemDto(itemService.add(ownerId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable Integer itemId,
                       @RequestBody ItemDto itemDto) {
        log.info("Передан запрос на изменение вещи по id:= {} пользователя по id:= {}, входные данные вещи : {}", itemId, ownerId,
                itemDto);
        return ItemMapper.toItemDto(itemService.update(ownerId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable Integer itemId) {
        log.info("Передан запрос на поиск вещи по id:= {}", itemId);
        return ItemMapper.toItemDto(itemService.getById(itemId));
    }

    @GetMapping()
    public List<ItemDto> getItemByOwner(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Передан запрос на поиск вещей владельца по id:= {}", ownerId);
        return itemService.getItemByOwner(ownerId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        log.info("Передан запрос на поиск вещей по подстроке text:= {}", text);
        return itemService.search(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}