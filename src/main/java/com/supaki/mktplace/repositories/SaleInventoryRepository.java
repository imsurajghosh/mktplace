package com.supaki.mktplace.repositories;

import com.supaki.mktplace.entities.InventoryStatus;
import com.supaki.mktplace.entities.SaleInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SaleInventoryRepository extends JpaRepository<SaleInventory, Integer> {

    @Modifying
    @Query(value = "UPDATE sale_inventory si SET si.status = ? , si.buyer_id = ?, si.updated_at = ? WHERE si.inventory_id = ? AND si.status = ?",
            nativeQuery = true)
    int updateInventoryStatus(InventoryStatus newStatus, String buyerId, long updatedAt,
                              String inventoryId, InventoryStatus oldStatus);

    Optional<SaleInventory> findByInventoryId(String inventoryId);

    List<SaleInventory> findByStatusAndUpdatedAtBefore(InventoryStatus status, long timestamp);

    List<SaleInventory> findByStatusIn(List<InventoryStatus> statuses);
}
