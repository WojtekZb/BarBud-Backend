package com.barbud.barbudbackend.services;

import com.barbud.barbudbackend.interfaces.IBarRepo;
import com.barbud.barbudbackend.requests.BarDetailsRequest;
import com.barbud.barbudbackend.requests.CreateBarRequest;
import com.barbud.barbudbackend.requests.UpdateBarRequest;
import com.barbud.barbudbackend.responses.BarDetailsResponse;
import com.barbud.barbudbackend.responses.BarIngredientsResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BarServiceTest {
    private IBarRepo barRepo;
    private BarService barService;

    @BeforeEach
    void setUp() {
        barRepo = mock(IBarRepo.class);
        barService = new BarService(barRepo);
    }

    @Test
    void createBarWithIngredients_WithNewBarName_ReturnsBarAdded() {
        // Arrange
        CreateBarRequest request = new CreateBarRequest(
                1L,
                "My Bar",
                List.of(1L, 2L, 3L)
        );

        when(barRepo.barNameExistsForUser(1L, "My Bar"))
                .thenReturn(false);

        when(barRepo.createBarWithIngredients(1L, "My Bar", List.of(1L, 2L, 3L)))
                .thenReturn("Bar added");

        // Act
        String result = barService.createBarWithIngredients(request);

        // Assert
        assertEquals("Bar added", result);
    }

    @Test
    void createBarWithIngredients_WithExistingBarName_ReturnsDuplicateMessage() {
        // Arrange
        CreateBarRequest request = new CreateBarRequest(
                1L,
                "My Bar",
                List.of(1L, 2L, 3L)
        );

        when(barRepo.barNameExistsForUser(1L, "My Bar"))
                .thenReturn(true);

        // Act
        String result = barService.createBarWithIngredients(request);

        // Assert
        assertEquals("You already have a bar with this name.", result);
    }

    @Test
    void getBarDetails_WhenBarBelongsToUser_ReturnsBarDetails() {
        // Arrange
        BarDetailsRequest request = new BarDetailsRequest(
                1L,
                10L
        );

        List<BarIngredientsResponse> ingredients = List.of(
                new BarIngredientsResponse(
                        5L,
                        "Vodka",
                        "Spirit"
                )
        );

        BarDetailsResponse repoResponse = new BarDetailsResponse(
                "Bar details.",
                10L,
                "My Bar",
                ingredients
        );

        when(barRepo.barBelongsToUser(1L, 10L))
                .thenReturn(true);

        when(barRepo.getBarDetails(1L, 10L))
                .thenReturn(repoResponse);

        // Act
        BarDetailsResponse response = barService.getBarDetails(request);

        // Assert
        assertNotNull(response);

        assertEquals("Bar details.", response.getMessage());
        assertEquals(10L, response.getId());
        assertEquals("My Bar", response.getName());

        assertNotNull(response.getIngredients());
        assertEquals(1, response.getIngredients().size());

        assertEquals(5L, response.getIngredients().get(0).getId());
        assertEquals("Vodka", response.getIngredients().get(0).getName());
        assertEquals("Spirit", response.getIngredients().get(0).getCategory());
    }

    @Test
    void getBarDetails_WhenBarDoesNotBelongToUser_ReturnsBarNotFound() {
        // Arrange
        BarDetailsRequest request = new BarDetailsRequest(
                1L,
                10L
        );

        when(barRepo.barBelongsToUser(1L, 10L))
                .thenReturn(false);

        // Act
        BarDetailsResponse response = barService.getBarDetails(request);

        // Assert
        assertNotNull(response);

        assertEquals("Bar not found.", response.getMessage());
        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getIngredients());
    }

    @Test
    void updateBar_WhenValidRequest_ReturnsBarUpdated() {
        // Arrange
        UpdateBarRequest request = new UpdateBarRequest(
                1L,
                10L,
                "Updated Bar",
                List.of(1L, 2L, 3L)
        );

        when(barRepo.barBelongsToUser(1L, 10L))
                .thenReturn(true);

        when(barRepo.barNameExistsForUserExceptCurrentBar(1L, 10L, "Updated Bar"))
                .thenReturn(false);

        when(barRepo.updateBar(1L, 10L, "Updated Bar", List.of(1L, 2L, 3L)))
                .thenReturn("Bar Updated");

        // Act
        String result = barService.updateBar(request);

        // Assert
        assertEquals("Bar Updated", result);
    }

    @Test
    void updateBar_WhenBarDoesNotBelongToUser_ReturnsBarNotFound() {
        // Arrange
        UpdateBarRequest request = new UpdateBarRequest(
                1L,
                10L,
                "Updated Bar",
                List.of(1L, 2L, 3L)
        );

        when(barRepo.barBelongsToUser(1L, 10L))
                .thenReturn(false);

        // Act
        String result = barService.updateBar(request);

        // Assert
        assertEquals("Bar not found", result);
    }

    @Test
    void updateBar_WhenBarNameAlreadyExists_ReturnsDuplicateMessage() {
        // Arrange
        UpdateBarRequest request = new UpdateBarRequest(
                1L,
                10L,
                "Updated Bar",
                List.of(1L, 2L, 3L)
        );

        when(barRepo.barBelongsToUser(1L, 10L))
                .thenReturn(true);

        when(barRepo.barNameExistsForUserExceptCurrentBar(1L, 10L, "Updated Bar"))
                .thenReturn(true);

        // Act
        String result = barService.updateBar(request);

        // Assert
        assertEquals("You already have a bar with this name", result);
    }

}
