package com.supaki.mktplace.utils;

import com.supaki.mktplace.entities.Item;
import com.supaki.mktplace.entities.SaleInventory;
import com.supaki.mktplace.entities.User;
import com.supaki.mktplace.entities.UserInventory;
import com.supaki.mktplace.models.ItemDTO;
import com.supaki.mktplace.models.SaleInventoryDTO;
import com.supaki.mktplace.models.UserDTO;
import com.supaki.mktplace.models.UserInventoryDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransformationUtils {

    @Autowired
    ModelMapper modelMapper;

    public Item convertDtoToEntity(ItemDTO itemDTO) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(itemDTO, Item.class);
    }

    public ItemDTO convertEntityToDTO(Item item) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(item, ItemDTO.class);
    }

    public User convertDtoToEntity(UserDTO userDTO) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userDTO, User.class);
    }

    public UserDTO convertEntityToDTO(User user) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(user, UserDTO.class);
    }

    public UserInventory convertDtoToEntity(UserInventoryDTO userInventoryDTO) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userInventoryDTO, UserInventory.class);
    }

    public UserInventoryDTO convertEntityToDTO(UserInventory userInventory) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userInventory, UserInventoryDTO.class);
    }

    public UserInventory copyEntity(UserInventory userInventory) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(userInventory, UserInventory.class);
    }

    public SaleInventory convertDtoToEntity(SaleInventoryDTO saleInventoryDTO) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper.map(saleInventoryDTO, SaleInventory.class);
    }

    public SaleInventoryDTO convertEntityToDTO(SaleInventory saleInventory) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        SaleInventoryDTO saleInventoryDTO = modelMapper.map(saleInventory, SaleInventoryDTO.class);
        UserInventory userInventory = saleInventory.getUserInventory();
        saleInventoryDTO.setItemId(userInventory.getItemId());
        saleInventoryDTO.setOwnerId(userInventory.getUserId());
        saleInventoryDTO.setItemName(userInventory.getItem().getName());
        return saleInventoryDTO;
    }

    public SaleInventoryDTO convertEntityToDTO(SaleInventory saleInventory, UserInventory userInventory) {
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        SaleInventoryDTO saleInventoryDTO = modelMapper.map(saleInventory, SaleInventoryDTO.class);
        saleInventoryDTO.setItemId(userInventory.getItemId());
        saleInventoryDTO.setOwnerId(userInventory.getUserId());
        saleInventoryDTO.setItemName(userInventory.getItem().getName());
        return saleInventoryDTO;
    }
}
