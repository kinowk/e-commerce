package com.loopers.application.user;

import com.loopers.domain.user.UserResult;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
        return userService.findUser(id)
                .map(UserOutput.GetUser::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당 ID의 사용자를 찾을 수 없습니다."));
    }

    public UserOutput.GetUser getUserByLoginId(String loginId) {
        return userService.findUserByLoginId(loginId)
                .map(UserOutput.GetUser::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "해당 ID의 사용자를 찾을 수 없습니다."));
    }
}
