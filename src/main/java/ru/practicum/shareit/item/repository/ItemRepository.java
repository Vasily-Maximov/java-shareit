package ru.practicum.shareit.item.repository;

import lombok.Getter;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.abstarct.repository.AbstractRepository;
import ru.practicum.shareit.item.model.Item;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Getter
public class ItemRepository extends AbstractRepository<Item> {

    private final Map<Integer, List<Item>> usersItems = new HashMap<>();
}