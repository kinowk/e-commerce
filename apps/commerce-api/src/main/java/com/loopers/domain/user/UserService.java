package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public Optional<UserResult.GetUser> findUserByLoginId(String loginId) {
        return repository.findByLoginId(loginId)
                .map(UserResult.GetUser::from);
    }

    @Transactional(readOnly = true)
    public Optional<UserResult.GetUser> findUser(Long id) {
        return repository.findById(id)
                .map(UserResult.GetUser::from);
    }
}
