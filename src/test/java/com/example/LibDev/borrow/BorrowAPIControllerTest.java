package com.example.LibDev.borrow;

import com.example.LibDev.borrow.controller.BorrowAPIController;
import com.example.LibDev.borrow.service.BorrowService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BorrowAPIControllerTest {
    private MockMvc mockMvc;

    @Mock
    private BorrowService borrowService;

    @InjectMocks
    private BorrowAPIController borrowAPIController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(borrowAPIController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("대출 생성 테스트(성공)")
    void borrow() throws Exception {
        Mockito.doNothing().when(borrowService).borrow(Mockito.any());

        mockMvc.perform(post("/api/v1/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("bookId", "1")
                )
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("대출 연장 테스트(성공)")
    void extend() throws Exception {
        Mockito.doNothing().when(borrowService).extendReturnDate(Mockito.any());

        mockMvc.perform(patch("/api/v1/extend/{borrowId}", 1L))
                .andExpect(status().isOk());
    }
}
