package com.supaki.mktplace.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "inventory_id", unique = true)
    private String inventoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "user_id")
    private String userId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", referencedColumnName = "item_id", insertable = false, updatable = false)
    private Item item;

    @Column(name = "item_id")
    private String itemId;

    @OneToOne(fetch = FetchType.LAZY,
            mappedBy = "userInventory")
    private SaleInventory saleInventory;

    @Column(name = "purchased_at")
    private long purchasedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @Override
    public String toString() {
        return "UserInventory{" +
                "id=" + id +
                ", inventoryId='" + inventoryId + '\'' +
                ", userId='" + userId + '\'' +
                ", item=" + item +
                ", itemId='" + itemId + '\'' +
                ", purchasedAt=" + purchasedAt +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
