package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstarct.repository.AbstractRepository;
import ru.practicum.shareit.abstarct.service.AbstractService;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService extends AbstractService<Item> {

    private final UserService userService;

    @Autowired
    public ItemService(AbstractRepository<Item> repository, UserService userService) {
        super(repository);
        this.userService = userService;
    }

    public Item add(Integer ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        super.add(item);
        log.info("Создана вещь: {}", item);
        return item;
    }

    public Item update(Integer ownerId, Integer itemId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item oldItem = getById(itemId);
        if (!oldItem.getOwnerId().equals(ownerId)) {
            String message = String.format("Найденная вещь по id: %d не принадлежит владельцу по id: %d", itemId, ownerId);
            log.info(message);
            throw new ObjectNotFoundException(message);
        }
        String inputName = itemDto.getName();
        super.checkField(inputName, "name");
        if (inputName == null) {
            itemDto.setName(oldItem.getName());
        }
        String description = itemDto.getDescription();
        super.checkField(description, "description");
        if (description == null) {
            itemDto.setDescription(oldItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(oldItem.getAvailable());
        }
        Item item = ItemMapper.toItem(itemDto, ownerId);
        item.setId(oldItem.getId());
        super.update(item);
        log.info("Выполнен запрос на изменение вещи по id:= {} пользователя по id:= {}, входными данными : {}", itemId, ownerId,
                itemDto);
        return item;
    }

    public Item getById(Integer itemId) {
        Item item = super.getById(itemId);
        if (item == null) {
            String message = String.format("Не найден вещь по id: %d", itemId);
            log.info(message);
            throw new ObjectNotFoundException(message);
        }
        return item;
    }

    public List<Item> getItemByOwner(Integer ownerId) {
        return super.getAll().stream().filter(item -> item.getOwnerId().equals(ownerId)).collect(Collectors.toList());
    }

    public List<Item> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        text = text.toLowerCase();
        String finalText = text;
        return super.getAll().stream().filter(Item::getAvailable).filter(item -> item.getName().toLowerCase().contains(finalText) ||
                        item.getDescription().toLowerCase().contains(finalText)).collect(Collectors.toList());
    }
}