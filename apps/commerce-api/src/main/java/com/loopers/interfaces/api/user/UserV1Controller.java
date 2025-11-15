package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserFacade;
import com.loopers.application.user.UserInput;
import com.loopers.application.user.UserOutput;
import com.loopers.interfaces.api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserV1Controller implements UserV1ApiSpec {

    private final UserFacade userFacade;

    @PostMapping
    @Override
    public ApiResponse<UserResponse.Join> join(@RequestBody UserRequest.Join request) {
        UserInput.Join input = request.toInput();
        UserOutput.Join output = userFacade.join(input);
        UserResponse.Join response = UserResponse.Join.from(output);
        return ApiResponse.success(response);
    }

    @GetMapping("/me")
    @Override
    public ApiResponse<UserResponse.GetUser> getUser(@RequestHeader("X-USER-ID") Long userId) {
        UserOutput.GetUser output = userFacade.getUser(userId);
        UserResponse.GetUser response = UserResponse.GetUser.from(output);
        return ApiResponse.success(response);
    }

}
