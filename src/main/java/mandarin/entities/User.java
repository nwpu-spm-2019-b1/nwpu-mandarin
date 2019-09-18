package mandarin.entities;

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
    @Column(name="password_hash")
    private String passwordHash;
    private String type = "READER";
    @Column(name = "signup_time")
    private Instant signupTime = Instant.now();

    public User(){}

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getSignupTime() {
        return signupTime;
    }
}
