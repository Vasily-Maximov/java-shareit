package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.nio.charset.StandardCharsets;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private final UserDto userDtoRequest = new UserDto(null, "User", "user@yandex.ru");

    @Test
    public void addTest01() throws Exception {
        when(userService.add(Mockito.any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    userDto.setId(1);
                    return userDto;
                });
        mvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(userDtoRequest))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.name").value(userDtoRequest.getName()));
    }

    @Test
    public void addTest02() throws Exception {
        userDtoRequest.setName("");
        when(userService.add(Mockito.any(UserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    userDto.setId(1);
                    return userDto;
                });
        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().is(400));
    }

    @Test
    public void updateTest03() throws Exception {
        when(userService.update(Mockito.any(UserDto.class), Mockito.anyInt()))
                .thenAnswer(invocationOnMock -> {
                    UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
                    userDto.setId(1);
                    userDto.setEmail("userNew@yandex.ru");
                    return userDto;
                });
        mvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1), Integer.class))
                .andExpect(jsonPath("$.email").value("userNew@yandex.ru"));
    }

    @Test
    void getByIdTest04() throws Exception {
        userDtoRequest.setId(1);
        Mockito.when(userService.getById(1))
                .thenReturn(userDtoRequest);
        mvc.perform(get("/users/{userId}", 1)
                ).andExpect(status().is(200))
                .andExpect(jsonPath("$.id", is(1), Integer.class));
    }

    @Test
    void getByIdTest05() throws Exception {
        String errorMessage = String.format("Не найден пользователь по id: %d", 1);
        Mockito.when(userService.getById(1))
                .thenThrow(new ObjectNotFoundException(errorMessage));
        mvc.perform(get("/users/{userId}", 1)
        ).andExpect(status().is(404));
    }

    @Test
    void getAllTest08() throws Exception {
        UserDto userDto1 = new UserDto(1, "User", "user@yandex.ru");
        UserDto userDto2 = new UserDto(2, "User2", "user2@yandex.ru");
        Mockito.when(userService.getAll())
                .thenReturn(List.of(userDto1, userDto2));

        String result = mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(result, objectMapper.writeValueAsString(List.of(userDto1, userDto2)));
    }

    @Test
    void deleteTest07() throws Exception {
        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());
        verify(userService).delete(1);
    }
}