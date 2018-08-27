package com.aoa.springwebservice.web;

import com.aoa.springwebservice.domain.Reservation;
import com.aoa.springwebservice.domain.Store;
import com.aoa.springwebservice.domain.StoreRepository;
import com.aoa.springwebservice.domain.User;
import com.aoa.springwebservice.security.HttpSessionUtils;
import com.aoa.springwebservice.security.LoginUser;
import com.aoa.springwebservice.service.OrderService;
import com.aoa.springwebservice.service.ReservationService;
import com.aoa.springwebservice.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.EntityExistsException;
import javax.servlet.http.HttpSession;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@Slf4j
public class PageController {

    @Autowired
    StoreService storeService;

    @Autowired
    ReservationService reservationService;

    @Autowired
    OrderService orderService;

    @GetMapping("/admin")
    public String show(@LoginUser User loginUser) {
        if (storeService.hasStore(loginUser))
            return "redirect:/result/success";
        return "/admin/store/fail";
    }

    @GetMapping("/owner/stores/form")
    public String registStore(@LoginUser User loginUser, Model model) {
        if(storeService.hasStore(loginUser)) {
            return "/alreadyRegisted";
        }
        model.addAttribute("navTitle", "가게정보 등록");
        return "/registStore";
    }

    @GetMapping("/owner/menus/form")
    public String registMenu(@LoginUser User loginUser, Model model) {
        if(!storeService.hasStore(loginUser)) {
            return "/fail";
        }
        model.addAttribute("navTitle", "메뉴 등록");
        return "/registMenu";
    }

    @GetMapping("/owner/reservations/form")
    public String openReservation(Model model, @LoginUser User loginUser) {
        //todo store 존재 확인, store isOpen 확인 --> 중복 로직 처리 어떻게?
        log.debug("store check {} ", storeService.getStoreByUser(loginUser));

        log.debug("dto check {}",  storeService.createStoreDetailInfoDTO(storeService.getStoreByUser(loginUser)));
        model.addAttribute("store", storeService.createStoreDetailInfoDTO(storeService.getStoreByUser(loginUser)));
        model.addAttribute("navTitle", "예약 등록");

        return "/openReservation";
    }

    @GetMapping(path = "/owner/reservations", params = "condition")
    public String showReservations(@RequestParam final String condition, @LoginUser User loginUser, Model model) {
        List<Reservation> reservations = reservationService.getReservationsByCondition(condition, storeService.getStoreByUser(loginUser));
        model.addAttribute("reservations", reservations);
        model.addAttribute("navTitle", "예약 조회");
        return "/displayReservation";
    }

//    @GetMapping("/stores/{storeId}/orders/form")
//    public String createOrder(@PathVariable long storeId, @LoginUser User loginUser, Model model){
//        //todo store 존재 확인, store isOpen 확인
//        model.addAttribute("store", storeService.createStoreDetailInfoDTO(storeService.getStoreByUser(loginUser)));
//        LocalTime now = LocalTime.now();
//        model.addAttribute("defaultTime", LocalTime.of(now.getHour(), ((now.getMinute()/ 30)) * 30).plusMinutes(30));
//        return "/createOrder";
//    }

    @Autowired
    StoreRepository storeRepository;
    @GetMapping("/stores/{storeId}/orders/form")
    public String createOrder(@PathVariable long storeId, Model model){
        //todo store 존재 확인, store isOpen 확인
        model.addAttribute("store", storeService.createStoreDetailInfoDTO(storeRepository.findById(storeId).get()));
        LocalTime now = LocalTime.now();
        model.addAttribute("defaultTime", LocalTime.of(now.getHour(), ((now.getMinute()/ 30)) * 30).plusMinutes(30));
        return "/createOrder";
    }

    @GetMapping("/orders/{orderId}/result")
    public String showOrderResult(@PathVariable long orderId, @LoginUser User user, Model model){

        model.addAttribute("order", "?"); //OUTPUT
        return "/showOrderResult";
    }

    @GetMapping("/owner/menus")
    public String showMenus(@LoginUser User user,  Model model) {
        model.addAttribute("store", storeService.getStoreByUser(user));
        model.addAttribute("navTitle", "메뉴 조회");
        return "displayMenu";
    }
}
