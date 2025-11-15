package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository repository;

    @Transactional
    public UserResult.Join join(UserCommand.Join command) {
        repository.findByLoginId(command.loginId()).ifPresent(user -> {
            throw new CoreException(ErrorType.CONFLICT, "이미 존재하는 ID입니다.");
        });

        User user = User.builder()
                .loginId(command.loginId())
                .gender(command.gender())
                .birthDate(command.birthDate())
                .email(command.email())
                .build();

        User savedUser = repository.save(user);
        return UserResult.Join.from(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResult.GetUser getUser(Long id) {
        return repository.findById(id)
                .map(UserResult.GetUser::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
    }

    @Transactional(readOnly = true)
    public UserResult.GetUser getUserByLoginId(String loginId) {
        return repository.findByLoginId(loginId)
                .map(UserResult.GetUser::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 사용자입니다."));
    }

}
