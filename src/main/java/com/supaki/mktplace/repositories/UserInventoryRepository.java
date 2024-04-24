package com.supaki.mktplace.repositories;

import com.supaki.mktplace.entities.UserInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserInventoryRepository extends JpaRepository<UserInventory, Integer> {

    List<UserInventory> findByUserIdAndIsDeletedFalse(String userId);

    Optional<UserInventory> findByInventoryId(String inventoryId);
}
