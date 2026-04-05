package com.loopers.domain.user;

import com.loopers.domain.point.Point;
import com.loopers.domain.point.PointHistory;
import com.loopers.domain.point.PointRepository;
import com.loopers.domain.point.attribute.PointHistoryType;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PointRepository pointRepository;

    @Transactional
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

        Point point = new Point(savedUser.getId(), 0L);
        Point savedPoint = pointRepository.save(point);

        PointHistory pointHistory = new PointHistory(savedPoint.getId(), savedUser.getId(), 0L, PointHistoryType.EARN, "회원가입");
        pointRepository.save(pointHistory);

        return UserResult.Join.from(savedUser);
    }

    @Transactional(readOnly = true)
    public Long getUserId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다."))
                .getId();
    }

    @Transactional(readOnly = true)
    public UserResult.GetUser getUser(String loginId) {
        return userRepository.findByLoginId(loginId)
                .map(UserResult.GetUser::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserResult.GetUser getUserById(Long userId) {
        return userRepository.findById(userId)
                .map(UserResult.GetUser::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND));
    }
}
