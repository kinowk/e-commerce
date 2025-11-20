package com.loopers.application.user;

import com.loopers.domain.user.UserResult;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public UserOutput.Join join(UserInput.Join input) {
        UserResult.Join result = userService.join(input.toCommand());
        return UserOutput.Join.from(result);
    }

    public UserOutput.GetUser getUser(Long id) {
        UserResult.GetUser result = userService.getUser(id);
        return UserOutput.GetUser.from(result);
    }

    public UserOutput.GetUser getUserByLoginId(String loginId) {
        UserResult.GetUser result = userService.getUserByLoginId(loginId);
        return UserOutput.GetUser.from(result);
    }
}
