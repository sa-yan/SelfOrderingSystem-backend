package com.sayan.selforderingsystem.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sayan.selforderingsystem.dto.ErrorDto;
import com.sayan.selforderingsystem.models.MenuItem;
import com.sayan.selforderingsystem.services.MenuService;
import jakarta.servlet.ServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*")
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<?> getAllMenuItems() {
        return ResponseEntity.ok(menuService.getAllMenuItems());
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableMenus() {
        return ResponseEntity.ok(menuService.getAvailableMenuItems());
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getMenuItemsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(menuService.getMenuItemsByCategory(category));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getMenuItemById(@PathVariable String id){
        return ResponseEntity.ok(menuService.getMenuItemById(id));
    }

    @PostMapping("/admin")
    public ResponseEntity<?> createMenuItem(
            @RequestPart("menuItem") String menuItemJson,
            @RequestPart("image") MultipartFile file) throws IOException {

        // Convert JSON string to MenuItem object
        ObjectMapper objectMapper = new ObjectMapper();
        MenuItem menuItem = objectMapper.readValue(menuItemJson, MenuItem.class);

        // Call service to save item
        MenuItem savedItem = menuService.addMenuItem(menuItem, file);

        if (savedItem != null) {
            return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(new ErrorDto("This item already exists", 409), HttpStatus.CONFLICT);
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<?> updateMenuItem(@PathVariable String id, @RequestBody MenuItem requestedMenuItem){
        if(menuService.updateMenuItem(id, requestedMenuItem)!=null){
            return new ResponseEntity<>(menuService.updateMenuItem(id, requestedMenuItem), HttpStatus.OK);
        }else {
            return new ResponseEntity<>(new ErrorDto("This item does not exist", 404), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/admin/{id}/avilability")
    public ResponseEntity<?> updateAvailability(@PathVariable String id,
                                                       @RequestParam boolean availability){
        if(menuService!=null){
            return new ResponseEntity<>(menuService.updateAvailability(id, availability), HttpStatus.OK);
        }else{
            return new ResponseEntity<>(new ErrorDto("This item does not exist", 404), HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable String id) {
        if (menuService.getMenuItemById(id) != null) {
            menuService.deleteMenuItem(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
