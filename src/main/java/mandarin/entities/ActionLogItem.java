package mandarin.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import mandarin.types.JSONBType;
import mandarin.utils.FormatUtils;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;
import java.io.IOException;
import java.time.Instant;
import java.util.Map;

@Entity
@Table(name = "action_log")
@TypeDefs({@TypeDef(name = "JSONBType", typeClass = JSONBType.class)})
public class ActionLogItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String type;

    @Type(type = "JSONBType")
    private Map<String, Object> info;

    private static class TimeSerializer extends JsonSerializer<Instant> {

        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(FormatUtils.formatInstant(value).orElse("-"));
        }
    }

    @JsonSerialize(using = TimeSerializer.class)
    private Instant time = Instant.now();

    public ActionLogItem() {
    }

    public ActionLogItem(User user, String type, Map<String, Object> info) {
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

    public Map<String, Object> getInfo() {
        return info;
    }

    public Instant getTime() {
        return time;
    }

}
