package com.supaki.mktplace.service;

import com.supaki.mktplace.entities.InventoryStatus;
import com.supaki.mktplace.entities.PurchaseCounter;
import com.supaki.mktplace.entities.SaleInventory;
import com.supaki.mktplace.entities.UserInventory;
import com.supaki.mktplace.models.SaleInventoryDTO;
import com.supaki.mktplace.models.UserInventoryDTO;
//import com.supaki.mktplace.repositories.AccountDetailRepository;
import com.supaki.mktplace.repositories.PurchaseCounterRepository;
import com.supaki.mktplace.repositories.SaleInventoryRepository;
import com.supaki.mktplace.repositories.UserInventoryRepository;
import com.supaki.mktplace.utils.CounterGenUtils;
import com.supaki.mktplace.utils.IDGenUtils;
import com.supaki.mktplace.utils.TransformationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class InventoryService {

    @Autowired
    private SaleInventoryRepository saleInventoryRepository;

    @Autowired
    private UserInventoryRepository userInventoryRepository;

//    @Autowired
//    private AccountDetailRepository accountDetailRepository;

    @Autowired
    private PurchaseCounterRepository purchaseCounterRepository;

    @Autowired
    private TransformationUtils transformationUtils;

    private boolean removeLockedItemsProcessorStarted = false;

    @Transactional
    public Optional<SaleInventory> updateInventoryStatus(String buyerId, String inventoryId, UserInventory userInventory) {
        Optional<PurchaseCounter> buyerMonthCounter
                = purchaseCounterRepository.findByCounterKey(CounterGenUtils.buyerMonthlyKey(buyerId));
        if (buyerMonthCounter.isPresent() && buyerMonthCounter.get().getCounter() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Buyer already bought an item this month");
        }
        int changedRows = saleInventoryRepository.updateInventoryStatus(InventoryStatus.AWAITING_PAYMENT, buyerId,
                new Date().getTime(), userInventory.getInventoryId(), InventoryStatus.LISTED);
        if (changedRows == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory item not listed");
        }
        Optional<SaleInventory> responseSaleInventory = saleInventoryRepository.findByInventoryId(inventoryId);
        return responseSaleInventory;
    }

    @Transactional
    public void markItemAsSoldAndCreateEntryOfbuyer(Optional<SaleInventory> optionalSaleInventory) {
        SaleInventory saleInventory = optionalSaleInventory.get();
        saleInventory.setStatus(InventoryStatus.SOLD);
        UserInventory userInventory = saleInventory.getUserInventory();
        userInventory.setDeleted(true);

        UserInventory buyerInventory = new UserInventory();
        buyerInventory.setInventoryId(IDGenUtils.inventoryIdGenerate());
        buyerInventory.setUserId(saleInventory.getBuyerId());
        buyerInventory.setItemId(userInventory.getItemId());
        buyerInventory.setPurchasedAt(new Date().getTime());

        String buyerMonthlyKey = CounterGenUtils.buyerMonthlyKey(saleInventory.getBuyerId());
        String buyerDailyKey = CounterGenUtils.buyerItemDailyKey(saleInventory.getBuyerId(), userInventory.getItemId());

        PurchaseCounter buyerMonthlyCounter = PurchaseCounter.builder()
                .counterKey(buyerMonthlyKey)
                .counter(1)
                .build();

        PurchaseCounter buyerDailyCounter = PurchaseCounter.builder()
                .counterKey(buyerDailyKey)
                .counter(1)
                .build();

        saleInventoryRepository.save(saleInventory);
        userInventoryRepository.save(userInventory);
        userInventoryRepository.save(buyerInventory);
        purchaseCounterRepository.save(buyerDailyCounter);
        purchaseCounterRepository.save(buyerMonthlyCounter);
        settleWithSeller(userInventory, saleInventory);
    }

    @Async
    public void settleWithSeller(UserInventory userInventory, SaleInventory saleInventory) {
//        List<AccountDetail> accountDetails = accountDetailRepository.findByUserId(userInventory.getUserId());
        // settle for this inventory and sale
    }

    public UserInventory createUserInventory(String userId, String itemId, UserInventoryDTO userInventoryDTO) {
        UserInventory userInventory = transformationUtils.convertDtoToEntity(userInventoryDTO);
        userInventory.setInventoryId(IDGenUtils.inventoryIdGenerate());
        userInventory.setUserId(userId);
        userInventory.setItemId(itemId);
        userInventory.setPurchasedAt(new Date().getTime());
        UserInventory saved = userInventoryRepository.save(
                userInventory
        );
        return saved;
    }

    @Transactional
    public SaleInventory createSaleInventory(String userId, String inventoryId, SaleInventoryDTO saleInventoryDTO) {
        Optional<UserInventory> optionalUserInventory =
                userInventoryRepository.findByInventoryId(inventoryId);
        if (optionalUserInventory.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory Id Invalid");
        }
        UserInventory userInventory = optionalUserInventory.get();
        SaleInventory alreadyInSaleInventory = userInventory.getSaleInventory();
        if (!Objects.equals(userInventory.getUserId(), userId) || alreadyInSaleInventory != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory or item invalid");
        }
        Optional<PurchaseCounter> buyerItemDailyCounter = purchaseCounterRepository.findByCounterKey(
                CounterGenUtils.buyerItemDailyKey(userId, userInventory.getItemId())
        );
        if (buyerItemDailyCounter.isPresent() && buyerItemDailyCounter.get().getCounter() > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Buyer not allowed to sell this item today");
        }
        SaleInventory saleInventory = transformationUtils.convertDtoToEntity(saleInventoryDTO);
        saleInventory.setUpdatedAt(new Date().getTime());
        saleInventory.setStatus(InventoryStatus.LISTED);
        SaleInventory save = saleInventoryRepository.saveAndFlush(
                saleInventory
        );
        return save;
    }

    @Async
    public void removeLockedItems() {
        if (removeLockedItemsProcessorStarted) {
            return;
        }
        removeLockedItemsProcessorStarted = true;
        while(true) {
            try {
                long timeQuery = new Date().getTime() - 300000; // last 5 minutes
                System.out.println("looking for locked inventories before:" + timeQuery);
                List<SaleInventory> lockedSaleInventories
                        = saleInventoryRepository.findByStatusAndUpdatedAtBefore(InventoryStatus.AWAITING_PAYMENT, timeQuery);
                lockedSaleInventories.forEach(saleInventory -> {
                    saleInventory.setStatus(InventoryStatus.LISTED);
                    saleInventory.setBuyerId(null);
                    saleInventoryRepository.save(saleInventory);
                });
                Thread.sleep(60000); // every minute
            } catch (InterruptedException e) {
                System.out.println("shutting down async thread");
                break;
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        removeLockedItemsProcessorStarted = false;
    }

    @Transactional
    public void releaseItemFromLock(SaleInventory saleInventory) {
        saleInventory.setBuyerId(null);
        saleInventory.setStatus(InventoryStatus.LISTED);
        saleInventoryRepository.save(saleInventory);
    }
}
