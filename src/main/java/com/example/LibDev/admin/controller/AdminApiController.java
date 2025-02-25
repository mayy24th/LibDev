package com.example.LibDev.admin.controller;

import com.example.LibDev.admin.dto.UpdateRoleDto;
import com.example.LibDev.admin.service.AdminService;
import com.example.LibDev.global.dto.GlobalResponseDto;
import com.example.LibDev.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminApiController {

    private final UserService userService;
    private final AdminService adminService;

    @GetMapping("/api/admin/v1/admins")
    public ResponseEntity<GlobalResponseDto> getAdmins() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, userService.info()));
    }

    @GetMapping("/api/admin/v1/admins/user-list")
    public ResponseEntity<GlobalResponseDto> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam(value = "email", required = false) String email) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK, adminService.findAllUsers(page, role, email)));
    }


    @PatchMapping("/api/admin/v1/admins/change-role")
    public ResponseEntity<GlobalResponseDto> changeRole(@RequestBody UpdateRoleDto updateRoleDto) {
        adminService.changeRole(updateRoleDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(GlobalResponseDto.success(HttpStatus.OK,"권한 변경 성공"));
    }
}
