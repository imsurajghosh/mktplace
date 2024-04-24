//package com.supaki.mktplace.controller;
//
//import com.supaki.mktplace.entities.AccountDetail;
//import com.supaki.mktplace.models.AccountDetailDTO;
//import com.supaki.mktplace.repositories.AccountDetailRepository;
//import com.supaki.mktplace.utils.IDGenUtils;
//import com.supaki.mktplace.utils.TransformationUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//public class AccountController {
//
//    @Autowired
//    AccountDetailRepository accountDetailRepository;
//
//    @Autowired
//    TransformationUtils transformationUtils;
//
//    @GetMapping("/user/{userId}/accounts")
//    public List<AccountDetailDTO> index(@PathVariable("userId") String userId){
//        return accountDetailRepository.findByUserId(userId).stream()
//                .map(transformationUtils::convertEntityToDTO)
//                .collect(Collectors.toList());
//    }
//
//    @PostMapping("/user/{userId}/account")
//    public AccountDetailDTO create(@RequestBody AccountDetailDTO accountDetailDTO) {
//        AccountDetail accountDetail = transformationUtils.convertDtoToEntity(accountDetailDTO);
//        accountDetail.setAccountId(IDGenUtils.accountIdGenerate());
//        AccountDetail save = accountDetailRepository.save(accountDetail);
//        return transformationUtils.convertEntityToDTO(save);
//    }
//
//    @GetMapping("/user/{userId}/account/{accountId}")
//    public AccountDetailDTO get(@PathVariable("accountId") String accountId,
//                                @PathVariable("userId") String userId) {
//        Optional<AccountDetail> optionalAccountDetail = accountDetailRepository.findByUserIdAndAccountId(userId, accountId);
//        if (optionalAccountDetail.isEmpty()) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account Not Found");
//        }
//        return transformationUtils.convertEntityToDTO(optionalAccountDetail.get());
//    }
//}
