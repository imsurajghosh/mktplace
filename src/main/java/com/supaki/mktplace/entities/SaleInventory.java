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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SaleInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", referencedColumnName = "inventory_id", insertable = false, updatable = false)
    private UserInventory userInventory;

    @Column(name = "inventory_id", unique = true)
    private String inventoryId;

    @Column(name = "listing_price")
    private long listingPrice;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private long updatedAt;

    private InventoryStatus status;

    @ManyToOne(fetch = FetchType.LAZY,
            cascade = CascadeType.ALL)
    @JoinColumn(name = "buyer_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User buyer;

    @Column(name = "buyer_id")
    private String buyerId;

    @Override
    public String toString() {
        return "SaleInventory{" +
                "id=" + id +
                ", inventoryId='" + inventoryId + '\'' +
                ", listingPrice=" + listingPrice +
                ", updatedAt=" + updatedAt +
                ", status=" + status +
                ", buyerId='" + buyerId + '\'' +
                '}';
    }
}
