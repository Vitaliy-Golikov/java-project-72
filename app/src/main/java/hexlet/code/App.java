package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.model.Url;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static Javalin getApp() {
        TemplateEngine templateEngine = createTemplateEngine();

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(templateEngine));
        });

        setupDatabase();

        app.get("/", ctx -> {
            Map<String, Object> flash = new HashMap<>();
            flash.put("flash", ctx.consumeSessionAttribute("flash"));
            flash.put("flashType", ctx.consumeSessionAttribute("flashType"));
            ctx.render("index.jte", Map.of("flash", flash));
        });

        app.get("/urls", ctx -> {
            try {
                var urls = UrlRepository.findAll();
                Map<String, Object> flash = new HashMap<>();
                flash.put("flash", ctx.consumeSessionAttribute("flash"));
                flash.put("flashType", ctx.consumeSessionAttribute("flashType"));
                ctx.render("urls/index.jte", Map.of("urls", urls, "flash", flash));
            } catch (SQLException e) {
                ctx.status(500);
                ctx.result("Database error");
                logger.error("Error fetching URLs", e);
            }
        });

        app.post("/urls", ctx -> {
            String name = ctx.formParam("url");
            if (name == null || name.isEmpty()) {
                ctx.sessionAttribute("flash", "URL не может быть пустым");
                ctx.sessionAttribute("flashType", "danger");
                ctx.redirect("/");
                return;
            }

            try {
                Url url = new Url(name);
                UrlRepository.save(url);
                ctx.sessionAttribute("flash", "Страница успешно добавлена");
                ctx.sessionAttribute("flashType", "success");
                ctx.redirect("/urls");
            } catch (SQLException e) {
                ctx.sessionAttribute("flash", "Ошибка при добавлении URL");
                ctx.sessionAttribute("flashType", "danger");
                ctx.redirect("/");
                logger.error("Error saving URL", e);
            }
        });

        app.get("/urls/{id}", ctx -> {
            Long id = Long.parseLong(ctx.pathParam("id"));
            try {
                var url = UrlRepository.find(id);
                if (url.isPresent()) {
                    Map<String, Object> flash = new HashMap<>();
                    flash.put("flash", ctx.consumeSessionAttribute("flash"));
                    flash.put("flashType", ctx.consumeSessionAttribute("flashType"));
                    ctx.render("urls/show.jte", Map.of("url", url.get(), "flash", flash));
                } else {
                    ctx.status(404);
                    ctx.result("URL not found");
                }
            } catch (SQLException e) {
                ctx.status(500);
                ctx.result("Database error");
                logger.error("Error fetching URL", e);
            }
        });

        return app;
    }

    private static void setupDatabase() {
        try {
            HikariConfig config = new HikariConfig();

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
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (Exception e) {
            logger.error("Failed to read schema.sql", e);
            return null;
        }
    }

    public static void main(String[] args) {
        Javalin app = getApp();
        String portStr = System.getenv().getOrDefault("PORT", "7070");
        int port = Integer.parseInt(portStr);

        app.start(port);
        logger.info("Application started on port {}", port);
    }
}