package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.abstarct.repository.AbstractRepository;
import ru.practicum.shareit.item.model.Item;

@Repository
public class ItemRepository extends AbstractRepository<Item> {
}
