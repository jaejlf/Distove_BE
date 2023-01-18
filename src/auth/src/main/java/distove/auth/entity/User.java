package distove.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String image_url;

    @Column
    private boolean is_deleted;

    @Column
    private LocalDateTime updated_at;

    @Column
    private LocalDateTime created_at;

    @Column
    private String password;

    @Column
    private String refreshToken;

    @Column
    private String nickname;


    public User(String email, String image_url, boolean is_deleted, LocalDateTime updated_at, LocalDateTime created_at, String password, String refreshToken, String nickname) {

        this.email = email;
        this.image_url = image_url;
        this.is_deleted = is_deleted;
        this.updated_at = updated_at;
        this.created_at = created_at;
        this.password = password;
        this.refreshToken = refreshToken;
        this.nickname = nickname;
    }

}
