package mandarin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import mandarin.auth.UserType;
import mandarin.entities.*;
import mandarin.services.ConfigurationService;
import mandarin.utils.PollingThread;
import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StartupListener {
    private static boolean firstTime = true;
    private DataSource dataSource;

    @Resource
    private EntityManagerFactory entityManagerFactory;

    @Resource
    private ConfigurationService configurationService;

    @Resource
    private PollingThread pollingThread;

    public StartupListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void addEntities() throws IOException {
        if (firstTime) {
            firstTime = false;
            Session session = entityManagerFactory.unwrap(SessionFactory.class).openSession();
            Transaction tx = session.beginTransaction();
            /*
            session.save(new Book("9781617294945", "Spring in Action", "Craig Walls", "Shelf 1", BigDecimal.ONE, Collections.singletonList(c1)));
            session.save(new Book("9780262033848", "Introduction to Algorithms", "Thomas H. Cormen, Charles E. Leiserson, Ronald L. Rivest, Clifford Stein", "Shelf 1", BigDecimal.ONE, Arrays.asList(c1, c2)));
            session.save(new Book("9781119183471", "Applied Cryptography: Protocols, Algorithms, and Source Code in C", "Bruce Schneier", "Shelf 1", BigDecimal.ONE, Arrays.asList(c1, c2, c3)));
            */
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            Type mapType = new TypeToken<List<Map<String, Object>>>() {
            }.getType();
            StringWriter writer = new StringWriter();
            IOUtils.copy(this.getClass().getClassLoader().getResourceAsStream("books.json"), writer, StandardCharsets.UTF_8);
            List<Map<String, Object>> items = gson.fromJson(writer.toString(), mapType);
            Map<String, Category> categories = new HashMap<>();
            session.createQuery("FROM Category").list().stream().forEach((Object o) -> {
                Category c = (Category) o;
                categories.put(c.getName(), c);
            });
            Random random = new Random();
            for (Map<String, Object> item : items) {
                String location = String.format("Floor %s, shelf %s", random.nextInt(5) + 1, random.nextInt(16) + 1);
                Book book = new Book((String) item.getOrDefault("isbn", "N/A"), (String) item.get("title"), (String) item.get("author"), (String) item.get("description"), location, new BigDecimal("123.456"), new ArrayList<>());
                if (item.containsKey("isbn")) {
                    book.getCategories().addAll(((List<String>) item.get("categories")).stream().map((String name) -> {
                        if (!categories.containsKey(name)) {
                            Category category = new Category(name, null);
                            session.save(category);
                            categories.put(name, category);
                            return category;
                        } else {
                            return categories.get(name);
                        }
                    }).collect(Collectors.toList()));
                    session.save(book);
                }
            }
            User reader = session.get(User.class, 2);
            for (int i = 1; i <= 3; i++) {
                LendingLogItem item = new LendingLogItem(session.get(Book.class, i), reader);
                item.setStartTime(item.getStartTime().minus(Duration.ofDays(15 * (4 - i))));
                session.save(item);
            }
            for (int i = 2; i <= 5; i++) {
                session.save(new User(String.format("reader%s", i), String.format("reader%s", i), UserType.Reader));
            }
            Reservation reservation = new Reservation(session.get(Book.class, 4), reader);
            reservation.setTime(reservation.getTime().minus(Duration.ofHours(1)));
            session.save(reservation);
            session.save(new NewsItem("News title", "News content", session.get(User.class, 3)));
            session.flush();
            tx.commit();
            session.close();
            pollingThread.start();
        }
    }

    public void run() {
        Connection conn = null;
        try {
            Properties props = new Properties();
            props.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
            if (!Boolean.parseBoolean(props.getProperty("mandarin.exec-init-sql", "false"))) {
                firstTime = false;
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