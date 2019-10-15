package mandarin.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "configuration")
public class ConfigurationItem {
    @Id
    private String key;
    private String value;

    public ConfigurationItem(String key, Object value) {
        this.key = key;
        this.value = value.toString();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
