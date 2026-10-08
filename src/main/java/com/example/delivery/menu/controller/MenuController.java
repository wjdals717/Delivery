package com.example.delivery.menu.controller;

import com.example.delivery.global.exception.ForbiddenException;
import com.example.delivery.global.exception.NotFoundException;
import com.example.delivery.global.exception.RestApiException;
import com.example.delivery.global.security.UserDetailsImpl;
import com.example.delivery.menu.dto.request.MenuRequest;
import com.example.delivery.menu.dto.response.MenuResponse;
import com.example.delivery.menu.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor

@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    // 메뉴 등록
    @PostMapping
    public ResponseEntity<?> createMenu(@Valid @RequestBody MenuRequest requestDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenu(requestDto, userDetails.getUser()));   //201
    }

    // 메뉴 목록 조회(복수)
    @GetMapping
    public ResponseEntity<List<MenuResponse>> getMenus() {
        return ResponseEntity.status(HttpStatus.OK).body(menuService.getMenus());        //200
    }

    // 메뉴 단건 조회(단수)
    @GetMapping("/{menuId}")
    public ResponseEntity<?> getMenu(@PathVariable Long menuId) {
        try {
            return new ResponseEntity<>(menuService.getMenu(menuId), HttpStatus.OK);     //200
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value()), //404
                    HttpStatus.NOT_FOUND);
        }
    }

    // 메뉴 수정
    @PutMapping("/{menuId}")
    public ResponseEntity<?> updateMenu(@PathVariable Long menuId, @Valid @RequestBody MenuRequest requestDto,
                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            MenuResponse response = menuService.updateMenu(menuId, requestDto, userDetails.getUser());
            return new ResponseEntity<>(response, HttpStatus.OK);   //200
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value()),    //404
                    HttpStatus.NOT_FOUND);
        } catch (ForbiddenException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.FORBIDDEN.value()),    //403
                    HttpStatus.FORBIDDEN);
        }
    }

    // 메뉴 삭제
    @DeleteMapping("/{menuId}")
    public ResponseEntity<?> deleteMenu(@PathVariable Long menuId,
                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        try {
            menuService.deleteMenu(menuId, userDetails.getUser());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); //204
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.NOT_FOUND.value()),
                    HttpStatus.NOT_FOUND);  //404
        } catch (ForbiddenException ex) {
            return new ResponseEntity<>(
                    new RestApiException(ex.getMessage(), HttpStatus.FORBIDDEN.value()),
                    HttpStatus.FORBIDDEN);  //403
        }
    }
}