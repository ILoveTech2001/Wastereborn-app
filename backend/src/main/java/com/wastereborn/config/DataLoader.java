package com.wastereborn.config;

import com.wastereborn.model.User;
import com.wastereborn.model.Category;
import com.wastereborn.repository.UserRepository;
import com.wastereborn.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        createDefaultCategories();
        createAdminUser();
    }

    private void createAdminUser() {
        String adminEmail = "admin@wastereborn.com";
        
        // Check if admin user already exists
        if (userRepository.findByEmail(adminEmail).isPresent()) {
            System.out.println("Admin user already exists: " + adminEmail);
            return;
        }

        // Create admin user
        User adminUser = new User();
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail(adminEmail);
        adminUser.setPhoneNumber("1234567890"); // Required field
        adminUser.setPassword(passwordEncoder.encode("admin123"));
        adminUser.setRole(User.Role.ADMIN);
        adminUser.setEnabled(true);
        adminUser.setPointsBalance(0);
        adminUser.setIsPremium(true);
        adminUser.setRegistrationDate(LocalDateTime.now());
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());

        userRepository.save(adminUser);
        
        System.out.println("✅ Admin user created successfully!");
        System.out.println("📧 Email: " + adminEmail);
        System.out.println("🔑 Password: admin123");
        System.out.println("👤 Role: ADMIN");
    }

    private void createDefaultCategories() {
        String[] categoryNames = {
            "Eco-Friendly Products",
            "Recycled Materials",
            "Home & Garden",
            "Electronics",
            "Fashion & Accessories"
        };

        for (String categoryName : categoryNames) {
            if (categoryRepository.findByName(categoryName).isEmpty()) {
                Category category = new Category();
                category.setName(categoryName);
                category.setDescription("Default " + categoryName + " category");
                category.setCreatedAt(LocalDateTime.now());
                category.setUpdatedAt(LocalDateTime.now());
                categoryRepository.save(category);
                System.out.println("✅ Created category: " + categoryName);
            }
        }
    }
}
