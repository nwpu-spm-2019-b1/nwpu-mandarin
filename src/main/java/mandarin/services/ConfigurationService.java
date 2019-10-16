package mandarin.services;

import mandarin.entities.ConfigurationItem;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigurationService {
    @Resource
    private EntityManagerFactory entityManagerFactory;
    private Map<String, String> defaults;

    public ConfigurationService() {
        defaults = new HashMap<>();
        defaults.put("fine_rate", "1");
        defaults.put("return_period", "30");
        defaults.put("reader_deposit", "300");
    }

    public String get(String key) {
        try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
            ConfigurationItem item = session.get(ConfigurationItem.class, key);
            if (item == null) {
                Object defaultValue = defaults.getOrDefault(key, null);
                if (defaultValue != null) {
                    return defaultValue.toString();
                } else {
                    return null;
                }
            }
            return item.getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Integer getAsInt(String key) {
        return Integer.parseInt(this.get(key));
    }

    public BigDecimal getAsBigDecimal(String key) {
        return new BigDecimal(this.get(key));
    }

    public String set(String key, Object value) {
        try (Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession()) {
            session.beginTransaction();
            ConfigurationItem item = session.get(ConfigurationItem.class, key);
            if (item == null) {
                session.save(new ConfigurationItem(key, value));
                session.getTransaction().commit();
                return null;
            } else {
                String oldValue = item.getValue();
                item.setValue(value.toString());
                session.update(item);
                session.getTransaction().commit();
                return oldValue;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
