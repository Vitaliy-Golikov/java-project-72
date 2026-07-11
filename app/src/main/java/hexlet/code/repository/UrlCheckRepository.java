package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlCheckRepository extends BaseRepository {

    private static final int MAX_LENGTH = 250;

    public static void save(UrlCheck urlCheck) throws SQLException {
        String sql = """
                INSERT INTO url_checks (url_id, status_code, h1, title, description, created_at)
                VALUES (?, ?, ?, ?, ?, ?)""";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            System.out.println("Saving check for URL ID: " + urlCheck.getUrl().getId());
            System.out.println("Status code: " + urlCheck.getStatusCode());
            System.out.println("Title: " + urlCheck.getTitle());
            System.out.println("H1: " + urlCheck.getH1());
            System.out.println("Description: " + urlCheck.getDescription());

            stmt.setLong(1, urlCheck.getUrl().getId());
            stmt.setInt(2, urlCheck.getStatusCode());
            stmt.setString(3, urlCheck.getH1());
            stmt.setString(4, urlCheck.getTitle());
            stmt.setString(5, urlCheck.getDescription());

            var now = LocalDateTime.now();
            stmt.setTimestamp(6, Timestamp.valueOf(now));

            int affectedRows = stmt.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);

            try (var generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    urlCheck.setId(generatedKeys.getLong(1));
                    urlCheck.setCreatedAt(now);
                    System.out.println("Generated ID: " + urlCheck.getId());
                } else {
                    throw new SQLException("DB have not returned an id after saving an entity");
                }
            }
        }
    }

    public static List<UrlCheck> findByUrlId(Long urlId) throws SQLException {
        String sql = """
            SELECT * FROM url_checks
            WHERE url_id = ?
            ORDER BY ID DESC
            """;

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, urlId);
            var resultSet = stmt.executeQuery();

            var urlChecks = new ArrayList<UrlCheck>();
            while (resultSet.next()) {
                urlChecks.add(mapRowToUrlCheck(resultSet, urlId));
            }
            System.out.println("Found " + urlChecks.size() + " checks for URL ID: " + urlId);
            return urlChecks;
        }
    }

    public static Optional<UrlCheck> findLatestByUrlId(Long urlId) throws SQLException {
        String sql = """
            SELECT * FROM url_checks
            WHERE url_id = ?
            ORDER BY created_at DESC
            LIMIT 1
            """;

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, urlId);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapRowToUrlCheck(resultSet, urlId));
            }
            return Optional.empty();
        }
    }

    private static UrlCheck mapRowToUrlCheck(ResultSet rs, Long urlId) throws SQLException {
        Url url = new Url("");
        url.setId(urlId);

        UrlCheck check = new UrlCheck(
                rs.getInt("status_code"),
                url
        );

        check.setId(rs.getLong("id"));
        check.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        check.setTitle(rs.getString("title"));
        check.setH1(rs.getString("h1"));
        check.setDescription(rs.getString("description"));

        System.out.println("Mapped check: ID=" + check.getId() +
                ", Status=" + check.getStatusCode() +
                ", Title=" + check.getTitle() +
                ", H1=" + check.getH1());

        return check;
    }

    public static void removeAll() throws SQLException {
        String sql = "DELETE FROM url_checks";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    private static String truncate(String value) {
        if (value == null) {
            return null;
        }
        System.out.println("=== truncate() ===");
        System.out.println("Input length: " + value.length());
        System.out.println("Input: " + value);
        if (value.length() <= MAX_LENGTH) {
            System.out.println("No truncation needed");
            return value;
        }
        String result = value.substring(0, MAX_LENGTH - 3) + "...";
        System.out.println("Result length: " + result.length());
        System.out.println("Result: " + result);
        System.out.println("Result ends with ...: " + result.endsWith("..."));
        return result;
    }
}