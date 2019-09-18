package mandarin.entities;

import mandarin.types.JSONBType;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "action_log")
@TypeDefs({@TypeDef(name = "JSONBType",typeClass = JSONBType.class)})
public class ActionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String type;

    @Type(type = "JSONBType")
    private Map<String,Object> info;

    private Instant time = Instant.now();

    public ActionLog() {
    }

    public ActionLog(User user, String type, Map<String,Object> info) {
        this.user = user;
        this.type = type;
        this.info = info;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String,Object> getInfo() {
        return info;
    }

    public Instant getTime() {
        return time;
    }

}
