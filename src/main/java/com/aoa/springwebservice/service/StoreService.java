package com.aoa.springwebservice.service;

import com.aoa.springwebservice.domain.Store;
import com.aoa.springwebservice.domain.StoreRepository;
import com.aoa.springwebservice.domain.User;
import com.aoa.springwebservice.dto.StoreInputDTO;
import com.aoa.springwebservice.dto.StoreOutputDTO;
import com.aoa.springwebservice.property.MayakURLProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@Service
public class StoreService {

    @Autowired
    StoreRepository storeRepository;

    @Autowired
    FileStorageService fileStorageService;

    //private static final String MAYAK_URL = MayakURLProperties;
    private String mayakUrl;

    @Autowired
    public StoreService(MayakURLProperties mayakURLProperties) {
        this.mayakUrl = mayakURLProperties.getUrl();
    }

    public Store createStore(StoreInputDTO storeDTO, User user){
        return storeRepository.save(storeDTO.toDomain(saveStoreImg(storeDTO), user));
    }
    public String saveStoreImg(StoreInputDTO storeDTO) {
        return fileStorageService.storeFile(storeDTO.getImageFile());
    }

    public boolean hasStore(User user) {
        return storeRepository.findByUserId(user.getId()).isPresent();
    }

    public Store getStoreByUser(User user){
        return storeRepository.findByUser(user).orElseThrow(() -> new EntityExistsException("가게 등록 부터 해주세요"));
    }
  
    public StoreOutputDTO createStoreDetailInfoDTO(Store store){
        return StoreOutputDTO.createStoreDetailInfoDTO(store);
    }

    public Store getStoreById(long id) {
        return storeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("주문할 가게가 등록되지 않았습니다"));
    }

    public String makeOwnerUrl(User loginUser) {
        return mayakUrl + "/stores/" + getStoreByUser(loginUser).getId() + "/orders/form";

    }
}
