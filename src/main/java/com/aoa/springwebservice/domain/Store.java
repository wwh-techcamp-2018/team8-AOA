package com.aoa.springwebservice.domain;

import com.aoa.springwebservice.domain.support.MenuOutputDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter @NoArgsConstructor
@Entity
@ToString
public class Store{

    private static final boolean OPEN = true;
    private static final boolean CLOSE = false;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //todo length 등 다른 조건들
    @Column(nullable = false, length = 40)
    private String storeName;

    @Column(nullable = false, length = 60)
    private String serviceDescription;

    @Column(nullable = false, length = 10)
    private String ownerName;

    @Column(nullable = true, length = 300)
    private String imgURL;

    @Column(nullable = false, length = 5)
    private String postCode;

    @Column(nullable = false, length = 100)
    private String address;

    @Column(nullable = true, length = 40)
    private String addressDetail;

    @Column(nullable = false, length = 11)
    private String phoneNumber;

    @Column(nullable = true, length = 600)
    private String description;

    @OneToOne
    private User user;

    // Todo 제약사항 추가
    private LocalDateTime timeToClose;

    // Todo Cascade issue 다른 옵션도 적용해야 할 수도 있음
    @OneToMany(mappedBy = "store", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Where(clause = "deleted = false")
    private List<Menu> menus = new ArrayList<>();

    // todo (현재 시각이랑 timeToClose 랑 비교) +(currentReservations 갯수?)해서 오픈상태 동기화 어떻게 해줄지
    @Transient
    private boolean isOpen;

    @Builder
    public Store(String storeName, String serviceDescription, String ownerName, String imgURL, String postCode, String address, String addressDetail, String phoneNumber, String description, LocalDateTime timeToClose, boolean isOpen) {
        this.storeName = storeName;
        this.serviceDescription = serviceDescription;
        this.ownerName = ownerName;
        this.imgURL = imgURL;
        this.postCode = postCode;
        this.address = address;
        this.addressDetail = addressDetail;
        this.phoneNumber = phoneNumber;
        this.description = description;
        this.timeToClose = timeToClose;
        this.isOpen = isOpen;
    }


    public boolean addMenu(Menu menu) {
        if(menu != null && menu.isEqualStore(this) && !hasMenu(menu)) {
            menus.add(menu);
            return true;
        }
        return false;
    }

    public boolean hasMenu(Menu menu) {
        return menus.contains(menu);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        //todo User 추가되면 Store의 unique 제약조건 다시 생각
        Store store = (Store) o;
        return Objects.equals(storeName, store.storeName) &&
                Objects.equals(user, store.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, user);
    }

    public void inactivateMenus(){
//        menus.stream()
//                .filter(menu -> menu.isUsed())
//                .forEach(menu -> menu.notUsed());
    }
    public boolean updateReservation(List<Reservation> reservations, LocalDateTime timeToClose) {
//        if(isOpen()) return false; //&& !validateReservations(reservations)) return false;
//
//        this.timeToClose = timeToClose;
//        inactivateMenus();
//        reservations.forEach(reservation -> reservation.changeToBeActivated());
//        // todo Cascade 이슈 생길 수도 있음, JPA 붙이고 확인 필요
//        currentReservations = reservations;
        return true;
    }

    public boolean isOpen(){
//        if(isOpen)
//            updateOpenStatus();
        return isOpen;
    }

    @PostLoad
    public void deactivate() {
        //Todo if(timeToClose.isAfter(LocalDateTime.now()))
        isOpen = CLOSE;
    }

    public void activate(LocalDateTime timeToClose){
        menus.stream().forEach(menu -> menu.dropLastUsedStatus());
        this.timeToClose = timeToClose;
        isOpen = OPEN;
    }

    public List<MenuOutputDTO> getMenuOutputDTOList() {
        List<MenuOutputDTO> menuDTOs = new ArrayList<>();
        this.menus.stream().forEach(e -> menuDTOs.add(MenuOutputDTO.createMenuOutputDTO(e)));
        return menuDTOs;
    }
}
