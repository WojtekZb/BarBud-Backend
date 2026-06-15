package com.barbud.barbudbackend.integration;

import com.jayway.jsonpath.JsonPath;
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

    private String getRefreshToken() throws Exception {
        String loginJson = """
            {
              "email": "admin@example.com",
              "password": "BorekILolek1!"
            }
            """;

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return JsonPath.read(response, "$.refreshToken");
    }

    @Test
    void login_WithExistingUser_ReturnsLoginResponse() throws Exception {
        String loginJson = """
                {
                  "email": "admin@example.com",
                  "password": "BorekILolek1!"
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
                .andExpect(jsonPath("$.userId").value(nullValue()))
                .andExpect(jsonPath("$.username").value(nullValue()))
                .andExpect(jsonPath("$.accessToken").value(nullValue()))
                .andExpect(jsonPath("$.accessExpiresIn").value(nullValue()))
                .andExpect(jsonPath("$.refreshToken").value(nullValue()))
                .andExpect(jsonPath("$.refreshExpiresIn").value(nullValue()));
    }

    @Test
    void refresh_WithCorrectRefreshToken_ReturnsLoginResponse() throws Exception {
        String token = getRefreshToken();

        String refreshJson = """
            {
                "refreshToken": "%s"
            }
            """.formatted(token);

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tokens refreshed"))
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.username").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.accessToken").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.accessExpiresIn").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.refreshToken").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.refreshExpiresIn").value(not(emptyOrNullString())));
    }

    @Test
    void refresh_WithInvalidRefreshToken_ReturnsErrorResponse() throws Exception {
        String refreshJson = """
                    {
                        "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIyMjRiNmRjYi1kZGZhLTQ4OGYtYTU4Ni1hMDBiZGE3MjE3OTciLCJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsInVzZXJJZCI6MSwidG9rZW5UeXBlIjoicmVmcmVzaCIsImlhdCI6MTc4MTU0NDkzNSwiZXhwIjoxNzg0MTM2OTM1fQ.xWFMwiGd_nuwa3vLE1hybrJuZE-NNlM6Tzr0Pr_xx34"
                    }
                    """;

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Tokens don't match"))
                .andExpect(jsonPath("$.userId").value(nullValue()))
                .andExpect(jsonPath("$.username").value(nullValue()))
                .andExpect(jsonPath("$.accessToken").value(nullValue()))
                .andExpect(jsonPath("$.accessExpiresIn").value(nullValue()))
                .andExpect(jsonPath("$.refreshToken").value(nullValue()))
                .andExpect(jsonPath("$.refreshExpiresIn").value(nullValue()));
    }
}