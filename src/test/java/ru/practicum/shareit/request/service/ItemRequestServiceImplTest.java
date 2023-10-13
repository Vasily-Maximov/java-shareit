package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.repository.JpaItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.JpaItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    @Mock
    private JpaItemRequestRepository requestRepository;

    @Mock
    private JpaUserRepository userRepository;

    @Mock
    private JpaItemRepository itemRepository;

    private final User user = new User(1, "User", "user@email.com");

    private final UserDto userDto = new UserDto(1, "User", "user@email.com");

    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1)
            .requester(user)
            .description("description")
            .build();

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1)
            .description("description")
            .requester(userDto)
            .items(new ArrayList<>())
            .build();

    @Test
    void createRequestWhenUserIsExistThenReturnedExpectedRequest() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));

        Mockito.when(requestRepository.save(any()))
                .thenReturn(itemRequest);

        assertEquals(requestService.add(itemRequestDto, 1), itemRequestDto);
    }

    @Test
    void createRequestWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найден пользователь по id: %d", 1)));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> requestService.add(itemRequestDto, 100));

        assertEquals(e.getMessage(), String.format("Не найден пользователь по id: %d", 1));
    }

    @Test
    void findByIdWhenRequestIsValidThenReturnedExpectedRequest() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findById(any()))
                .thenReturn(Optional.ofNullable(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(any()))
                .thenReturn(new ArrayList<>());

        assertEquals(requestService.getById(1, 1), itemRequestDto);
    }

    @Test
    void findByIdWhenRequestIsNotExistThenReturnedNotFoundException() {
        Mockito.when(requestRepository.findById(anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найден запрос по идентификатору id = %d", 1)));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> requestService.getById(1, 1));

        assertEquals(e.getMessage(), String.format("Не найден запрос по идентификатору id = %d", 1));
    }

    @Test
    void findAllRequestsWhenParamsIsExistThenReturnedExpectedListRequests() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByRequestIdIn(any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(requestRepository.findByRequesterIdIsNot(anyInt(), any()))
                .thenReturn(List.of(itemRequest));

        assertEquals(requestService.getRequests(1, 1, 1), List.of(itemRequestDto));
    }

    @Test
    void findAllRequestsWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найден пользователь по id: %d", 1)));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> requestService.getRequests(1, 1, 1));

        assertEquals(e.getMessage(), String.format("Не найден пользователь по id: %d", 1L));
    }

    @Test
    void findAllUserRequestsWhenUserIsExistThenReturnedExpectedListRequests() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(user));
        Mockito.when(requestRepository.findByRequesterIdOrderByCreatedDesc(anyInt()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findByRequestIdIn(any()))
                .thenReturn(new ArrayList<>());

        assertEquals(requestService.getUserRequests(1), List.of(itemRequestDto));
    }

    @Test
    void findAllUserRequestsWhenUserIsNotExistThenReturnedNotFoundException() {
        Mockito.when(userRepository.findById(anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найден пользователь по id: %d", 1)));

        Exception e = assertThrows(ObjectNotFoundException.class,
                () -> requestService.getUserRequests(1));

        assertEquals(e.getMessage(), String.format("Не найден пользователь по id: %d", 1));
    }
}