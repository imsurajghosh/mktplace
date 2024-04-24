package com.supaki.mktplace.controller;

import com.supaki.mktplace.entities.User;
import com.supaki.mktplace.models.UserDTO;
import com.supaki.mktplace.repositories.UserRespository;
import com.supaki.mktplace.utils.IDGenUtils;
import com.supaki.mktplace.utils.TransformationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class UserController {

    @Autowired
    UserRespository userRespository;

    @Autowired
    TransformationUtils transformationUtils;

    @GetMapping("/users")
    public List<UserDTO> index(){
        return userRespository.findAll().stream()
                .map(transformationUtils::convertEntityToDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/user")
    public UserDTO create(@RequestBody UserDTO userDTO) {
        User user = transformationUtils.convertDtoToEntity(userDTO);
        user.setUserId(IDGenUtils.userIdGenerate());
        User saved = userRespository.save(user);
        return transformationUtils.convertEntityToDTO(saved);
    }

    @GetMapping("/user/{userId}")
    public UserDTO create(@PathVariable("userId") String userId) {
        Optional<User> optionalUser = userRespository.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Found");
        }
        UserDTO userDTO = transformationUtils.convertEntityToDTO(optionalUser.get());
        return userDTO;
    }
}
