package com.wastereborn.service;

import com.wastereborn.model.Product;
import com.wastereborn.model.User;
import com.wastereborn.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategoryNameAndAvailable(category);
    }

    public List<Product> getPointsRedeemableProducts() {
        return productRepository.findPointsRedeemableProducts();
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword);
    }

    public List<String> getCategories() {
        return productRepository.findDistinctCategories()
                .stream()
                .map(category -> category.getName())
                .collect(java.util.stream.Collectors.toList());
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(Product product, User createdBy) {
        product.setCreatedBy(createdBy);
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setCategory(updatedProduct.getCategory());
        product.setPrice(updatedProduct.getPrice());
        product.setPointsPrice(updatedProduct.getPointsPrice());
        product.setImageUrl(updatedProduct.getImageUrl());
        product.setStockQuantity(updatedProduct.getStockQuantity());
        product.setIsAvailable(updatedProduct.getIsAvailable());
        product.setIsPointsRedeemable(updatedProduct.getIsPointsRedeemable());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setStockQuantity(product.getStockQuantity() + quantity);
        return productRepository.save(product);
    }

    public Product reduceStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock");
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        return productRepository.save(product);
    }

    public Product toggleAvailability(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsAvailable(!product.getIsAvailable());
        return productRepository.save(product);
    }

    public Product togglePointsRedeemable(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setIsPointsRedeemable(!product.getIsPointsRedeemable());
        return productRepository.save(product);
    }
}
