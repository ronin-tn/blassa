package com.blassa.service;

import com.blassa.dto.VehicleDTO;
import com.blassa.model.entity.User;
import com.blassa.model.entity.Vehicle;
import com.blassa.repository.UserRepository;
import com.blassa.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(UUID.randomUUID())
                .email("rayen@blassa.tn")
                .firstName("Rayen")
                .lastName("Test")
                .build();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("rayen@blassa.tn")
                .password("password")
                .authorities("USER")
                .build();

        Authentication auth = mock(Authentication.class);
        lenient().when(auth.getPrincipal()).thenReturn(userDetails);

        SecurityContext ctx = mock(SecurityContext.class);
        lenient().when(ctx.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(ctx);

        lenient().when(userRepository.findByEmail("rayen@blassa.tn")).thenReturn(Optional.of(testUser));
    }

    @Test
    void getMaskedPlate_shouldMaskPlate() {
        String result = vehicleService.getMaskedPlate("123 TU 4567");
        assertEquals("*** 567", result);
    }

    @Test
    void getMaskedPlate_shouldReturnStars_whenPlateIsNull() {
        assertEquals("***", vehicleService.getMaskedPlate(null));
    }

    @Test
    void getMaskedPlate_shouldReturnStars_whenPlateIsTooShort() {
        assertEquals("***", vehicleService.getMaskedPlate("AB"));
    }

    @Test
    void createVehicle_shouldSaveAndReturnDTO() {
        VehicleDTO request = new VehicleDTO(null, "Peugeot", "208", "Blanc", "200 TN 1234", 2022);

        Vehicle savedVehicle = new Vehicle();
        savedVehicle.setId(UUID.randomUUID());
        savedVehicle.setOwner(testUser);
        savedVehicle.setMake("Peugeot");
        savedVehicle.setModel("208");
        savedVehicle.setColor("Blanc");
        savedVehicle.setLicensePlate("200 TN 1234");
        savedVehicle.setProductionYear(2022);

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        VehicleDTO result = vehicleService.createVehicle(request);

        assertNotNull(result);
        assertEquals("Peugeot", result.make());
        assertEquals("208", result.model());
        assertEquals("Blanc", result.color());
        assertEquals("200 TN 1234", result.licensePlate());
        assertEquals(2022, result.productionYear());
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void deleteVehicle_shouldThrow_whenNotOwner() {
        User otherUser = User.builder()
                .id(UUID.randomUUID())
                .build();

        Vehicle vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setOwner(otherUser);

        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> vehicleService.deleteVehicle(vehicle.getId()));

        assertEquals("Not authorized to delete this vehicle", ex.getMessage());
        verify(vehicleRepository, never()).delete(any());
    }

    @Test
    void deleteVehicle_shouldDelete_whenOwner() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setOwner(testUser);

        when(vehicleRepository.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        vehicleService.deleteVehicle(vehicle.getId());

        verify(vehicleRepository).delete(vehicle);
    }
}
