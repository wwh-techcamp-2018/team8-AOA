package com.aoa.springwebservice.web;

import com.aoa.springwebservice.domain.Menu;
import com.aoa.springwebservice.domain.MenuRepository;
import com.aoa.springwebservice.domain.User;
import com.aoa.springwebservice.domain.support.MenuDTO;
import com.aoa.springwebservice.domain.support.MenuDTOToUpload;
import com.aoa.springwebservice.domain.support.MenuOutputDTO;
import com.aoa.springwebservice.security.LoginUser;
import com.aoa.springwebservice.service.FileStorageService;
import com.aoa.springwebservice.service.MenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiMenuController {

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private MenuService menuService;

    @PostMapping(path = "/owner/menus")
    public String createMenu(MenuDTOToUpload menuDTO, @LoginUser User user) {
        log.debug("menuDTO : {}", menuDTO);
        log.debug("file : {}", menuDTO.getFile());
        String menuImgUrl = fileStorageService.storeFile(menuDTO.getFile());
        menuDTO.setImageUrl(menuImgUrl);
        menuService.createMenu(menuDTO, user);
        return "/result/success";
    }

    @GetMapping(path = "/owner/menus")
    public List<MenuOutputDTO> getAllMenu(@LoginUser User user) {
        return menuService.findAllMenuInStore(user);
    }

    @DeleteMapping(path = "/owner/menus/{menuId}")
    public ResponseEntity<Menu> deleteMenu(@PathVariable long menuId){
        Menu menu = menuService.deleteMenu(menuId);
        return new ResponseEntity<Menu>(menu, new HttpHeaders(), HttpStatus.OK);
    }

    @GetMapping(path = "/test/user")
    public User getUser(@LoginUser User loginUser) {
        return loginUser;
    }
}