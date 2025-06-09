package org.trading.system.userManagement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.userManagement.dto.request.CreateUserRequest;
import org.trading.system.userManagement.model.User;
import org.trading.system.userManagement.service.UserManagementService;

import java.util.List;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping()
    public ApiResponse<User> createUser(@Valid @RequestBody
                                            CreateUserRequest request){
        return userManagementService.createUser(request);
    }

    @GetMapping("{userId}")
    public ApiResponse<User> getUserById(@PathVariable String userId){
        return userManagementService.getUserById(userId);
    }

    @GetMapping()
    public ApiResponse<List<User>> getAllUsers(){
        return userManagementService.getAllUsers();
    }
}
