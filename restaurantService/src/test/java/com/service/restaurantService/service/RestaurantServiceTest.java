package com.service.restaurantService.service;

import com.service.restaurantService.dto.UserResponseDto;
import com.service.restaurantService.entities.Restaurant;
import com.service.restaurantService.exception.ResourceNotFoundException;
import com.service.restaurantService.exception.UnauthorizedException;
import com.service.restaurantService.repo.RestaurantRepository;
import com.service.restaurantService.service.feignClientServices.UserClient;
import com.service.restaurantService.service.impl.RestaurantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private RestaurantServiceImpl restaurantService;

    private Restaurant restaurant;
    private UserResponseDto ownerDto;
    private String authHeader = "Bearer dummy";

    @BeforeEach
    void setup() {
        restaurant = new Restaurant();
        restaurant.setId("r1");
        restaurant.setOwner("user1");
        restaurant.setName("Pizza Palace");
        restaurant.setCuisineType("Italian");
        restaurant.setOpen(false);

        ownerDto = new UserResponseDto();
        ownerDto.setUserId("user1");
        ownerDto.setRole("OWNER");
    }

    // ------------------------------------------------------
    // createRestaurant()
    // ------------------------------------------------------
    @Test
    void createRestaurant_ShouldSave_WhenUserIsOwner() throws Exception {
        when(userClient.getUserHandler(authHeader)).thenReturn(ownerDto);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurant saved = restaurantService.createRestaurant(restaurant, authHeader);

        assertNotNull(saved);
        assertEquals("user1", saved.getOwner());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void createRestaurant_ShouldThrow_WhenUserNotOwner() {
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId("user2");
        dto.setRole("CUSTOMER");
        when(userClient.getUserHandler(authHeader)).thenReturn(dto);

        assertThrows(UnauthorizedException.class,
                () -> restaurantService.createRestaurant(restaurant, authHeader));
        verify(restaurantRepository, never()).save(any());
    }

    // ------------------------------------------------------
    // getRestaurantById()
    // ------------------------------------------------------
    @Test
    void getRestaurantById_ShouldReturn_WhenExists() throws Exception {
        when(restaurantRepository.findById("r1")).thenReturn(Optional.of(restaurant));

        Restaurant result = restaurantService.getRestaurantById("r1");

        assertEquals("Pizza Palace", result.getName());
        verify(restaurantRepository).findById("r1");
    }

    @Test
    void getRestaurantById_ShouldThrow_WhenNotFound() {
        when(restaurantRepository.findById("r1")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> restaurantService.getRestaurantById("r1"));
    }

    // ------------------------------------------------------
    // updateRestaurant()
    // ------------------------------------------------------
    @Test
    void updateRestaurant_ShouldUpdate_WhenOwnerValid() throws Exception {
        when(restaurantRepository.findById("r1")).thenReturn(Optional.of(restaurant));
        when(userClient.getUserHandler(authHeader)).thenReturn(ownerDto);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurant updated = new Restaurant();
        updated.setName("New Pizza Place");
        updated.setDescription("Updated desc");

        Restaurant result = restaurantService.updateRestaurant("r1", updated, authHeader);

        assertEquals("New Pizza Place", result.getName());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void updateRestaurant_ShouldThrow_WhenUnauthorized() {
        when(restaurantRepository.findById("r1")).thenReturn(Optional.of(restaurant));
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId("userX");
        when(userClient.getUserHandler(authHeader)).thenReturn(dto);

        assertThrows(UnauthorizedException.class,
                () -> restaurantService.updateRestaurant("r1", restaurant, authHeader));
    }

    // ------------------------------------------------------
    // deleteRestaurant()
    // ------------------------------------------------------
    @Test
    void deleteRestaurant_ShouldDelete_WhenOwnerValid() throws Exception {
        when(restaurantRepository.findById("r1")).thenReturn(Optional.of(restaurant));
        when(userClient.getUserHandler(authHeader)).thenReturn(ownerDto);

        restaurantService.deleteRestaurant("r1", authHeader);

        verify(restaurantRepository).deleteById("r1");
    }

    @Test
    void deleteRestaurant_ShouldThrow_WhenUnauthorized() {
        when(restaurantRepository.findById("r1")).thenReturn(Optional.of(restaurant));
        UserResponseDto dto = new UserResponseDto();
        dto.setUserId("userX");
        when(userClient.getUserHandler(authHeader)).thenReturn(dto);

        assertThrows(UnauthorizedException.class,
                () -> restaurantService.deleteRestaurant("r1", authHeader));
        verify(restaurantRepository, never()).deleteById(any());
    }

    // ------------------------------------------------------
    // getRestaurantsByOwner()
    // ------------------------------------------------------
    @Test
    void getRestaurantsByOwner_ShouldReturnList() throws Exception {
        when(restaurantRepository.findByOwner("user1"))
                .thenReturn(Arrays.asList(restaurant));

        List<Restaurant> list = restaurantService.getRestaurantsByOwner("user1");

        assertEquals(1, list.size());
        assertEquals("Pizza Palace", list.get(0).getName());
    }

    // ------------------------------------------------------
    // getRestaurantsByCuisine()
    // ------------------------------------------------------
    @Test
    void getRestaurantsByCuisine_ShouldReturnList() throws Exception {
        when(restaurantRepository.findByCuisineType("Italian"))
                .thenReturn(Arrays.asList(restaurant));

        List<Restaurant> list = restaurantService.getRestaurantsByCuisine("Italian");

        assertEquals(1, list.size());
        assertEquals("Italian", list.get(0).getCuisineType());
    }

    // ------------------------------------------------------
    // openRestaurant() / closeRestaurant()
    // ------------------------------------------------------
    @Test
    void openRestaurant_ShouldSetOpenTrue() throws Exception {
        when(restaurantRepository.findById("r1")).thenReturn(Optional.of(restaurant));
        when(userClient.getUserHandler(authHeader)).thenReturn(ownerDto);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurant result = restaurantService.openRestaurant("r1", authHeader);

        assertTrue(result.isOpen());
        verify(restaurantRepository).save(restaurant);
    }

    @Test
    void closeRestaurant_ShouldSetOpenFalse() throws Exception {
        restaurant.setOpen(true);
        when(restaurantRepository.findById("r1")).thenReturn(Optional.of(restaurant));
        when(userClient.getUserHandler(authHeader)).thenReturn(ownerDto);
        when(restaurantRepository.save(any(Restaurant.class))).thenAnswer(inv -> inv.getArgument(0));

        Restaurant result = restaurantService.closeRestaurant("r1", authHeader);

        assertFalse(result.isOpen());
        verify(restaurantRepository).save(restaurant);
    }

    // ------------------------------------------------------
    // getAllRestaurants()
    // ------------------------------------------------------
    @Test
    void getAllRestaurants_ShouldReturnAll() throws Exception {
        when(restaurantRepository.findAll()).thenReturn(List.of(restaurant));

        List<Restaurant> all = restaurantService.getAllRestaurants();

        assertEquals(1, all.size());
        assertEquals("Pizza Palace", all.getFirst().getName());
    }
}
