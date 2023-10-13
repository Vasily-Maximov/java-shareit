package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = ItemDto.builder()
            .id(1)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1)
            .build();

    private final ItemDto itemDtoRequest = ItemDto.builder()
            .id(null)
            .name("Item")
            .description("Description")
            .available(true)
            .requestId(1)
            .build();

    private final List<ItemDto> itemsDtoList = List.of(
            new ItemDto(1, "Name", "Description", true, null,
                    null, null, null),
            new ItemDto(2, "Name2", "Description2", true, null,
                    null, null, null));
    private final CommentDto commentDto = CommentDto.builder().id(1).text("Text").authorName("Name").build();


    @Test
    void createItemWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        when(itemService.add(anyInt(), any(ItemDto.class)))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name").value(itemDtoRequest.getName()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).add(1, itemDtoRequest);
    }

    @Test
    void createItemWhenItemWithoutNameThenReturnedStatusIsBadRequest() throws Exception {
        ItemDto badItemDto = ItemDto.builder()
                .id(1)
                .name("")
                .description("Description")
                .available(true)
                .requestId(1)
                .build();

        when(itemService.add(anyInt(), any()))
                .thenReturn(badItemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItemDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).add(1, badItemDto);
    }

    @Test
    void createItemWhenItemWithoutAvailbleThenReturnedStatusIsBadRequest() throws Exception {
        ItemDto badItemDto = ItemDto.builder()
                .id(1)
                .name("Item")
                .description("description")
                .requestId(1)
                .build();
        when(itemService.add(anyInt(), any()))
                .thenReturn(badItemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(badItemDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).add(1, badItemDto);
    }

    @Test
    void findItemByIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        when(itemService.getById(anyInt(), anyInt()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).getById(1, 1);
    }

    @Test
    void findItemByIdWhenItemIdIsNotFoundThenReturnedStatusIsNotFound() throws Exception {
        when(itemService.getById(anyInt(), anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format("Не найдена вещь по id: %d", 100)));

        mvc.perform(get("/items/{itemId}", 100)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDtoRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllUsersItemsWhenUserIdIsExistThenReturnedStatusIsOk() throws Exception {
        when(itemService.getItemByOwner(anyInt(), anyInt(), anyInt()))
                .thenReturn(itemsDtoList);

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemsDtoList));
    }

    @Test
    void getItemByOwner() throws Exception {
        List<ItemDto> itemDtoList = new ArrayList<>();
        when(itemService.getItemByOwner(anyInt(), anyInt(), anyInt()))
                .thenReturn(itemDtoList);

        String result = mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 100))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, objectMapper.writeValueAsString(itemDtoList));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto updateItemDto = ItemDto.builder()
                .id(1)
                .name("updateItem")
                .description("Description")
                .available(true)
                .requestId(1)
                .build();

        when(itemService.update(anyInt(), anyInt(), any(ItemDto.class)))
                .thenReturn(updateItemDto);

        mvc.perform(patch("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(updateItemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).update(1, 1, updateItemDto);
    }

    @Test
    void searchItemByParams() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search?text=descr", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Item")))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Description")));
    }

    @Test
    void addCommentWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Integer.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())));
    }

    @Test
    void addCommentWhenTextIsEmptyThenReturnedStatusIsBadRequest() throws Exception {
        CommentDto badCommentDto = CommentDto.builder().id(1).text("").authorName("AuthorName").build();
        when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(badCommentDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteItemTest() throws Exception {
        mvc.perform(delete("/items/{itemId}", 1))
                .andExpect(status().isOk());

        verify(itemService).deleteById(1);
    }
}