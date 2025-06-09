package org.trading.system.userManagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.userManagement.dto.request.CreateUserRequest;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserManagementController.class)
class UserManagementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserManagementService userManagementService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    void createUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testuser");

        User user = new User();
        user.setUserId("1");
        user.setUsername("testuser");

        ApiResponse<User> response = ApiResponse.ok(user);

        Mockito.when(userManagementService.createUser(any(CreateUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void getUserById() throws Exception {
        User user = new User();
        user.setUserId("1");
        user.setUsername("testuser");

        ApiResponse<User> response = ApiResponse.ok(user);

        Mockito.when(userManagementService.getUserById(eq("1")))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    void getAllUsers() throws Exception {
        User user = new User();
        user.setUserId("1");
        user.setUsername("testuser");

        ApiResponse<List<User>> response = ApiResponse.ok(Collections.singletonList(user));

        Mockito.when(userManagementService.getAllUsers())
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].username").value("testuser"));
    }
}