package distove.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @Column
    private String email;

    @Column
    private String profileImgUrl;

    @Column
    private boolean isDeleted = false;

    @Column
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Column
    private String password;

    @Column
    private String refreshToken;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Column
    private String nickname;


    public User(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
        this.updatedAt = LocalDateTime.now();
    }
}
