package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exeption.ObjectNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final ItemDto itemDto = ItemDto.builder()
            .name("testItem")
            .description("testDescription")
            .available(true)
            .build();
    private final BookingDto bookingDto = BookingDto.builder()
            .start(LocalDateTime.of(2023, 11, 1, 15, 0, 0))
            .end(LocalDateTime.of(2023, 11, 10, 15, 0, 0))
            .itemId(1).build();
    private final ResponseBookingDto responseBookingDto = ResponseBookingDto.builder()
            .start(LocalDateTime.of(2023, 11, 1, 15, 0, 0))
            .end(LocalDateTime.of(2023, 11, 10, 15, 0, 0))
            .item(itemDto)
            .build();

    @Test
    void createBookingWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.add(any(), anyInt()))
                .thenReturn(responseBookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).add(bookingDto, 1);
    }

    @Test
    void createBookingWhenStartIsNotValidThenReturnedStatusIsBadRequest() throws Exception {
        BookingDto badBookingDto = BookingDto.builder()
                .start(LocalDateTime.of(1000, 5, 10, 13, 0, 0))
                .end(LocalDateTime.of(2023, 5, 20, 13, 0, 0))
                .itemId(1)
                .build();

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(badBookingDto)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, never()).add(badBookingDto, 1);
    }

    @Test
    void createBookingWhenUserIsNotOwnerThenReturnedStatusIsNotFound() throws Exception {
        String errorMessage = String.format("Не найден пользователь по id: %d", 1);
        Mockito.when(bookingService.add(any(), anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format(errorMessage)));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findBookingByIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.getBookingById(anyInt(), anyInt()))
                .thenReturn(responseBookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookingById(1, 1);
    }

    @Test
    void findBookingByIdWhenBookingIdNotFoundThenReturnedStatusIsNotFound() throws Exception {
        String errorMessage = String.format("Не найдено бронирование по id = %s", 10);
        Mockito.when(bookingService.getBookingById(anyInt(), anyInt()))
                .thenThrow(new ObjectNotFoundException(String.format(errorMessage)));

        mvc.perform(get("/bookings/{bookingId}", 100)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bookingDto)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void findAllByUserIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.getAllBookingsByUser(any(), anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBookingDto));

        String result = mvc.perform(get("/bookings/?state=ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(responseBookingDto)));
    }

    @Test
    void findAllByOwnerIdWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.getAllBookingsByOwner(any(), anyInt(), anyInt(), anyInt()))
                .thenReturn(List.of(responseBookingDto));

        String result = mvc.perform(get("/bookings/owner?state=ALL")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(result, mapper.writeValueAsString(List.of(responseBookingDto)));
    }

    @Test
    void approveBookingWhenAllParamsIsValidThenReturnedStatusIsOk() throws Exception {
        Mockito.when(bookingService.approve(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(responseBookingDto);

        mvc.perform(get("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}