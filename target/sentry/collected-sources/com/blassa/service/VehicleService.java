package com.blassa.service;

import com.blassa.dto.VehicleDTO;
import com.blassa.model.entity.User;
import com.blassa.model.entity.Vehicle;
import com.blassa.repository.UserRepository;
import com.blassa.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    @Transactional
    public VehicleDTO createVehicle(VehicleDTO request) {
        User user = getCurrentUser();

        Vehicle vehicle = new Vehicle();
        vehicle.setOwner(user);
        vehicle.setMake(request.make());
        vehicle.setModel(request.model());
        vehicle.setColor(request.color());
        vehicle.setLicensePlate(request.licensePlate());
        vehicle.setProductionYear(request.productionYear());

        Vehicle saved = vehicleRepository.save(vehicle);
        return mapToDTO(saved);
    }

    public List<VehicleDTO> getMyVehicles() {
        User user = getCurrentUser();
        return vehicleRepository.findByOwnerId(user.getId()).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteVehicle(UUID id) {
        User user = getCurrentUser();
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        if (!vehicle.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to delete this vehicle");
        }

        vehicleRepository.delete(vehicle);
    }

    public Vehicle getVehicleEntity(UUID id) {
        User user = getCurrentUser(); // Ensure ownership check if needed, or allow if ride creation uses it
        return vehicleRepository.findById(id).orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    // lahne nkhabiw fi matricule lel securtiy: returns "*** 123"
    public String getMaskedPlate(String plate) {
        if (plate == null || plate.length() < 3)
            return "***";
        return "*** " + plate.substring(Math.max(0, plate.length() - 3));
    }

    private VehicleDTO mapToDTO(Vehicle vehicle) {
        return new VehicleDTO(
                vehicle.getId(),
                vehicle.getMake(),
                vehicle.getModel(),
                vehicle.getColor(),
                vehicle.getLicensePlate(), // driver ichof matricule kol
                vehicle.getProductionYear());
    }

    private User getCurrentUser() {
        String email = ((UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getUsername();
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }
}
