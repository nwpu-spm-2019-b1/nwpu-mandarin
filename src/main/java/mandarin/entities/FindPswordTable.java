package mandarin.entities;

import javax.persistence.*;
import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "findPswordTables")
public class FindPswordTable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "start_time", nullable = false)
    private Instant startTime = Instant.now();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String description;

    public FindPswordTable() {
    }

    public FindPswordTable(User user, String descrision) {
        this.user = user;
        this.description = descrision;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public User getUser() {
        return user;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descrision) {
        this.description = descrision;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
