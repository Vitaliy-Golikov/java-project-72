package hexlet.code.repository;

import hexlet.code.dto.urls.UrlListItem;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;  // ← ДОБАВЛЕН ИМПОРТ

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, url.getName());

            var createdAt = LocalDateTime.now();
            stmt.setTimestamp(2, Timestamp.valueOf(createdAt));

            stmt.executeUpdate();
            var generatedKeys = stmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                url.setId(generatedKeys.getLong(1));
                url.setCreatedAt(createdAt);
            } else {
                throw new SQLException("DB have not returned an id after saving an entity");
            }
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        var sql = "SELECT * FROM urls WHERE id = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                return Optional.of(mapRowToUrl(resultSet));
            }
            return Optional.empty();
        }
    }

    public static Optional<Url> findByName(String name) throws SQLException {
        var sql = "SELECT * FROM urls WHERE name = ?";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(mapRowToUrl(resultSet));
            }
            return Optional.empty();
        }
    }

    public static List<Url> findAll() throws SQLException {
        var sql = "SELECT * FROM urls ORDER BY id DESC";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql);
             var resultSet = stmt.executeQuery()) {
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                result.add(mapRowToUrl(resultSet));
            }
            return result;
        }
    }

    private static Url mapRowToUrl(ResultSet resultSet) throws SQLException {
        var id = resultSet.getLong("id");
        var name = resultSet.getString("name");
        var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();

        var url = new Url(name, createdAt);
        url.setId(id);
        return url;
    }

    public static List<UrlListItem> getAllWithLastChecks() throws SQLException {
        String sql = """
            SELECT u.*,
                   uc.id as check_id,
                   uc.status_code,
                   uc.title,
                   uc.h1,
                   uc.description,
                   uc.created_at as check_created_at
            FROM urls u
            LEFT JOIN url_checks uc ON u.id = uc.url_id
            WHERE uc.id IS NULL
               OR uc.id = (
                   SELECT MAX(id)
                   FROM url_checks
                   WHERE url_id = u.id
               )
            ORDER BY u.id DESC
            """;

        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql);
             var resultSet = stmt.executeQuery()) {
            var items = new ArrayList<UrlListItem>();
            while (resultSet.next()) {
                Url url = mapRowToUrl(resultSet);
                UrlCheck lastCheck = null;
                Long checkId = resultSet.getObject("check_id", Long.class);
                if (checkId != null) {
                    lastCheck = new UrlCheck();
                    lastCheck.setId(checkId);
                    lastCheck.setStatusCode(resultSet.getInt("status_code"));
                    lastCheck.setTitle(resultSet.getString("title"));
                    lastCheck.setH1(resultSet.getString("h1"));
                    lastCheck.setDescription(resultSet.getString("description"));
                    lastCheck.setCreatedAt(resultSet.getTimestamp("check_created_at").toLocalDateTime());
                }
                items.add(UrlListItem.fromUrl(url, lastCheck));
            }
            return items;
        }
    }

    public static void removeAll() throws SQLException {
        String sql = "DELETE FROM urls";
        try (var conn = dataSource.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }
}