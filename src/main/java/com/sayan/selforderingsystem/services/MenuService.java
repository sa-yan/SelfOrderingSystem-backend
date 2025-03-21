package com.sayan.selforderingsystem.services;

import com.sayan.selforderingsystem.models.MenuItem;
import com.sayan.selforderingsystem.repositories.MenuItemRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final CloudinaryService cloudinaryService;

    public MenuItem addMenuItem(MenuItem menuItem, MultipartFile file) throws IOException {
        String name = menuItem.getName();

        if (menuItemRepository.findByName(name) != null) {
            return null; // Item already exists
        }

        Map data = cloudinaryService.upload(file);
        String imageUrl = data.get("url").toString();
        menuItem.setPicUrl(imageUrl);

        return menuItemRepository.save(menuItem);
    }


    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    public List<MenuItem> getAvailableMenuItems() {
        return menuItemRepository.findByIsavailableTrue();
    }

    public List<MenuItem> getMenuItemsByCategory(String category) {
        return menuItemRepository.findByCategory(category);
    }

    public List<MenuItem> getMenuItemById(String id) {
        List<MenuItem> result = new ArrayList<>();
        menuItemRepository.findById(id)
                .ifPresent(result::add);
        return result;
    }

    public MenuItem updateMenuItem(String id, MenuItem requestedMenuItem){
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if(menuItem.isPresent()) {
            MenuItem menuItem1 = menuItem.get();
            menuItem1.setName(requestedMenuItem.getName());
            menuItem1.setDescription(requestedMenuItem.getDescription());
            menuItem1.setIsavailable(requestedMenuItem.isIsavailable());
            menuItem1.setCategory(requestedMenuItem.getCategory());
            menuItem1.setPrice(requestedMenuItem.getPrice());
            return menuItemRepository.save(menuItem1);
        }
        return null;
    }

    public MenuItem updateAvailability(String id, boolean availability) {
        Optional<MenuItem> menuItem = menuItemRepository.findById(id);
        if(menuItem.isPresent()) {
            MenuItem menuItem1 = menuItem.get();
            menuItem1.setIsavailable(availability);
            return menuItemRepository.save(menuItem1);
        }
        return null;
    }

    public void deleteMenuItem(String id) {
        menuItemRepository.deleteById(id);
    }
}
