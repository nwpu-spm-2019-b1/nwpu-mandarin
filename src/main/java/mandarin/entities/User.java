package mandarin.entities;

import mandarin.utils.CryptoUtils;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String passwordHash;
    private String type = "READER";
    private Instant signupTime = Instant.now();

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
