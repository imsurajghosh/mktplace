package com.supaki.mktplace.controller;

import com.supaki.mktplace.entities.InventoryStatus;
import com.supaki.mktplace.entities.SaleInventory;
import com.supaki.mktplace.entities.User;
import com.supaki.mktplace.entities.UserInventory;
import com.supaki.mktplace.models.PaymentGatewayCallbackRequest;
import com.supaki.mktplace.models.SaleInventoryDTO;
import com.supaki.mktplace.models.UserInventoryDTO;
import com.supaki.mktplace.repositories.SaleInventoryRepository;
import com.supaki.mktplace.repositories.UserInventoryRepository;
import com.supaki.mktplace.service.InventoryService;
import com.supaki.mktplace.utils.TransformationUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
public class InventoryController {

    @Autowired
    private UserInventoryRepository userInventoryRepository;

    @Autowired
    private SaleInventoryRepository saleInventoryRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private TransformationUtils transformationUtils;

    @GetMapping("/sales")
    public List<SaleInventoryDTO> index(){
        return saleInventoryRepository.findByStatusIn(List.of(InventoryStatus.LISTED)).stream()
                .map(transformationUtils::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/user/{userId}/inventory")
    public List<UserInventoryDTO> getUserInventory(@PathVariable("userId") String userId) {
        List<UserInventory> userInventories = userInventoryRepository.findByUserIdAndIsDeletedFalse(userId);
        return userInventories.stream().map(transformationUtils::convertEntityToDTO).collect(Collectors.toList());
    }

    @PostMapping("/user/{userId}/item/{itemId}/inventory")
    public UserInventoryDTO create(@PathVariable("userId") String userId,
                                @PathVariable("itemId") String itemId,
                                @RequestBody UserInventoryDTO userInventoryDTO) {
        UserInventory saved = inventoryService.createUserInventory(userId, itemId, userInventoryDTO);
        return transformationUtils.convertEntityToDTO(saved);
    }

    @PostMapping("/user/{userId}/inventory/{inventoryId}/sell")
    public SaleInventoryDTO create(@PathVariable("userId") String userId,
                                @PathVariable("inventoryId") String inventoryId,
                                @Valid @RequestBody SaleInventoryDTO saleInventoryDTO) {
        saleInventoryDTO.setInventoryId(inventoryId);
        inventoryService.createSaleInventory(userId, inventoryId, saleInventoryDTO);
        UserInventory userInventory
                = userInventoryRepository.findByInventoryId(inventoryId).get();
        SaleInventory saleInventory
                = saleInventoryRepository.findByInventoryId(inventoryId).get();
        return transformationUtils.convertEntityToDTO(saleInventory, userInventory);
    }

    @GetMapping("/user/{userId}/inventory/{inventoryId}")
    public UserInventoryDTO get(@PathVariable("userId") String ownerId,
                                @PathVariable("inventoryId") String inventoryId) {
        Optional<UserInventory> userInventoryOptional = userInventoryRepository.findByInventoryId(inventoryId);
        if (userInventoryOptional.isEmpty() || !userInventoryOptional.get().getUserId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory Id invalid");
        }
        UserInventory userInventory = userInventoryOptional.get();
        return transformationUtils.convertEntityToDTO(userInventory);
    }

    @PostMapping("/user/{userId}/inventory/{inventoryId}/buy")
    public SaleInventoryDTO create(@PathVariable("userId") String buyerId,
                                   @PathVariable("inventoryId") String inventoryId) {
        Optional<UserInventory> optionalUserInventory =
                userInventoryRepository.findByInventoryId(inventoryId);
        if (optionalUserInventory.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory Id Invalid");
        }
        UserInventory userInventory = optionalUserInventory.get();
        if (!Objects.equals(userInventory.getSaleInventory().getStatus(), InventoryStatus.LISTED)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Inventory item not listed");
        }
        Optional<SaleInventory> responseSaleInventory = inventoryService.updateInventoryStatus(buyerId, inventoryId, userInventory);
        inventoryService.removeLockedItems();
        return transformationUtils.convertEntityToDTO(responseSaleInventory.get());
    }

    @PostMapping("/payment-gateway/callback")
    public ResponseEntity callback(@RequestBody PaymentGatewayCallbackRequest paymentGatewayCallbackRequest) {
        Optional<SaleInventory> optionalSaleInventory
                = saleInventoryRepository.findByInventoryId(paymentGatewayCallbackRequest.getInventoryId());
        if (optionalSaleInventory.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        SaleInventory saleInventory = optionalSaleInventory.get();
        boolean lockedBythisBuyer = InventoryStatus.AWAITING_PAYMENT.equals(saleInventory.getStatus()) &&
                paymentGatewayCallbackRequest.getBuyerId().equals(saleInventory.getBuyerId());
        if ( !"SUCCESS".equals(paymentGatewayCallbackRequest.getPaymentStatus())) {
            if (lockedBythisBuyer) {
                inventoryService.releaseItemFromLock(saleInventory);
            }
            return ResponseEntity.ok().build();
        }
        if (lockedBythisBuyer) {
            inventoryService.markItemAsSoldAndCreateEntryOfbuyer(optionalSaleInventory);
            return ResponseEntity.ok().build();
        }
        // if payment is success and not locked by this buyer
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }



    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

}
