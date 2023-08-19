package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import java.util.List;

public interface JpaItemRepository extends JpaRepository<Item, Integer> {

    List<Item> findAllByOwnerId(Integer userId, Pageable pageable);

    @Query("SELECT i FROM Item i "
            + "WHERE upper(i.name) like upper(concat('%', ?1, '%'))"
            + "OR upper(i.description) like upper(concat('%', ?1, '%'))"
            + "AND i.available = true")
    List<Item> searchItemsByText(String text, Pageable page);

   List<Item> findAllByRequestId(ItemRequest itemRequest);
}