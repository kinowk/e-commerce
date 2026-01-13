package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserResult.Join join(UserCommand.Join command) {
        userRepository.findByLoginId(command.loginId())
                .ifPresent(user -> {
                    throw new CoreException(ErrorType.CONFLICT);
                });

        User user = new User(
                command.username(),
                command.loginId(),
                command.password(),
                command.email(),
                command.birthDate(),
                command.gender()
        );
        User savedUser = userRepository.save(user);
        return UserResult.Join.from(savedUser);
    }

    public UserResult.GetUser getUser(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(UserResult.GetUser::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }
}
