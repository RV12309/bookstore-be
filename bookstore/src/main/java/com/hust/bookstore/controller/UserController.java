package com.hust.bookstore.controller;

import com.hust.bookstore.dto.PageDto;
import com.hust.bookstore.dto.request.SearchUserRequest;
import com.hust.bookstore.dto.request.UpdateUserRequest;
import com.hust.bookstore.dto.response.BaseResponse;
import com.hust.bookstore.dto.response.UserResponse;
import com.hust.bookstore.dto.response.UserStatisticResponse;
import com.hust.bookstore.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.hust.bookstore.enumration.ResponseCode.SUCCESS;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Cập nhật thông tin người dùng")
    @PutMapping
    ResponseEntity<BaseResponse<Object>> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        userService.updateUser(request);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Danh sách người dùng")
    @PostMapping
    ResponseEntity<BaseResponse<PageDto<UserResponse>>> searchUsers(@Valid @RequestBody  SearchUserRequest request) {
        PageDto<UserResponse> users = userService.searchUsers(request);
        return ResponseEntity.ok(BaseResponse.<PageDto<UserResponse>>builder()
                .code(SUCCESS.code())
                .message(SUCCESS.message()).data(users).build());
    }

    @Operation(summary = "Xóa người dùng")
    @DeleteMapping("/{id}")
    ResponseEntity<BaseResponse<Object>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Khoá người dùng")
    @PutMapping("/{id}/lock")
    ResponseEntity<BaseResponse<Object>> lockUser(@PathVariable Long id) {
        userService.lockUser(id);
        return ResponseEntity.ok(BaseResponse.builder().code(SUCCESS.code()).message(SUCCESS.message()).build());
    }

    @Operation(summary = "Mở khoá người dùng")
    @PutMapping("/{id}/unlock")
    ResponseEntity<BaseResponse<Object>> unlockUser(@PathVariable Long id) {
        userService.unlockUser(id);
        return ResponseEntity.ok(BaseResponse.builder()
                .code(SUCCESS.code())
                .message(SUCCESS.message()).build());
    }

    @Operation(summary = "Xem thông tin người dùng")
    @GetMapping("/{id}")
    ResponseEntity<BaseResponse<UserResponse>> getUser(@PathVariable Long id) {
        UserResponse user = userService.getUser(id);
        return ResponseEntity.ok(BaseResponse.<UserResponse>builder()
                .code(SUCCESS.code())
                .message(SUCCESS.message())
                .data(user).build());
    }

    @Operation(summary = "Thống kê người dùng sử dụng hệ thống sellers/customers")
    @GetMapping("/statistic")
    ResponseEntity<BaseResponse<UserStatisticResponse>> statisticUser() {
        UserStatisticResponse userStatisticResponse = userService.statisticUser();
        return ResponseEntity.ok(BaseResponse.<UserStatisticResponse>builder()
                .code(SUCCESS.code())
                .message(SUCCESS.message())
                .data(userStatisticResponse).build());
    }

}
