package com.example.delivery.menu.service;

import com.example.delivery.global.exception.ForbiddenException;
import com.example.delivery.global.exception.NotFoundException;
import com.example.delivery.menu.dto.request.MenuRequest;
import com.example.delivery.menu.dto.response.MenuResponse;
import com.example.delivery.menu.entity.Menu;
import com.example.delivery.menu.repository.MenuRepository;
import com.example.delivery.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;

    @Transactional
    public MenuResponse createMenu(MenuRequest req, User owner) {
        Menu menu = menuRepository.save(new Menu(req.getName(), req.getPrice(), req.getDescription(), owner));
        return new MenuResponse(menu);
    }

    public List<MenuResponse> getMenus() {
        return menuRepository.findAllByDeletedFalse().stream().map(MenuResponse::new).toList();
    }

    public MenuResponse getMenu(Long menuId) {
        return new MenuResponse(findMenu(menuId));
    }

    @Transactional
    public MenuResponse updateMenu(Long menuId, MenuRequest req, User user) {
        Menu menu = findMenu(menuId);           // 메뉴 확인 404
        checkOwner(menu, user);                 // 다른 사장님의 메뉴면 403
        menu.setName(req.getName());            // 더티 체킹
        menu.setPrice(req.getPrice());
        menu.setDescription(req.getDescription());
        return new MenuResponse(menu);
    }

    @Transactional
    public void deleteMenu(Long menuId, User user) {
        Menu menu = findMenu(menuId);
        checkOwner(menu, user);
        menu.setDeleted(true);                  // repository.delete() 쓰지 않기
    }

    private Menu findMenu(Long menuId) {
        return menuRepository.findByIdAndDeletedFalse(menuId)
                .orElseThrow(() -> new NotFoundException("메뉴가 존재하지 않습니다."));
    }

    private void checkOwner(Menu menu, User user) {
        if (!menu.getOwner().getId().equals(user.getId())) {
            throw new ForbiddenException("본인 메뉴만 수정·삭제할 수 있습니다.");  //403
        }
    }
}