package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import io.javalin.Javalin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
import java.util.stream.Collectors;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        });

        // Инициализация базы данных
        setupDatabase();

        app.get("/", ctx -> {
            ctx.result("Hello World");
        });

        return app;
    }

    private static void setupDatabase() {
        try {
            HikariConfig config = new HikariConfig();

            // Получаем URL из переменной окружения или используем H2
            String jdbcUrl = System.getenv("JDBC_DATABASE_URL");
            if (jdbcUrl == null || jdbcUrl.isEmpty()) {
                jdbcUrl = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";
                config.setDriverClassName("org.h2.Driver");
            } else {
                config.setDriverClassName("org.postgresql.Driver");
            }

            config.setJdbcUrl(jdbcUrl);
            config.setMaximumPoolSize(10);
            config.setMinimumIdle(2);

            HikariDataSource dataSource = new HikariDataSource(config);
            BaseRepository.dataSource = dataSource;

            // Инициализация схемы
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                String schema = readSchemaFile();
                if (schema != null && !schema.isEmpty()) {
                    stmt.execute(schema);
                }
            }

            logger.info("Database connection established");
        } catch (Exception e) {
            logger.error("Database setup failed", e);
            throw new RuntimeException("Database setup failed", e);
        }
    }

    private static String readSchemaFile() {
        try (InputStream inputStream = App.class.getClassLoader()
                .getResourceAsStream("schema.sql")) {
            if (inputStream == null) {
                return null;
            }
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            logger.error("Failed to read schema.sql", e);
            return null;
        }
    }

    public static void main(String[] args) {
        Javalin app = getApp();

        // Порт из переменной окружения или 7070 по умолчанию
        String portStr = System.getenv().getOrDefault("PORT", "7070");
        int port = Integer.parseInt(portStr);

        app.start(port);
        logger.info("Application started on port {}", port);
    }
}