package com.wastereborn.service;

import com.wastereborn.model.User;
import com.wastereborn.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return user;
    }

    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email is already taken!");
        }
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            throw new RuntimeException("Phone number is already taken!");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public User addPoints(Long userId, Integer points) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setPointsBalance(user.getPointsBalance() + points);
        return userRepository.save(user);
    }

    public User deductPoints(Long userId, Integer points) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getPointsBalance() < points) {
            throw new RuntimeException("Insufficient points");
        }
        user.setPointsBalance(user.getPointsBalance() - points);
        return userRepository.save(user);
    }

    public List<User> getTopUsersByPoints() {
        return userRepository.findTopUsersByPoints();
    }

    public List<User> getUsersByStreet(String street) {
        return userRepository.findByStreet(street);
    }

    public List<User> getUsersByCity(String city) {
        return userRepository.findByCity(city);
    }

    public User updateUserProfile(Long userId, User updatedUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        user.setAddress(updatedUser.getAddress());
        user.setStreet(updatedUser.getStreet());
        user.setCity(updatedUser.getCity());
        
        return userRepository.save(user);
    }

    public void updateUserStats(Long userId, boolean isOrder, boolean isPickup) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (isOrder) {
            user.setTotalOrders(user.getTotalOrders() + 1);
        }
        
        if (isPickup) {
            user.setTotalPickups(user.getTotalPickups() + 1);
            // Calculate recycling percentage (this is a simple calculation, you can make it more sophisticated)
            double percentage = (user.getTotalPickups() * 10.0); // Each pickup contributes 10% (max 100%)
            user.setRecyclingPercentage(Math.min(percentage, 100.0));
        }
        
        userRepository.save(user);
    }
}
