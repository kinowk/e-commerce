package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.regex.Pattern;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "login_id", nullable = false)
    private String loginId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private String birthDate;

    @Column(nullable = false)
    private String email;


    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    private static final Pattern LOGIN_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]{1,10}$");

    @Builder
    public User(String loginId, Gender gender, String birthDate, String email) {
        validateLoginId(loginId);
        validateGender(gender);
        validateBirthDate(birthDate);
        validateEmail(email);

        this.loginId = loginId;
        this.gender = gender;
        this.email = email;
        this.birthDate = birthDate;
    }

    private void validateLoginId(String loginId) {
        if (!StringUtils.hasText(loginId) || !LOGIN_ID_PATTERN.matcher(loginId).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "ID 형식이 올바르지 않습니다. ID는 영문 및 숫자 10자 이내로 입력하시기 바랍니다.");
        }
    }

    private void validateGender(Gender gender) {
        if (gender == null)
            throw new CoreException(ErrorType.BAD_REQUEST, "성별이 올바르지 않습니다.");
    }

    private void validateBirthDate(String birthDate) {
        if (!StringUtils.hasText(birthDate))
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일 형식이 올바르지 않습니다.");

        try {
            LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT));
        } catch (DateTimeParseException e) {
            throw new CoreException(ErrorType.BAD_REQUEST, "생년월일은 'yyyy-MM-dd' 형식이어야 합니다.");
        }
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email) || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "이메일 형식이 올바르지 않습니다.");
        }
    }

}
