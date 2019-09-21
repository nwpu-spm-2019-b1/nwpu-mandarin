package mandarin;

import org.apache.commons.io.IOUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

@Component
public class StartupListener {
    private static boolean firstTime = true;
    private DataSource dataSource;

    public StartupListener(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void run() {
        if (firstTime) {
            Connection conn = null;
            try {
                Properties props = new Properties();
                props.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
                if (!Boolean.parseBoolean(props.getProperty("mandarin.exec-init-sql", "false"))) {
                    return;
                }
                conn = dataSource.getConnection();
                conn.nativeSQL("BEGIN;");
                StringWriter writer = new StringWriter();
                IOUtils.copy(StartupListener.class.getClassLoader().getResourceAsStream("data.sql"), writer, StandardCharsets.UTF_8);
                conn.nativeSQL(writer.toString());
                conn.nativeSQL("COMMIT;");
            } catch (IOException | SQLException e) {
                try {
                    if (conn != null) {
                        conn.rollback();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                throw new RuntimeException(e);
            }
            firstTime = false;
        }
    }
}