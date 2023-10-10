package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.JpaBookingRepository;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.repository.JpaCommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

@ExtendWith(MockitoExtension.class)
class ItemServiceImpTest {

    private ItemService itemService;

    @Mock
    private JpaItemRepository itemRepository;

    @Mock
    private JpaBookingRepository bookingRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaCommentRepository commentRepository;

    @Mock
    private ItemRequestService requestService;

    private final User user = new User(1, "User", "user@email.com");

    private final UserDto userDto = new UserDto(1, "User", "user@email.com");

    private final ItemRequest requestId = ItemRequest.builder()
            .id(1)
            .description("description")
            .requester(user)
            .items(new ArrayList<>())
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1)
            .description("description")
            .requester(userDto)
            .items(new ArrayList<>())
            .build();
    private final Item item = Item.builder()
            .id(1)
            .name("ItemName")
            .description("description")
            .available(true)
            .requestId(requestId)
            .ownerId(1)
            .build();
    private final ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("ItemName")
            .description("description")
            .available(true)
            .requestId(1)
            .build();
    private final List<Booking> bookingList = List.of(Booking.builder()
                    .id(1).item(item).booker(user)
                    .start(LocalDateTime.now().minusHours(2))
                    .end(LocalDateTime.now().minusHours(1))
                    .status(BookingStatus.WAITING).build(),
            Booking.builder()
                    .id(2).item(item).booker(user)
                    .start(LocalDateTime.now().plusHours(1))
                    .end(LocalDateTime.now().plusHours(2))
                    .status(BookingStatus.WAITING).build());

    private final Comment comment = Comment.builder().id(1).text("Text").item(item).author(user).build();

    private final CommentDto commentDto = CommentDto.builder().id(1).text("Text").item(itemDto).authorName("User").build();

    @BeforeEach
    public void beforeEach() {
        itemService = new ItemServiceImp(userRepository, itemRepository, bookingRepository, commentRepository, requestService);
    }

    @Test
    void createItemWhenAllIsValidThenReturnedExpectedItem() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        Mockito.when(requestService.getById(anyInt(), anyInt()))
                .thenReturn(itemRequestDto);

        Mockito.when(itemRepository.save(any()))
                .thenReturn(item);

        assertEquals(itemService.add(1, itemDto), itemDto);
    }

    @Test
    void createItemWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найден пользователь по id: %d", 100)));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.add(100, itemDto));

        assertEquals(e.getMessage(), String.format("Не найден пользователь по id: %d", 100));
    }

    @Test
    void findByIdWhenParamsIsValidThenReturnedExpectedItem() {
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        Mockito.when(commentRepository.findAllByItemId(anyInt()))
                .thenReturn(List.of(comment));

        Mockito.when(bookingRepository.findByItemIn(any()))
                .thenReturn(bookingList);

        ItemDto requestedItemDto = itemService.getById(1, 1);

        assertEquals(requestedItemDto.getName(), item.getName());
        assertEquals(requestedItemDto.getDescription(), item.getDescription());
        assertEquals(requestedItemDto.getAvailable(), item.getAvailable());
        assertEquals(requestedItemDto.getLastBooking().getId(), 1);
        assertEquals(requestedItemDto.getLastBooking().getBookerId(), 1);
        assertEquals(requestedItemDto.getNextBooking().getId(), 2);
        assertEquals(requestedItemDto.getNextBooking().getBookerId(), 1);
    }

    @Test
    void findByIdWhenItemNotFoundThenReturnedNotFoundException() {
        Mockito.when(itemRepository.findById(anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найдена вещь по id: %d", 100)));

        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemService
                .getById(100, 1));

        assertEquals(e.getMessage(), String.format("Не найдена вещь по id: %d", 100));
    }

    @Test
    void findAllUserItemsWhenAllParamsIsValidThenReturnedListItems() {
        Mockito.when(itemRepository.findAllByOwnerId(anyInt(), any()))
                .thenReturn(List.of(item));

        Mockito.when(bookingRepository.findByItemIn(any()))
                .thenReturn(bookingList);

        List<ItemDto> userItemsList = itemService.getItemByOwner(1, 1, 1);

        assertEquals(userItemsList.get(0).getLastBooking().getId(), 1);
        assertEquals(userItemsList.get(0).getLastBooking().getBookerId(), 1);
        assertEquals(userItemsList.get(0).getNextBooking().getId(), 2);
        assertEquals(userItemsList.get(0).getNextBooking().getBookerId(), 1);
    }

    @Test
    void updateItemWhenAllParamsIsValidThenReturnedUpdatedItem() {
        ItemDto itemDtoUpdate = ItemDto.builder()
                .id(1)
                .name("ItemUpdate")
                .description("DescriptionUpdate")
                .available(true)
                .build();

        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));

        assertEquals(itemService.update(1, 1, itemDtoUpdate), itemDtoUpdate);
    }

    @Test
    void updateItemWhenUserIsNotOwnerIdThenReturnedOperationAccessException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(2, 1, itemDto));

        assertEquals(e.getMessage(), String.format("Найденная вещь по id: %d не принадлежит владельцу по id: %d", 1, 2));
    }

    @Test
    void updateItemWhenItemIsNotFoundThenReturnedNotFoundException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.empty());

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> itemService.update(1, 100, itemDto));

        assertEquals(e.getMessage(), String.format("Не найдена вещь по id: %d", 100));
    }

    @Test
    void searchTestWllParamsIsValidThenReturnedPageableListOfItems() {
        assertThat(itemService.search("", 0, 10), hasSize(0));
        assertThat(itemService.search(null, 0, 10), hasSize(0));

        Mockito.when(itemRepository.searchItemsByText(anyString(), any()))
                .thenReturn(List.of(item));

        assertEquals(itemService.search("item", 0, 10), List.of(itemDto));
    }

    @Test
    void addCommentTest() {
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.findByItemIdAndBookerIdAndStatusIsAndEndIsBefore(anyInt(), anyInt(), any(), any()))
                .thenReturn(bookingList);
        Mockito.when(commentRepository.save(any()))
                .thenReturn(comment);
        Mockito.when(commentRepository.save(any()))
                .thenAnswer(i -> i.getArgument(0));
        CommentDto testComment = itemService.addComment(1, 1, commentDto);

        assertEquals(testComment.getId(), commentDto.getId());
        assertEquals(testComment.getItem(), commentDto.getItem());
        assertEquals(testComment.getText(), commentDto.getText());
        assertEquals(testComment.getAuthorName(), commentDto.getAuthorName());
    }

    @Test
    void deleteItem() {
        itemService.deleteById(1);

        Mockito.verify(itemRepository).deleteById(1);
    }

    @Test
    public void getOwnerIdTest() {
        Mockito.when(itemRepository.findById(anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найдена вещь по id: %d", 100)));

        Exception e = assertThrows(ObjectNotFoundException.class, () -> itemService.getOwnerId(100));

        assertEquals(e.getMessage(), String.format("Не найдена вещь по id: %d", 100));
    }

    @Test
    void getItemByIdTest() {
        Mockito.when(itemRepository.findById(anyInt()))
                .thenReturn(Optional.of(item));

        Mockito.when(bookingRepository.findByItemIn(any()))
                .thenReturn(bookingList);

        Mockito.when(commentRepository.findAllByItemId(anyInt()))
                .thenReturn(List.of(comment));

        ItemDto responseItemDto = itemService.getItemById(1, 1);

        assertEquals("Text", responseItemDto.getComments().get(0).getText());
        assertEquals(responseItemDto.getName(), item.getName());
        assertEquals(responseItemDto.getDescription(), item.getDescription());
        assertEquals(responseItemDto.getAvailable(), item.getAvailable());
        assertEquals(responseItemDto.getLastBooking().getId(), 1);
        assertEquals(responseItemDto.getLastBooking().getBookerId(), 1);
        assertEquals(responseItemDto.getNextBooking().getId(), 2);
        assertEquals(responseItemDto.getNextBooking().getBookerId(), 1);
    }
}