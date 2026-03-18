package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {
    
    private final InventoryRepository inventoryRepository;
    
    @Transactional
    public boolean checkAvailability(String product, int quantity) {
        Optional<Inventory> inventoryOpt = inventoryRepository.findByProduct(product);
        
        if (inventoryOpt.isEmpty()) {
            log.warn("Product not found: {}", product);
            return false;
        }
        
        Inventory inventory = inventoryOpt.get();
        boolean isAvailable = inventory.getQuantity() >= quantity;
        
        log.info("Checking availability for product: {}, requested: {}, available: {}, available: {}", 
            product, quantity, inventory.getQuantity(), isAvailable);
        
        if (isAvailable) {
            // Reserve the inventory
            inventory.setQuantity(inventory.getQuantity() - quantity);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
            inventoryRepository.save(inventory);
            log.info("Inventory reserved for product: {}, remaining: {}", 
                product, inventory.getQuantity());
        }
        
        return isAvailable;
    }
    
    public Optional<Inventory> getInventory(String product) {
        return inventoryRepository.findByProduct(product);
    }
    
    public Inventory addInventory(String product, int quantity) {
        Optional<Inventory> existingInventory = inventoryRepository.findByProduct(product);
        
        if (existingInventory.isPresent()) {
            Inventory inventory = existingInventory.get();
            inventory.setQuantity(inventory.getQuantity() + quantity);
            Inventory saved = inventoryRepository.save(inventory);
            log.info("Added inventory for product: {}, new quantity: {}", 
                product, saved.getQuantity());
            return saved;
        } else {
            Inventory newInventory = new Inventory();
            newInventory.setProduct(product);
            newInventory.setQuantity(quantity);
            newInventory.setReservedQuantity(0);
            Inventory saved = inventoryRepository.save(newInventory);
            log.info("Created new inventory for product: {}, quantity: {}", 
                product, saved.getQuantity());
            return saved;
        }
    }
}
