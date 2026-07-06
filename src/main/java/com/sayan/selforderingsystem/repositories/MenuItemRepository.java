package com.sayan.selforderingsystem.repositories;

import com.sayan.selforderingsystem.models.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, String> {
    List<MenuItem> findByCategory(String category);
    List<MenuItem> findByIsavailableTrue();
    MenuItem findByName(String name);
}
