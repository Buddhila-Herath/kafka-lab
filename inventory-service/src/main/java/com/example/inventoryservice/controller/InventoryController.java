package com.example.inventoryservice.controller;

import com.example.inventoryservice.entity.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryRepository inventoryRepository;
    
    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
    
    @GetMapping("/{product}")
    public ResponseEntity<Inventory> getInventoryByProduct(@PathVariable String product) {
        return inventoryRepository.findByProduct(product)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Inventory addInventory(@RequestBody Inventory inventory) {
        return inventoryRepository.save(inventory);
    }
    
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Inventory Service is running on port 8082");
    }
}
