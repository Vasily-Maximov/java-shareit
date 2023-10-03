package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertEquals;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ItemRequestService requestService;

    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1).description("testDescription").build();

    @Test
    void createRequestWhenRequestDtoValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(requestService.add(any(), anyInt()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).add(itemRequestDto, 1);
    }

    @Test
    void createRequestWhenRequestDtoNotValidThenReturnedStatusIsBadRequest() throws Exception {
        ItemRequestDto badItemRequestDto = ItemRequestDto.builder().description("").build();

        Mockito.when(requestService.add(badItemRequestDto, 1))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badItemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(requestService, never()).add(badItemRequestDto, 1);
    }

    @Test
    void findByIdWhenRequestIsExistThanReturnedStatusIsOk() throws Exception {
        Mockito.when(requestService.getById(anyInt(), anyInt()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findByIdWhenRequestIsNotExistThanReturnedStatusIsNotFound() throws Exception {
        Mockito.when(requestService.getById(anyInt(), anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найден запрос по идентификатору id = %d", 1)));

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllUserRequestsWhenUserIsExistThenReturnedStatusIsOk() throws Exception {
        Mockito.when(requestService.getUserRequests(anyInt()))
                .thenReturn(Collections.emptyList());

        String result = mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(Collections.emptyList()));
    }

    @Test
    void findAllUserRequestsWhenUserIsNotExistThenReturnedStatusIsNotFound() throws Exception {
        Mockito.when(requestService.getUserRequests(anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найден пользователь по id: %d", 100)));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService, never()).getUserRequests(1);
    }

    @Test
    void findAllRequestsTest() throws Exception {
        Mockito.when(requestService.getRequests(anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}