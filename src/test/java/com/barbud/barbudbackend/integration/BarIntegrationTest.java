package com.barbud.barbudbackend.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.jayway.jsonpath.JsonPath;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BarIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private String getAccessToken() throws Exception {
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

        return JsonPath.read(response, "$.accessToken");
    }

    @Test
    void ingredients_ReturnsIngredientList() throws Exception {
        String accessToken = getAccessToken();

        mockMvc.perform(get("/bar/ingredients")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].name").value(not(emptyOrNullString())))
                .andExpect(jsonPath("$[0].category").value(not(emptyOrNullString())));
    }

    @Test
    void myBars_WithExistingUser_ReturnsBars() throws Exception {
        String accessToken = getAccessToken();

        String myBarsJson = """
                {
                    "userId": 3
                }
                """;

        mockMvc.perform(post("/bar/my-bars")
                        .header("Authorization", "Bearer " + accessToken)
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
        String accessToken = getAccessToken();

        String myBarsJson = """
                {
                    "userId": 9999
                }
                """;

        mockMvc.perform(post("/bar/my-bars")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(myBarsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void details_WithExistingBar_ReturnsBarDetails() throws Exception {
        String accessToken = getAccessToken();

        String detailsJson = """
                {
                    "userId": 3,
                    "barId": 2
                }
                """;

        mockMvc.perform(post("/bar/details")
                        .header("Authorization", "Bearer " + accessToken)
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
        String accessToken = getAccessToken();

        String detailsJson = """
                {
                    "userId": 9999,
                    "barId": 10
                }
                """;

        mockMvc.perform(post("/bar/details")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(detailsJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Bar not found."))
                .andExpect(jsonPath("$.id").value(nullValue()))
                .andExpect(jsonPath("$.name").value(nullValue()))
                .andExpect(jsonPath("$.ingredients").value(nullValue()));
    }

}
