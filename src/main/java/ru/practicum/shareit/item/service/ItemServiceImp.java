package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.abstarct.AbstractService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.mapper.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.exeption.OperationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImp extends AbstractService implements ItemService {

    private final UserService userService;

    private final JpaItemRepository itemRepository;

    private final JpaBookingRepository bookingRepository;

    private final JpaCommentRepository commentRepository;

    private final ItemRequestService requestService;

    @Override
    @Transactional
    public ItemDto add(Integer ownerId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        item.setRequestId(itemDto.getRequestId() != null ?
                ItemRequestMapper.toItemRequest(requestService.getById(ownerId, itemDto.getRequestId())) : null);
        itemRepository.save(item);
        log.info("Создана вещь: {}", item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Integer ownerId, Integer itemId, ItemDto itemDto) {
        userService.getById(ownerId);
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найдена вещь по id: %d", itemId)));
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
        Item item = ItemMapper.toItem(itemDto);
        item.setId(oldItem.getId());
        item.setOwnerId(ownerId);
        itemRepository.save(item);
        log.info("Выполнен запрос на изменение вещи по id:= {} пользователя по id:= {}, входными данными : {}", itemId, ownerId,
                itemDto);
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto getById(Integer itemId, Integer ownerId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Не найдена вещь по id: %d", itemId)));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (Objects.equals(item.getOwnerId(), ownerId)) {
            updateBookings(itemDto);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(CommentMapper.toCommentDtoList(comments));
        log.info("Выполнен запрос на получение вещи по id:= {}", itemId);
        return itemDto;
    }

    @Override
    @Transactional
    public List<ItemDto> getItemByOwner(Integer ownerId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size, Sort.by("id"));
        List<ItemDto> item = itemRepository.findAllByOwnerId(ownerId, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        List<ItemDto> list = new ArrayList<>();
        item.stream().map(this::updateBookings).forEach(i -> {
            CommentMapper.toCommentDtoList(commentRepository.findAllByItemId(i.getId()));
            list.add(i);
        });
        log.info("Выполнен запрос на получение списка вещей пользователя с id:= {}", ownerId);
        return list;
    }

    @Override
    @Transactional
    public Integer getOwnerId(Integer itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Не найдена вещь по id: %d", itemId)))
                .getOwnerId();
    }

    @Override
    @Transactional
    public ItemDto getItemById(Integer itemId, Integer userId) {
        ItemDto itemDto;
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException(String
                .format("Не найдена вещь по id: %d", itemId)));
        itemDto = ItemMapper.toItemDto(item);
        if (Objects.equals(item.getOwnerId(), userId)) {
            updateBookings(itemDto);
        }
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        itemDto.setComments(CommentMapper.toCommentDtoList(comments));
        return itemDto;
    }

    @Override
    @Transactional
    public List<ItemDto> search(String text, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.searchItemsByText(text, page).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto updateBookings(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = bookingRepository.getBookingsItem(itemDto.getId());
        Booking lastBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isBefore(now))
                .min((obj1, obj2) -> obj2.getStart().compareTo(obj1.getStart())).orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(obj -> !(obj.getStatus().equals(BookingStatus.REJECTED)))
                .filter(obj -> obj.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart)).orElse(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toItemBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toItemBookingDto(nextBooking));
        }
        return itemDto;
    }

    @Override
    @Transactional
    public void deleteById(Integer itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto addComment(Integer itemId, Integer userId, CommentDto commentDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ObjectNotFoundException(String.format("Не найдена вещь по id:= %d", itemId)));
        User user = UserMapper.toUser(userService.getById(userId));
        List<Booking> bookings = bookingRepository
                .findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(itemId, userId, BookingStatus.APPROVED, LocalDateTime.now());
        log.info(bookings.toString());
        if (!bookings.isEmpty() && bookings.get(0).getStart().isBefore(LocalDateTime.now())) {
            Comment comment = CommentMapper.toComment(commentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            return CommentMapper.toCommentDto(commentRepository.save(comment));
        } else {
            throw new OperationException(String.format("Пользователь с id:= %d не брал в аренду вещь с id:= %d", userId, itemId));
        }
    }
}