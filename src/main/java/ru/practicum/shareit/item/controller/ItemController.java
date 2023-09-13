package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.shareit.check.CreateGroup;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.service.ItemServiceImp;
import ru.practicum.shareit.item.dto.ItemDto;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemController {

    private final ItemServiceImp itemService;

    private static final String OWNER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestHeader(OWNER_ID) Integer ownerId, @Validated(CreateGroup.class) @RequestBody ItemDto
            itemDto) {
        log.info("Передан запрос на создание вещи: {}", itemDto);
        return itemService.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(OWNER_ID) Integer ownerId, @PathVariable Integer itemId,
                       @RequestBody ItemDto itemDto) {
        log.info("Передан запрос на изменение вещи по id:= {} пользователя по id:= {}, входные данные вещи : {}", itemId, ownerId,
                itemDto);
        return itemService.update(ownerId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getById(@RequestHeader(OWNER_ID) Integer ownerId, @PathVariable Integer itemId) {
        log.info("Передан запрос на поиск вещи по id:= {}", itemId);
        return itemService.getById(itemId, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemByOwner(@RequestHeader(OWNER_ID) Integer ownerId,
                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Передан запрос на поиск вещей владельца по id:= {}", ownerId);
        return itemService.getItemByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Передан запрос на поиск вещей по подстроке text:= {}", text);
        return itemService.search(text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Integer itemId) {
        log.info("Передан запрос на удаление вещи по id:= {}", itemId);
        itemService.deleteById(itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(OWNER_ID) Integer userId,
                                    @PathVariable Integer itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        log.info("Передан запрос на добавление комментария к вещи с id:= {}", itemId);
        return itemService.addComment(itemId, userId, commentDto);
    }
}