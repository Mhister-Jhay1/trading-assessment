package org.trading.system.userManagement.service;

import org.trading.system.common.dto.response.ApiResponse;
import org.trading.system.userManagement.dto.request.CreateUserRequest;
import org.trading.system.userManagement.model.User;

import java.util.List;

public interface UserManagementService {

    ApiResponse<User> createUser(CreateUserRequest request);

    ApiResponse<User> getUserById(String userId);

    ApiResponse<List<User>> getAllUsers();
}
