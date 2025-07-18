package com.wastereborn.service;

import com.wastereborn.model.Driver;
import com.wastereborn.model.PickupRequest;
import com.wastereborn.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public List<Driver> getActiveDrivers() {
        return driverRepository.findByIsActiveTrue();
    }

    public List<Driver> getAvailableDrivers() {
        return driverRepository.findAvailableDrivers();
    }

    public List<Driver> getDriversByStatus(Driver.DriverStatus status) {
        return driverRepository.findByStatus(status);
    }

    public Optional<Driver> getDriverById(Long id) {
        return driverRepository.findById(id);
    }

    public Optional<Driver> getDriverByEmail(String email) {
        return driverRepository.findByEmail(email);
    }

    public Driver createDriver(Driver driver) {
        if (driverRepository.existsByEmail(driver.getEmail())) {
            throw new RuntimeException("Driver with this email already exists");
        }
        if (driverRepository.existsByPhone(driver.getPhone())) {
            throw new RuntimeException("Driver with this phone number already exists");
        }
        return driverRepository.save(driver);
    }

    public Driver updateDriver(Long id, Driver updatedDriver) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setName(updatedDriver.getName());
        driver.setEmail(updatedDriver.getEmail());
        driver.setPhone(updatedDriver.getPhone());
        driver.setLicenseNumber(updatedDriver.getLicenseNumber());
        driver.setVehicleType(updatedDriver.getVehicleType());
        driver.setVehiclePlate(updatedDriver.getVehiclePlate());
        driver.setAddress(updatedDriver.getAddress());
        driver.setCurrentLocation(updatedDriver.getCurrentLocation());

        return driverRepository.save(driver);
    }

    public Driver updateDriverStatus(Long id, Driver.DriverStatus status) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setStatus(status);
        driver.updateLastActive();
        return driverRepository.save(driver);
    }

    public Driver updateDriverLocation(Long id, String location) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setCurrentLocation(location);
        driver.updateLastActive();
        return driverRepository.save(driver);
    }

    public void deleteDriver(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        
        driver.setIsActive(false);
        driver.setStatus(Driver.DriverStatus.INACTIVE);
        driverRepository.save(driver);
    }

    public Driver assignPickup(Long driverId, PickupRequest pickup) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        if (driver.getStatus() != Driver.DriverStatus.AVAILABLE) {
            throw new RuntimeException("Driver is not available");
        }

        driver.setStatus(Driver.DriverStatus.BUSY);
        driver.setTotalPickups(driver.getTotalPickups() + 1);
        driver.updateLastActive();

        return driverRepository.save(driver);
    }

    public Driver completePickup(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setStatus(Driver.DriverStatus.AVAILABLE);
        driver.setCompletedPickups(driver.getCompletedPickups() + 1);
        driver.updateLastActive();

        return driverRepository.save(driver);
    }

    public Driver updateDriverRating(Long driverId, Double newRating) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        // Simple rating update - in a real system, you'd calculate average from multiple ratings
        driver.setRating(newRating);
        return driverRepository.save(driver);
    }

    public List<Driver> findBestAvailableDrivers() {
        return driverRepository.findActiveDriversOrderByRating();
    }

    public Long getActiveDriverCount() {
        return driverRepository.countActiveDrivers();
    }

    public Long getAvailableDriverCount() {
        return driverRepository.countAvailableDrivers();
    }
}
