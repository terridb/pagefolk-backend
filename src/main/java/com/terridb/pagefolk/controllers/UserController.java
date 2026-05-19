package com.terridb.pagefolk.controllers;

import com.terridb.pagefolk.dtos.common.PaginatedResponse;
import com.terridb.pagefolk.dtos.user.UserAdminDetailedResponseDto;
import com.terridb.pagefolk.dtos.user.UserAdminResponseDto;
import com.terridb.pagefolk.dtos.user.UserDetailedResponseDto;
import com.terridb.pagefolk.dtos.user.UserFilterDto;
import com.terridb.pagefolk.dtos.user.UserSimpleResponseDto;
import com.terridb.pagefolk.dtos.user.UserUpdateRequestDto;
import com.terridb.pagefolk.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDetailedResponseDto> getMe(Authentication authentication) {
        return ResponseEntity.ok(userService.findOrCreateUser(authentication));
    }

    @PatchMapping("/me/delete")
    public ResponseEntity<Void> softDeleteMe(Authentication authentication) {
        userService.softDeleteCurrentUser(authentication);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/{id}/delete")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> softDeleteUser(@PathVariable Long id) {
        userService.softDeleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/admin/{id}/revive")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<Void> reviveUser(@PathVariable Long id) {
        userService.reviveUserById(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDetailedResponseDto> updateMe(
            Authentication authentication,
            @Valid @RequestBody UserUpdateRequestDto input) {
        return ResponseEntity.ok(userService.updateUser(authentication, input));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDetailedResponseDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

@GetMapping
    public ResponseEntity<PaginatedResponse<UserSimpleResponseDto>> getAllUsers(UserFilterDto filter) {
        return ResponseEntity.ok(userService.getAllUsers(filter));
    }

    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<UserAdminDetailedResponseDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserByIdForAdmins(id));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<PaginatedResponse<UserAdminResponseDto>> getAllUsersForAdmins(UserFilterDto filter) {
        return ResponseEntity.ok(userService.getAllUsersForAdmins(
                filter.getSearch(),
                filter.getDeleted(),
                filter.getPage(),
                filter.getSize(),
                filter.getSortBy(),
                filter.getDirection()
        ));
    }
}
