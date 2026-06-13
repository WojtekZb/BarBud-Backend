package com.barbud.barbudbackend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void login_WithExistingUser_ReturnsLoginResponse() throws Exception {
        String loginJson = """
                {
                  "email": 
                  "password": 
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("You're logged in"))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.accessToken").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.accessExpiresIn").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.refreshToken").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.refreshExpiresIn").value(not(emptyOrNullString())));
    }

    @Test
    void login_WithIncorrectCredentials_ReturnsErrorResponse() throws Exception {
        String loginJson = """
                {
                    "email": "fakaemail@email.com",
                    "password": "IncorrectPass!"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("email or password invalid"))
                .andExpect(jsonPath("$.userId").value(0))
                .andExpect(jsonPath("$.username").value(nullValue()))
                .andExpect(jsonPath("$.accessToken").value(nullValue()))
                .andExpect(jsonPath("$.accessExpiresIn").value(nullValue()))
                .andExpect(jsonPath("$.refreshToken").value(nullValue()))
                .andExpect(jsonPath("$.refreshExpiresIn").value(nullValue()));
    }
}