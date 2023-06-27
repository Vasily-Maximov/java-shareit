package ru.practicum.shareit.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.exeption.ErrorResponse;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public Item add(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @Validated(CreateGroup.class) @RequestBody ItemDto itemDto) {
        log.info("Передан запрос на создание вещи: {}", itemDto);
        return itemService.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable Integer itemId,
                       @RequestBody ItemDto itemDto) {
        log.info("Передан запрос на изменение вещи по id:= {} пользователя по id:= {}, входные данные вещи : {}", itemId, ownerId,
                itemDto);
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public Item getById(@RequestHeader("X-Sharer-User-Id") Integer ownerId, @PathVariable Integer itemId) {
        log.info("Передан запрос на поиск вещи по id:= {}", itemId);
        return itemService.getById(itemId);
    }

    @GetMapping()
    public List<Item> getItemByOwner(@RequestHeader("X-Sharer-User-Id") Integer ownerId) {
        log.info("Передан запрос на поиск вещей владельца по id:= {}", ownerId);
        return itemService.getItemByOwner(ownerId);
    }

    @GetMapping("/search")
    public List<Item> search(@RequestParam String text) {
        log.info("Передан запрос на поиск вещей по подстроке text:= {}", text);
        return itemService.search(text);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectParameter(final NumberFormatException e) {
        String message = String.format("Некорректно передан параметр: %s", e.getMessage());
        log.info(message);
        return new ErrorResponse(message);
    }
}