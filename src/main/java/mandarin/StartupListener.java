package mandarin;

import mandarin.entities.Book;
import mandarin.entities.Category;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Component
public class StartupListener {
    private static boolean firstTime = true;
    private DataSource dataSource;

    @Resource
    EntityManagerFactory entityManagerFactory;

    public StartupListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void addEntities() {
        if (firstTime) {
            firstTime = false;
            SessionFactory sessionFactory = entityManagerFactory.unwrap(SessionFactory.class);
            Session session = sessionFactory.openSession();
            Transaction tx = session.beginTransaction();
            Category c1 = new Category("Programming", null);
            session.save(c1);
            session.flush();
            Category c2 = new Category("Algorithms", c1);
            session.save(c2);
            Category c3 = new Category("Cryptography", c1);
            session.save(c3);
            session.save(new Book("9781617294945", "Spring in Action", "Craig Walls", "Shelf 1", BigDecimal.ONE, Collections.singletonList(c1)));
            session.save(new Book("9780262033848", "Introduction to Algorithms", "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein", "Shelf 1", BigDecimal.ONE, Arrays.asList(c1, c2)));
            session.save(new Book("9781119183471", "Applied Cryptography: Protocols, Algorithms, and Source Code in C", "Bruce Schneier", "Shelf 1", BigDecimal.ONE, Arrays.asList(c1, c2, c3)));
            session.flush();
            tx.commit();
            session.close();
        }
    }

    public void run() {
        Connection conn = null;
        try {
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            if (!Boolean.parseBoolean(props.getProperty("mandarin.exec-init-sql", "false"))) {
                return;
            }
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);
            conn.nativeSQL("BEGIN;");
            StringWriter writer = new StringWriter();
            IOUtils.copy(StartupListener.class.getClassLoader().getResourceAsStream("data.sql"), writer, StandardCharsets.UTF_8);
            Statement stmt = conn.createStatement();
            stmt.execute(writer.toString());
            conn.commit();
        } catch (IOException | SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            throw new RuntimeException(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}