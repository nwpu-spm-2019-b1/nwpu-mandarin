package mandarin.entities;

import mandarin.auth.UserType;
import mandarin.utils.CryptoUtils;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    @Column(name = "password_hash")
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private UserType type = UserType.Reader;
    @Column(name = "signup_time")
    private Instant signupTime = Instant.now();

    public User() {
    }

    public User(String username, String password, UserType type) {
        this.username = username;
        this.passwordHash = CryptoUtils.hashPassword(password);
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = CryptoUtils.hashPassword(password);
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public Instant getSignupTime() {
        return signupTime;
    }
}
