package mandarin;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
public class StartupListener {
    private boolean firstTime = true;
    @Resource
    EntityManagerFactory entityManagerFactory;

    @EventListener(ContextRefreshedEvent.class)
    public void contextRefreshedEvent() {
        if (firstTime) {
            SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);

            StringWriter writer = new StringWriter();
            try {
                Session session = sessionFactory.openSession();
                session.beginTransaction();
                IOUtils.copy(StartupListener.class.getClassLoader().getResourceAsStream("data.sql"), writer, StandardCharsets.UTF_8);
                session.createSQLQuery(writer.toString()).executeUpdate();
                session.getTransaction().commit();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            firstTime = false;
        }
    }
}