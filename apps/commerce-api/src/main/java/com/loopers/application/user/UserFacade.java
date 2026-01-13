package com.loopers.application.user;

import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserResult;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;

    public UserOutput.Join join(UserInput.Join input) {
        UserCommand.Join command = input.toCommand();
        UserResult.Join result = userService.join(command);
        return UserOutput.Join.from(result);
    }

    public UserOutput.GetUser getUser(String loginId) {
        UserResult.GetUser result = userService.getUser(loginId);
        return UserOutput.GetUser.from(result);
    }

}
