package com.supaki.mktplace.controller;

import com.supaki.mktplace.entities.Item;
import com.supaki.mktplace.models.ItemDTO;
import com.supaki.mktplace.repositories.ItemRepository;
import com.supaki.mktplace.utils.IDGenUtils;
import com.supaki.mktplace.utils.TransformationUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class ItemController {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TransformationUtils transformationUtils;

    @GetMapping("/items")
    public List<ItemDTO> index(){
        return itemRepository.findAll().stream()
                .map(transformationUtils::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/item")
    public ItemDTO create(@RequestBody ItemDTO itemDTO) {
        Item item = transformationUtils.convertDtoToEntity(itemDTO);
        item.setItemId(IDGenUtils.itemIdGenerate());
        Item save = itemRepository.save(item);
        return transformationUtils.convertEntityToDTO(save);
    }

    @GetMapping("/item/{itemId}")
    public ItemDTO create(@PathVariable("itemId") String itemId) {
        Optional<Item> optionalItem = itemRepository.findByItemId(itemId);
        if (optionalItem.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item Not Found");
        }
        return transformationUtils.convertEntityToDTO(optionalItem.get());
    }
}
