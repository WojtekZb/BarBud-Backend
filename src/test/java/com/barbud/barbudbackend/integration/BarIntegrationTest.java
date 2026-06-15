package com.barbud.barbudbackend.integration;

import com.barbud.barbudbackend.responses.BarResponse;
import com.barbud.barbudbackend.responses.LoginResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.jayway.jsonpath.JsonPath;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private LoginResponse getCredentials() throws Exception {
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

        return new LoginResponse(
                JsonPath.read(response, "$.message"),
                ((Number) JsonPath.read(response, "$.userId")).longValue(),
                JsonPath.read(response, "$.username"),
                JsonPath.read(response, "$.accessToken"),
                LocalDateTime.parse(JsonPath.read(response, "$.accessExpiresIn")),
                JsonPath.read(response, "$.refreshToken"),
                LocalDateTime.parse(JsonPath.read(response, "$.refreshExpiresIn"))
        );
    }

    private Long getBarId(Long userId) throws Exception {
        LoginResponse response = getCredentials();

        String myBarsJson = """
            {
                "userId": %s
            }
            """.formatted(userId);

        String result = mockMvc.perform(post("/bar/my-bars")
                        .header("Authorization", "Bearer " + response.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(myBarsJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Number barId = JsonPath.read(result, "$[0].id");
        return barId.longValue();
    }

    @Test
    void ingredients_ReturnsIngredientList() throws Exception {
        LoginResponse response = getCredentials();

        mockMvc.perform(get("/bar/ingredients")
                        .header("Authorization", "Bearer " + response.getAccessToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$[0].category").value(not(emptyOrNullString())));
    }

    @Test
    void myBars_WithExistingUser_ReturnsBars() throws Exception {
        LoginResponse response = getCredentials();

        String myBarsJson = """
                {
                    "userId": "%s"
                }
                """.formatted(response.getUserId());

        mockMvc.perform(post("/bar/my-bars")
                        .header("Authorization", "Bearer " + response.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(myBarsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$[0].amountIngredients").isNumber());
    }

    @Test
    void myBars_WithNonExistingUser_ReturnsEmptyList() throws Exception {
        LoginResponse response = getCredentials();

        String myBarsJson = """
                {
                    "userId": 9999
                }
                """;

        mockMvc.perform(post("/bar/my-bars")
                        .header("Authorization", "Bearer " + response.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(myBarsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void details_WithExistingBar_ReturnsBarDetails() throws Exception {
        LoginResponse response = getCredentials();
        Long barId = getBarId(response.getUserId());

        String detailsJson = """
                {
                    "userId": "%s",
                    "barId": "%s"
                }
                """.formatted(response.getUserId(), barId);

        mockMvc.perform(post("/bar/details")
                        .header("Authorization", "Bearer " + response.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(detailsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Bar details."))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.ingredients").isArray())
                .andExpect(jsonPath("$.ingredients", not(empty())))
                .andExpect(jsonPath("$.ingredients[0].id").isNumber())
                .andExpect(jsonPath("$.ingredients[0].name").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$.ingredients[0].category").value(not(emptyOrNullString())));
    }

    @Test
    void details_WithBarThatDoesNotBelongToUser_ReturnsBarNotFound() throws Exception {
        LoginResponse response = getCredentials();

        String detailsJson = """
                {
                    "userId": "9999",
                    "barId": "10"
                }
                """;

        mockMvc.perform(post("/bar/details")
                        .header("Authorization", "Bearer " + response.getAccessToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(detailsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Bar not found."))
                .andExpect(jsonPath("$.id").value(nullValue()))
                .andExpect(jsonPath("$.name").value(nullValue()))
                .andExpect(jsonPath("$.ingredients").value(nullValue()));
    }

}
